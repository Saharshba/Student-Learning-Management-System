package com.ooad.lms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ooad.lms.dto.CreateAssignmentRequest;
import com.ooad.lms.dto.CreateCourseRequest;
import com.ooad.lms.dto.CreateExamRequest;
import com.ooad.lms.dto.CreateModuleRequest;
import com.ooad.lms.dto.UploadMaterialRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.Exam;
import com.ooad.lms.model.Material;
import com.ooad.lms.model.MaterialFileMetadata;
import com.ooad.lms.model.MaterialType;
import com.ooad.lms.model.Module;
import com.ooad.lms.model.Role;
import com.ooad.lms.repository.AssignmentRepository;
import com.ooad.lms.repository.CourseRepository;
import com.ooad.lms.repository.MaterialFileMetadataRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final MaterialFileMetadataRepository materialFileMetadataRepository;

    public CourseService(
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository,
            UserService userService,
            FileStorageService fileStorageService,
            MaterialFileMetadataRepository materialFileMetadataRepository
    ) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.materialFileMetadataRepository = materialFileMetadataRepository;
    }

    public Course createCourse(CreateCourseRequest request) {
        return courseRepository.save(new Course(null, request.title(), request.description()));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    public Course updateCourse(Long courseId, CreateCourseRequest request) {
        Course course = getCourse(courseId);
        course.setTitle(request.title());
        course.setDescription(request.description());
        return courseRepository.save(course);
    }

    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new NotFoundException("Course not found");
        }
        courseRepository.deleteById(courseId);
    }

    public Course assignInstructor(Long courseId, Long instructorId) {
        userService.validateRole(instructorId, Role.INSTRUCTOR);
        Course course = getCourse(courseId);
        course.setInstructorId(instructorId);
        return courseRepository.save(course);
    }

    public Module addModule(Long instructorId, Long courseId, CreateModuleRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Module module = new Module(null, request.title());
        Course course = getCourse(courseId);
        course.addModule(module);
        courseRepository.save(course);
        return module;
    }

    public Material addMaterial(Long instructorId, Long courseId, Long moduleId, UploadMaterialRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Module module = getCourse(courseId).getModules().stream()
                .filter(existing -> existing.getModuleId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Module not found"));

        Material material = new Material(
            null,
                request.fileType(),
                LocalDateTime.now(),
                request.name(),
                request.contentUrl()
        );
        module.addMaterial(material);
        courseRepository.save(getCourse(courseId));
        return material;
    }

    public Material uploadPdfMaterial(Long instructorId, Long courseId, Long moduleId, String name, MultipartFile file) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Material name is required");
        }

        validateInstructorForCourse(instructorId, courseId);
        Module module = getCourse(courseId).getModules().stream()
                .filter(existing -> existing.getModuleId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Module not found"));

        FileStorageService.StoredFile storedFile = fileStorageService.storePdf(file);
        Material material = new Material(
            null,
                MaterialType.PDF,
                LocalDateTime.now(),
                name,
            "pending"
        );
        module.addMaterial(material);
        Course saved = courseRepository.save(getCourse(courseId));

        Material persistedMaterial = saved.getModules().stream()
            .filter(m -> m.getModuleId().equals(moduleId))
            .flatMap(m -> m.getMaterials().stream())
            .filter(m -> "pending".equals(m.getContentUrl()))
            .reduce((first, second) -> second)
            .orElseThrow(() -> new NotFoundException("Saved material not found"));

        persistedMaterial.setContentUrl("/api/students/{studentId}/materials/" + persistedMaterial.getFileId() + "/download");
        courseRepository.save(saved);

        materialFileMetadataRepository.save(new MaterialFileMetadata(
            persistedMaterial.getFileId(),
            storedFile.storagePath(),
            storedFile.originalFileName()
        ));
        return persistedMaterial;
    }

    public Assignment createAssignment(Long instructorId, Long courseId, CreateAssignmentRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Assignment assignment = new Assignment(null, request.description(), request.deadline());
        assignment.publish();
        Course course = getCourse(courseId);
        course.addAssignment(assignment);
        courseRepository.save(course);
        return assignment;
    }

    public Exam addExam(Long instructorId, Long courseId, CreateExamRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Exam exam = new Exam(
                null,
                request.title(),
                request.description(),
                java.time.LocalDateTime.parse(request.scheduleDateTime()),
                request.fileType(),
                request.fileUrl(),
                java.time.LocalDateTime.now()
        );
        Course course = getCourse(courseId);
        course.addExam(exam);
        courseRepository.save(course);
        return exam;
    }

    public Assignment getAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
    }

    private void validateInstructorForCourse(Long instructorId, Long courseId) {
        userService.validateRole(instructorId, Role.INSTRUCTOR);
        Course course = getCourse(courseId);
        if (course.getInstructorId() == null || !course.getInstructorId().equals(instructorId)) {
            throw new BadRequestException("Instructor is not assigned to this course");
        }
    }
}