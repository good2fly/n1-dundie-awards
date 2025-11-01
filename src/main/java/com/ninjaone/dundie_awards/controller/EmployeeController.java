package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping()
public class EmployeeController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // get all employees
    @GetMapping("/employees")
    @ResponseBody
    public List<Employee> getAllEmployees() {
        logger.debug("getAllEmployees: invoked");
        logger.info("getAllEmployees: transactional? {}", TransactionSynchronizationManager.isActualTransactionActive());
        return employeeRepository.findAll();
    }

    // create employee rest api
    @PostMapping("/employees")
    @ResponseBody
    public Employee createEmployee(@RequestBody Employee employee) {
        logger.debug("createEmployee: invoked with request: {}", employee);
        logger.debug("createEmployee: transactional? {}", TransactionSynchronizationManager.isActualTransactionActive());
        Employee saved = employeeRepository.save(employee);
        logger.debug("createEmployee: saved={}", saved);
        return saved;
    }

    // get employee by id rest api
    @GetMapping("/employees/{id}")
    @ResponseBody
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        logger.debug("getEmployeeById: invoked with id: {}", id);
        logger.debug("getEmployeeById: transactional? {}", TransactionSynchronizationManager.isActualTransactionActive());
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            return ResponseEntity.ok(optionalEmployee.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // update employee rest api
    @PutMapping("/employees/{id}")
    @ResponseBody
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        logger.debug("updateEmployee: invoked with id: {}, details: {}", id, employeeDetails);
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (!optionalEmployee.isPresent()) {
            logger.error("updateEmployee: updateEmployee with ID={} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Employee employee = optionalEmployee.get();
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());

        Employee updatedEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(updatedEmployee);
    }

    // delete employee rest api
    @DeleteMapping("/employees/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id) {
        logger.debug("deleteEmployee: invoked with id: {}", id);
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (!optionalEmployee.isPresent()) {
            logger.error("updateEmployee: deleteEmployee with ID={} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Employee employee = optionalEmployee.get();
        employeeRepository.delete(employee);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
