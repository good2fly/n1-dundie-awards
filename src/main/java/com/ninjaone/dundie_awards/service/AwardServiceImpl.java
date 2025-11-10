package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.config.RabbitMqConfig;
import com.ninjaone.dundie_awards.event.DundieAwardGranted;
import com.ninjaone.dundie_awards.pubsub.MessageProducer;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AwardServiceImpl implements AwardService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ApplicationEventPublisher publisher;
    private final MessageProducer messageProducer;
    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;

    public AwardServiceImpl(ApplicationEventPublisher publisher,
                            MessageProducer messageProducer,
                            EmployeeRepository employeeRepository,
                            OrganizationRepository organizationRepository) {
        this.publisher = publisher;
        this.messageProducer = messageProducer;
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    @Override
    public void giveDundieAwards(long organizationId) {

        logger.info("giveDundieAwards: transactional? {}", TransactionSynchronizationManager.isActualTransactionActive());

        // make sure org exists
        if (!organizationRepository.existsById(organizationId))
                throw new EntityNotFoundException("Organization with ID=" + organizationId + " not found");

        // These 2 operations can be turned into 1 (by using 'RETURNING') if we were on PostgreSQL
        Set<Long> affectedEmpIds = employeeRepository.findAllIdsByOrganizationId(organizationId);
        int count = employeeRepository.incrementAwardCount(affectedEmpIds);
        logger.info("Gave dundie awards to {} employees for organization with ID={} successfully; publishing event..", count, organizationId);

        // Alternative is to use manual transactions + TransactionSynchronizationManager.registerSynchronization
        publisher.publishEvent(new DundieAwardGranted(organizationId, LocalDateTime.now(), affectedEmpIds));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void giveDundieAwardsAfterCommit(DundieAwardGranted event) {

        logger.info("giveDundieAwardsAfterCommit: transactional? {}", TransactionSynchronizationManager.isActualTransactionActive());
        logger.info("Gave dundie awards after commit: sending event {}", event);
        messageProducer.sendMessage(RabbitMqConfig.ROUTING_KEY, event);
    }

}
