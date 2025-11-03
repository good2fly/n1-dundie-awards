package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<Employee> findById(long id);

    List<Employee> findAllEmployees();

    Employee create(Employee employee);

    Optional<Employee> update(long id, Employee employee);

    boolean deleteById(long id);
}
