package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.service.AwardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AwardController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AwardService awardService;

    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    // TODO I'd rather pick a more REST-oriented endpont, like /dundieawards/{organizationId}
    @PostMapping("/give-dundie-awards/{organizationId}")
    public ResponseEntity<Void> giveDundieAwards(@PathVariable long organizationId) {
        logger.info("giveDundieAwards: transactional? {}", TransactionSynchronizationManager.isActualTransactionActive());
        awardService.giveDundieAwards(organizationId);
        return ResponseEntity.accepted().build(); // Return 202 Accepted
    }
}