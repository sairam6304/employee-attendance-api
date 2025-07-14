package com.example.attendance.repository;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Employee;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDate;
import java.util.*;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByEmployee(Employee employee);
}
