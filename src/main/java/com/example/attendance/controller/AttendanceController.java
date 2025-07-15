package com.example.attendance.controller;

import com.example.attendance.kafka.KafkaProducerService;
import com.example.attendance.model.Attendance;
import com.example.attendance.repository.AttendanceRepository;
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
    private KafkaProducerService kafkaProducerService;

    // GET all attendance records
    @GetMapping
    public List<Attendance> getAll() {
        return attendanceRepository.findAll();
    }

    // POST: Mark attendance and send Kafka message
    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(@RequestBody Attendance attendance) {
        // Set timestamp if not provided
        if (attendance.getCheckInTime() == null) {
            attendance.setCheckInTime(LocalDateTime.now());
        }


        // Save to DB
        Attendance saved = attendanceRepository.save(attendance);

        // Send Kafka message
        String kafkaMessage = String.format("Marked attendance: EmployeeID=%s at %s",
                saved.getEmployee().getId(), // or getName() if available
                saved.getCheckInTime());

        kafkaProducerService.sendMessage(kafkaMessage);

        return ResponseEntity.ok(saved);
    }

    // GET one attendance by ID
    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getById(@PathVariable Long id) {
        Optional<Attendance> found = attendanceRepository.findById(id);
        return found.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: Update attendance
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


    // DELETE: Remove attendance by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        if (!attendanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        attendanceRepository.deleteById(id);

        // Kafka delete log (optional)
        kafkaProducerService.sendMessage("Deleted attendance record with ID=" + id);

        return ResponseEntity.noContent().build();
    }
}
