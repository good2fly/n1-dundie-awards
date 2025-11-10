package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.request.EmployeeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, OrganizationRepository organizationRepository) {
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Employee> findById(long id) {
        return employeeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional
    @Override
    public Employee create(EmployeeRequest employeeRequest) {

        Organization organization = organizationRepository.findById(employeeRequest.organizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization by ID=" + employeeRequest.organizationId() + " not found"));
        Employee employee = new Employee(employeeRequest.firstName(), employeeRequest.lastName(), organization);
        return employeeRepository.save(employee);
    }

    @Transactional
    @Override
    public Optional<Employee> update(long id, EmployeeRequest employeeRequest) {
        return employeeRepository.findById(id)
                .map(emp -> {
                    emp.setFirstName(employeeRequest.firstName());
                    emp.setLastName(employeeRequest.lastName());
                    return emp;
                });
    }

    @Transactional
    @Override
    public boolean deleteById(long id) {

        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        optionalEmployee.ifPresentOrElse(employeeRepository::delete,
                                        () -> logger.error("deleteById: employee with ID={} not found", id));
        return optionalEmployee.isPresent();
    }
}
