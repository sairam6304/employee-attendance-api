# Employee Attendance API

This is a Spring Boot REST API for managing employee attendance.
You can check in, check out, add attendance records, and fetch attendance history.

---

## Features

- Check in an employee
- Check out an employee
- Add attendance record manually (POST)
- View attendance history by employee
- View attendance by date

---

## Endpoints

| Method | URL                                     | Description                          |
|--------|-----------------------------------------|------------------------------------|
| POST   | `/api/attendance/checkin/{employeeId}`  | Employee check-in                   |
| POST   | `/api/attendance/checkout/{employeeId}` | Employee check-out                  |
| POST   | `/api/attendance`                       | Add attendance record (manual)     |
| GET    | `/api/attendance/history/{employeeId}` | Get attendance history for employee|
| GET    | `/api/attendance/daily/{date}`          | Get attendance for all on a date   |

---

## Example Usage

### Check-in

```bash
POST http://localhost:8081/api/attendance/checkin/1
