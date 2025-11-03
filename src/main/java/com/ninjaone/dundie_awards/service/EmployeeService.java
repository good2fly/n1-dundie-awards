package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.request.EmployeeRequest;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<Employee> findById(long id);

    List<Employee> findAllEmployees();

    Employee create(EmployeeRequest employee);

    Optional<Employee> update(long id, EmployeeRequest employee);

    boolean deleteById(long id);
}
