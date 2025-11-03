package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Optional<Employee> findById(long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee create(Employee employee) {
        if (employee.getId() != null) {
            throw new IllegalArgumentException("Cannot create an employee that already has an ID=" + employee.getId());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> update(long id, Employee employee) {
        return employeeRepository.findById(employee.getId())
                .map(emp -> {
                    emp.setFirstName(employee.getFirstName());
                    emp.setLastName(employee.getLastName());
                    return emp;
                });
    }

    @Override
    public boolean deleteById(long id) {

        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        optionalEmployee.ifPresentOrElse(employeeRepository::delete,
                                        () -> logger.error("deleteById: employee with ID={} not found", id));
        return optionalEmployee.isPresent();
    }
}
