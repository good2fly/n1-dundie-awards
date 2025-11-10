package com.ninjaone.dundie_awards.repository;

import com.ninjaone.dundie_awards.model.DundieAwardEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DundieAwardEventRepository extends JpaRepository<DundieAwardEvent, String> {
}
