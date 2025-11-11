package com.ninjaone.dundie_awards.repository;

import com.ninjaone.dundie_awards.model.DundieAwardEvent;
import com.ninjaone.dundie_awards.model.DundieAwardRollbackEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DundieAwardRollbackEventRepository extends JpaRepository<DundieAwardRollbackEvent, Long> {
}
