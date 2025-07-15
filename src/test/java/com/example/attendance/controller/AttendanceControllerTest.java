package com.example.attendance.controller;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Employee;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.kafka.KafkaProducerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceControllerTest {

    @InjectMocks
    private AttendanceController attendanceController;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    private Employee employee;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee();
        employee.setId(123L);
        // initialize other employee fields if needed
    }

    @Test
    void testGetAll() {
        List<Attendance> mockList = new ArrayList<>();
        mockList.add(new Attendance());
        when(attendanceRepository.findAll()).thenReturn(mockList);

        List<Attendance> result = attendanceController.getAll();

        assertEquals(1, result.size());
        verify(attendanceRepository, times(1)).findAll();
    }

    @Test
    void testMarkAttendance_WithNoCheckInTime_SetsCurrentTimeAndSendsKafka() {
        Attendance input = new Attendance();
        input.setEmployee(employee);

        when(attendanceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Attendance> response = attendanceController.markAttendance(input);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Attendance savedAttendance = response.getBody();
        assertNotNull(savedAttendance);
        assertNotNull(savedAttendance.getCheckInTime());
        assertEquals(employee, savedAttendance.getEmployee());

        verify(attendanceRepository, times(1)).save(any());
        verify(kafkaProducerService, times(1)).sendMessage(
                contains("Marked attendance: EmployeeID=" + employee.getId())
        );
    }

    @Test
    void testGetById_Found() {
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setCheckInTime(LocalDateTime.now());

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));

        ResponseEntity<Attendance> response = attendanceController.getById(1L);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(attendance, response.getBody());
    }

    @Test
    void testGetById_NotFound() {
        when(attendanceRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Attendance> response = attendanceController.getById(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateAttendance_Found_UpdatesAndSendsKafka() {
        Attendance existing = new Attendance();
        existing.setEmployee(employee);
        existing.setDate(LocalDate.now());
        existing.setCheckInTime(LocalDateTime.now().minusHours(1));
        existing.setCheckOutTime(LocalDateTime.now());
        existing.setHoursWorked(8.0);

        Attendance updated = new Attendance();
        updated.setEmployee(employee);
        updated.setDate(LocalDate.now().plusDays(1));
        updated.setCheckInTime(LocalDateTime.now().minusHours(2));
        updated.setCheckOutTime(LocalDateTime.now().plusHours(1));
        updated.setHoursWorked(9.0);

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(attendanceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Attendance> response = attendanceController.updateAttendance(1L, updated);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        Attendance result = response.getBody();
        assertNotNull(result);
        assertEquals(updated.getDate(), result.getDate());
        assertEquals(updated.getHoursWorked(), result.getHoursWorked());

        verify(attendanceRepository, times(1)).save(existing);
        verify(kafkaProducerService, times(1)).sendMessage(
                "Updated attendance: EmployeeID=" + employee.getId()
        );
    }

    @Test
    void testUpdateAttendance_NotFound() {
        when(attendanceRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Attendance> response = attendanceController.updateAttendance(1L, new Attendance());

        assertEquals(404, response.getStatusCodeValue());
        verify(attendanceRepository, never()).save(any());
        verify(kafkaProducerService, never()).sendMessage(anyString());
    }

    @Test
    void testDeleteAttendance_Found_DeletesAndSendsKafka() {
        when(attendanceRepository.existsById(1L)).thenReturn(true);
        doNothing().when(attendanceRepository).deleteById(1L);

        ResponseEntity<Void> response = attendanceController.deleteAttendance(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(attendanceRepository, times(1)).deleteById(1L);
        verify(kafkaProducerService, times(1)).sendMessage("Deleted attendance record with ID=1");
    }

    @Test
    void testDeleteAttendance_NotFound() {
        when(attendanceRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = attendanceController.deleteAttendance(1L);

        assertEquals(404, response.getStatusCodeValue());
        verify(attendanceRepository, never()).deleteById(anyLong());
        verify(kafkaProducerService, never()).sendMessage(anyString());
    }
}
