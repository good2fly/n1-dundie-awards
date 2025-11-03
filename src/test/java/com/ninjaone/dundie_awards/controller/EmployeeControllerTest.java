package com.ninjaone.dundie_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.request.EmployeeRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = organizationRepository.save(new Organization("Kattegat"));
    }

    @Test
    void getAllEmployees_returnsPersistedEmployees() throws Exception {
        employeeRepository.save(new Employee("Ragnar", "Lothbrok", organization));
        employeeRepository.save(new Employee("Floki", "Vilgerðarson", organization));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(9))) // 2 test + app default data
                .andExpect(jsonPath("$[*].firstName", Matchers.hasItem("Ragnar")))
                .andExpect(jsonPath("$[*].firstName", Matchers.hasItem("Floki")))
                .andExpect(jsonPath("$[*].lastName", Matchers.hasItem("Lothbrok")))
                .andExpect(jsonPath("$[*].lastName", Matchers.hasItem("Vilgerðarson")));
    }

    @Test
    void getEmployeeById_returnsEmployeeWhenPresent() throws Exception {
        Employee employee = employeeRepository.save(new Employee("Earl", "Haraldson", organization));

        mockMvc.perform(get("/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employee.getId()))
                .andExpect(jsonPath("$.firstName").value("Earl"))
                .andExpect(jsonPath("$.lastName").value("Haraldson"))
                .andExpect(jsonPath("$.dundieAwards").value(0))
                .andExpect(jsonPath("$.orgId").value(organization.getId()))
                .andExpect(jsonPath("$.orgName").value(organization.getName()));
    }

    @Test
    void getEmployeeById_returns404WhenMissing() throws Exception {
        mockMvc.perform(get("/employees/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployee_persistsAndReturnsEmployee() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Dwight", "Schrute", organization.getId());

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Dwight"))
                .andExpect(jsonPath("$.lastName").value("Schrute"))
                .andExpect(jsonPath("$.dundieAwards").value(0));

        boolean exists = employeeRepository.findAll().stream()
                .anyMatch(emp -> "Dwight".equals(emp.getFirstName()) && "Schrute".equals(emp.getLastName()));
        assertThat(exists).isTrue();
    }

    @Test
    void createEmployee_returns400ForValidationErrors() throws Exception {
        EmployeeRequest invalid = new EmployeeRequest("Kelly", "", organization.getId());

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmployee_updatesExistingEmployee() throws Exception {
        Employee existing = employeeRepository.save(new Employee("Rollo", "Lothbrok", organization));
        EmployeeRequest request = new EmployeeRequest("Yolo", "Longstep", organization.getId());

        mockMvc.perform(put("/employees/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Yolo"))
                .andExpect(jsonPath("$.lastName").value("Longstep"));
    }

    @Test
    void updateEmployee_returns404WhenMissing() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Ryan", "Howard", organization.getId());

        mockMvc.perform(put("/employees/{id}", 4242L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEmployee_removesAndReturnsConfirmation() throws Exception {
        Employee existing = employeeRepository.save(new Employee("King", "Eckbert", organization));

        mockMvc.perform(delete("/employees/{id}", existing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));

        assertThat(employeeRepository.findById(existing.getId())).isEmpty();
    }

    @Test
    void deleteEmployee_returns404WhenMissing() throws Exception {
        mockMvc.perform(delete("/employees/{id}", 5150L))
                .andExpect(status().isNotFound());
    }
}
