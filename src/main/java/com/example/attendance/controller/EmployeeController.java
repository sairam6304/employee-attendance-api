package com.example.attendance.controller;

import com.example.attendance.model.Employee;
import com.example.attendance.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository repo;

    public EmployeeController(EmployeeRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Employee addEmployee(@RequestBody Employee e) {
        return repo.save(e);
    }

    @GetMapping
    public List<Employee> getAll() {
        return repo.findAll();
    }
}
