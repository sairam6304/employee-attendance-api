package com.example.attendance.controller;

import com.example.attendance.model.Employee;
import com.example.attendance.repository.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @Mock
    EmployeeRepository repo;

    @InjectMocks
    EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEmployee() {
        Employee employee = new Employee();
        when(repo.save(any(Employee.class))).thenReturn(employee);

        Employee result = employeeController.addEmployee(new Employee());
        Assertions.assertEquals(employee, result); // compare same instance
    }

    @Test
    void testGetAll() {
        Employee employee = new Employee();
        when(repo.findAll()).thenReturn(List.of(employee));

        List<Employee> result = employeeController.getAll();
        Assertions.assertEquals(List.of(employee), result); // compare same instance
    }
}
