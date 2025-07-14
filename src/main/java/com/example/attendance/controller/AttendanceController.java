package com.example.attendance.controller;

import com.example.attendance.model.*;
import com.example.attendance.repository.*;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository attendanceRepo;
    private final EmployeeRepository employeeRepo;

    public AttendanceController(AttendanceRepository attendanceRepo, EmployeeRepository employeeRepo) {
        this.attendanceRepo = attendanceRepo;
        this.employeeRepo = employeeRepo;
    }

    @PostMapping("/checkin/{employeeId}")
    public String checkIn(@PathVariable Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId).orElseThrow();
        Attendance att = new Attendance();
        att.setEmployee(employee);
        att.setDate(LocalDate.now());
        att.setCheckInTime(LocalDateTime.now());
        attendanceRepo.save(att);
        return "Checked in";
    }

    @PostMapping("/checkout/{employeeId}")
    public String checkOut(@PathVariable Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId).orElseThrow();
        List<Attendance> list = attendanceRepo.findByEmployee(employee);
        Attendance att = list.stream().filter(a -> a.getDate().equals(LocalDate.now())).findFirst().orElseThrow();
        att.setCheckOutTime(LocalDateTime.now());
        Duration duration = Duration.between(att.getCheckInTime(), att.getCheckOutTime());
        att.setHoursWorked(duration.toMinutes() / 60.0);
        attendanceRepo.save(att);
        return "Checked out";
    }

    @GetMapping("/daily/{date}")
    public List<Attendance> getDaily(@PathVariable String date) {
        return attendanceRepo.findByDate(LocalDate.parse(date));
    }

    @GetMapping("/history/{employeeId}")
    public List<Attendance> getHistory(@PathVariable Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId).orElseThrow();
        return attendanceRepo.findByEmployee(employee);
    }
    @PostMapping
    public Attendance createAttendance(@RequestBody Attendance attendance) {
        return attendanceRepo.save(attendance);
    }

}
