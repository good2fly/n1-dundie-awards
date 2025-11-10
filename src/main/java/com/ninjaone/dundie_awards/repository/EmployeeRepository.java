package com.ninjaone.dundie_awards.repository;

import com.ninjaone.dundie_awards.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Modifying
    @Query(nativeQuery = true,
           value = """
            UPDATE employees
            SET dundie_awards = dundie_awards + 1,
                updated_at = NOW()
            WHERE organization_id = :organizationId
            RETURNING id
    """)
    List<Long> incrementAwardCountByOrgId(@Param("organizationId") long organizationId);

    @Modifying(clearAutomatically = true, flushAutomatically = false)
    @Query("""
        UPDATE Employee e
        SET e.dundieAwards = e.dundieAwards + 1
        WHERE e.id in (:empIds)
    """)
    int incrementAwardCount(@Param("empIds") Collection<Long> empIds);

    @Query("""
        SELECT e.id
        FROM Employee e
        WHERE e.organization.id = :organizationId
    """)
    Set<Long> findAllIdsByOrganizationId(@Param("organizationId") long organizationId);

//    // We'll need this for the rollback
//    @Modifying
//    @Query("UPDATE Employee e SET e.dundieAwards = e.dundieAwards - 1 WHERE e.organization.id = :organizationId AND e.dundieAwards > 0")
//    int decrementDundieAwardsByOrganization(@Param("organizationId") Long organizationId);

}
