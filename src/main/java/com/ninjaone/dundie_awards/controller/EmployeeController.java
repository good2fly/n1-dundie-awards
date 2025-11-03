package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.request.EmployeeRequest;
import com.ninjaone.dundie_awards.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        logger.debug("getAllEmployees: invoked");
        // TODO would be better to sort by last name (or something) to make response deterministic
        List<Employee> employees = employeeService.findAllEmployees();
        return ResponseEntity.ok(employees.stream().map(this::toDto).toList());
    }

    // create employee rest api
    @Transactional
    @PostMapping("/employees")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody @Valid EmployeeRequest employee) {
        logger.debug("createEmployee: invoked with request: {}", employee);
        Employee created = employeeService.create(employee);
        return ResponseEntity.ok(toDto(created));
    }

    // get employee by id rest api
    @Transactional(readOnly = true)
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable long id) {
        logger.debug("getEmployeeById: invoked with id: {}", id);
        Optional<Employee> optionalEmployee = employeeService.findById(id);
        return optionalEmployee.map(employee -> ResponseEntity.ok(toDto(employee)))
                               .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // update employee rest api
    @Transactional
    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable long id, @RequestBody @Valid EmployeeRequest employeeDetails) {
        logger.debug("updateEmployee: invoked with id: {}, details: {}", id, employeeDetails);
        Optional<Employee> optionalEmployee = employeeService.update(id, employeeDetails);
        return optionalEmployee.map(employee -> ResponseEntity.ok(toDto(employee)))
                               .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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

    // TODO Use a mapping framework, like MapStruct, in a real project.
    private EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getDundieAwards(),
                               employee.getOrganization().getId(), employee.getOrganization().getName());
    }
}
