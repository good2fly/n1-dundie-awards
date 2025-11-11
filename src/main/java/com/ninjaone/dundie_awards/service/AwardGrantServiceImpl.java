package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.event.DundieAwardGranted;
import com.ninjaone.dundie_awards.model.DundieAwardRollbackEvent;
import com.ninjaone.dundie_awards.repository.DundieAwardRollbackEventRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AwardGrantServiceImpl implements AwardGrantService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ApplicationEventPublisher publisher;
    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;
    private final DundieAwardRollbackEventRepository dundieAwardRollbackEventRepository;

    public AwardGrantServiceImpl(ApplicationEventPublisher publisher,
                                 EmployeeRepository employeeRepository,
                                 OrganizationRepository organizationRepository,
                                 DundieAwardRollbackEventRepository dundieAwardRollbackEventRepository) {
        this.publisher = publisher;
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
        this.dundieAwardRollbackEventRepository = dundieAwardRollbackEventRepository;
    }

    @Transactional
    @Override
    public void giveDundieAwards(long organizationId) {

        logger.debug("giveDundieAwards: transactional? {}", TransactionSynchronizationManager.isActualTransactionActive()); // TODO remove later

        // make sure org exists
        if (!organizationRepository.existsById(organizationId))
                throw new EntityNotFoundException("Organization with ID=" + organizationId + " not found");

        // These 2 operations can be turned into 1 (by using 'RETURNING') if we were on PostgreSQL
        Set<Long> affectedEmpIds = employeeRepository.findAllIdsByOrganizationId(organizationId);
        int count = employeeRepository.addToAwardCount(affectedEmpIds, 1);
        if (count > 0) {
            logger.info("Gave dundie awards to {} employees for organization with ID={} successfully; publishing event..", count, organizationId);
            // Alternative is to use manual transactions + TransactionSynchronizationManager.registerSynchronization
            // Note, that there's still a tiny chance that the app crashes (e.g. pod restart) between the transaction's end and handling the event -> use outbox pattern
            publisher.publishEvent(new DundieAwardGranted(organizationId, LocalDateTime.now(), affectedEmpIds));
        } else {
            logger.warn("No employees found for organization with ID={}", organizationId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void rollbackActivityForAwardGranted(DundieAwardGranted event) {

        logger.debug("rollbackActivityForAwardGranted: transactional? {}", TransactionSynchronizationManager.isActualTransactionActive()); // TODO remove later
        logger.info("rollbackActivityForAwardGranted: rolling back award due to failed activity processing: {}", event);

        // Prevent duplicate execution by storing the idempotency key in a unique column
        DundieAwardRollbackEvent rollbackEvent = new DundieAwardRollbackEvent(event.idempotencyKey()); // prevent duplicate execution
        dundieAwardRollbackEventRepository.save(rollbackEvent);
        // TODO Add cleanup for these de-dupe records (e.g. scheduled task). In a real system, it may be better to use Redis for this as it can expire them automatically.

        employeeRepository.addToAwardCount(event.affectedEmpIds(), -1);
    }

}
