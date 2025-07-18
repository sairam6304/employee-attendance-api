package com.example.attendance.controller;

import com.example.attendance.kafka.KafkaProducerService;
import com.example.attendance.model.Attendance;
import com.example.attendance.model.Employee;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @GetMapping
    public List<Attendance> getAll() {
        return attendanceRepository.findAll();
    }

    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(@RequestBody Attendance attendance) {
        if (attendance.getEmployee() == null || attendance.getEmployee().getId() == null) {
            return ResponseEntity.badRequest().build();  // handle missing employee gracefully
        }

        if (attendance.getCheckInTime() == null) {
            attendance.setCheckInTime(LocalDateTime.now());
        }

        // Fetch full Employee from DB - fix injection below
        Employee employee = employeeRepository.findById(attendance.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        attendance.setEmployee(employee);

        Attendance saved = attendanceRepository.save(attendance);

        kafkaProducerService.sendMessage(
                "Marked attendance: EmployeeID=" + saved.getEmployee().getId() + " at " + saved.getCheckInTime()
        );

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getById(@PathVariable Long id) {
        Optional<Attendance> attendance = attendanceRepository.findById(id);
        return attendance.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Long id,
                                                       @RequestBody Attendance updated) {
        return attendanceRepository.findById(id)
                .map(existing -> {
                    existing.setEmployee(updated.getEmployee());
                    existing.setDate(updated.getDate());
                    existing.setCheckInTime(updated.getCheckInTime());
                    existing.setCheckOutTime(updated.getCheckOutTime());
                    existing.setHoursWorked(updated.getHoursWorked());

                    Attendance saved = attendanceRepository.save(existing);

                    kafkaProducerService.sendMessage(
                            "Updated attendance: EmployeeID=" + saved.getEmployee().getId()
                    );

                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Attendance> createAttendance(@RequestBody Attendance attendance) {
        if (attendance.getCheckInTime() == null) {
            attendance.setCheckInTime(LocalDateTime.now());
        }

        Attendance saved = attendanceRepository.save(attendance);

        kafkaProducerService.sendMessage(
                "Created attendance: EmployeeID=" +
                        (saved.getEmployee() != null ? saved.getEmployee().getId() : "N/A") +
                        " at " + saved.getCheckInTime()
        );

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        if (!attendanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        attendanceRepository.deleteById(id);

        kafkaProducerService.sendMessage("Deleted attendance record with ID=" + id);

        return ResponseEntity.noContent().build();
    }
}
