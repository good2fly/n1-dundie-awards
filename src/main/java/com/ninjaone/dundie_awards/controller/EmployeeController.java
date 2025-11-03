package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class EmployeeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // get all employees
    @Transactional(readOnly = true)
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.debug("getAllEmployees: invoked");
        return ResponseEntity.ok(employeeService.findAllEmployees());
    }

    // create employee rest api
    @Transactional
    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        logger.debug("createEmployee: invoked with request: {}", employee);
        Employee created = employeeService.create(employee);
        return ResponseEntity.ok(created);
    }

    // get employee by id rest api
    @Transactional(readOnly = true)
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable long id) {
        logger.debug("getEmployeeById: invoked with id: {}", id);
        Optional<Employee> optionalEmployee = employeeService.findById(id);
        if (optionalEmployee.isPresent()) {
            return ResponseEntity.ok(optionalEmployee.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // update employee rest api
    @Transactional
    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @RequestBody Employee employeeDetails) {
        logger.debug("updateEmployee: invoked with id: {}, details: {}", id, employeeDetails);
        Optional<Employee> optionalEmployee = employeeService.update(id, employeeDetails);
        if (optionalEmployee.isEmpty()) {
            logger.error("updateEmployee: updateEmployee with ID={} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(optionalEmployee.get());
    }

    // delete employee rest api
    @Transactional
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable long id) {
        logger.debug("deleteEmployee: invoked with id: {}", id);

        boolean deleted = employeeService.deleteById(id);

        if (deleted) {
            Map<String, Boolean> response = Map.of("deleted", true);
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
