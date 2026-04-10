# Student Learning Management System

This project is a Spring Boot implementation of the OOAD Student Learning Management System specification.

## Features

- Browser homepage rendered with Spring Boot Thymeleaf
- User registration and login for students, instructors, and administrators
- Course management and instructor assignment
- Module and material management
- Assignment creation, submission, grading, and feedback
- Student progress tracking and upcoming deadline notifications
- Administrator performance report generation

## Tech Stack

- Java 17
- Spring Boot 3
- Maven
- H2 database (backend persistence)

## Run the Project

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

Open `http://localhost:8080/` for the HTML homepage.

## Seeded Users

- Admin: `admin@lms.com` / `admin123`
- Instructor: `instructor@lms.com` / `teach123`
- Student: `student@lms.com` / `learn123`

## Main API Endpoints

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/login`

### Student

- `GET /api/students/courses`
- `POST /api/students/{studentId}/courses/{courseId}/enroll`
- `POST /api/students/{studentId}/assignments/{assignmentId}/submit`
- `GET /api/students/{studentId}/grades`
- `GET /api/students/{studentId}/progress`
- `GET /api/students/{studentId}/notifications`
- `GET /api/students/{studentId}/materials/{materialId}/download`

### Instructor

- `POST /api/instructor/courses/{courseId}/modules?instructorId={id}`
- `POST /api/instructor/courses/{courseId}/modules/{moduleId}/materials?instructorId={id}`
- `POST /api/instructor/courses/{courseId}/modules/{moduleId}/materials/pdf?instructorId={id}` (multipart form-data: `name`, `file`)
- `POST /api/instructor/courses/{courseId}/assignments?instructorId={id}`
- `GET /api/instructor/courses/{courseId}/assignments/{assignmentId}/submissions?instructorId={id}`
- `POST /api/instructor/courses/{courseId}/submissions/{submissionId}/grade?instructorId={id}`

### Administrator

- `GET /api/admin/users?adminId={id}`
- `POST /api/admin/courses?adminId={id}`
- `PUT /api/admin/courses/{courseId}?adminId={id}`
- `DELETE /api/admin/courses/{courseId}?adminId={id}`
- `POST /api/admin/courses/{courseId}/assign-instructor/{instructorId}?adminId={id}`
- `GET /api/admin/reports/performance?adminId={id}`

## Notes

- Core backend data is persisted in H2.
- Uploaded PDFs are stored on disk, and their metadata is stored in H2.
- The structure can be migrated to MySQL/PostgreSQL later if needed.