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
import com.ooad.lms.model.AssignmentFileMetadata;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.Exam;
import com.ooad.lms.model.Material;
import com.ooad.lms.model.MaterialFileMetadata;
import com.ooad.lms.model.MaterialType;
import com.ooad.lms.model.Module;
import com.ooad.lms.model.Role;
import com.ooad.lms.repository.AssignmentFileMetadataRepository;
import com.ooad.lms.repository.AssignmentRepository;
import com.ooad.lms.repository.CourseRepository;
import com.ooad.lms.repository.InMemoryDataStore;
import com.ooad.lms.repository.MaterialFileMetadataRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final MaterialFileMetadataRepository materialFileMetadataRepository;
    private final AssignmentFileMetadataRepository assignmentFileMetadataRepository;
    private final InMemoryDataStore dataStore;

    public CourseService(
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository,
            UserService userService,
            FileStorageService fileStorageService,
                MaterialFileMetadataRepository materialFileMetadataRepository,
                AssignmentFileMetadataRepository assignmentFileMetadataRepository,
                InMemoryDataStore dataStore
    ) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.materialFileMetadataRepository = materialFileMetadataRepository;
        this.assignmentFileMetadataRepository = assignmentFileMetadataRepository;
        this.dataStore = dataStore;
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
        Long moduleId = dataStore.nextModuleId();
        Module module = new Module(moduleId, request.title());
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
                dataStore.nextMaterialId(),
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
        Long materialId = dataStore.nextMaterialId();
        Material material = new Material(
                materialId,
                MaterialType.PDF,
                LocalDateTime.now(),
                name,
                "/api/materials/" + materialId + "/download"
        );
        module.addMaterial(material);
        courseRepository.save(getCourse(courseId));

        materialFileMetadataRepository.save(new MaterialFileMetadata(
                materialId,
                storedFile.storagePath(),
                storedFile.originalFileName()
        ));
        return material;
    }

    public Assignment createAssignment(Long instructorId, Long courseId, CreateAssignmentRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Assignment assignment = new Assignment(null, request.description(), request.deadline());
        assignment.publish();
        assignment = assignmentRepository.save(assignment);
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

        public void uploadAssignmentPdf(Long instructorId, Long courseId, Long assignmentId, MultipartFile file) {
        validateInstructorForCourse(instructorId, courseId);

        Course course = getCourse(courseId);
        boolean assignmentBelongsToCourse = course.getAssignments().stream()
            .anyMatch(assignment -> assignment.getAssignmentId().equals(assignmentId));
        if (!assignmentBelongsToCourse) {
            throw new BadRequestException("Assignment does not belong to this course");
        }

        FileStorageService.StoredFile storedFile = fileStorageService.storePdf(file);
        assignmentFileMetadataRepository.save(new AssignmentFileMetadata(
            assignmentId,
            storedFile.storagePath(),
            storedFile.originalFileName()
        ));
        }

        public MaterialDownload getAssignmentPdf(Long assignmentId) {
        AssignmentFileMetadata metadata = assignmentFileMetadataRepository.findById(assignmentId)
            .orElseThrow(() -> new NotFoundException("Assignment PDF not found"));

        return new MaterialDownload(
            fileStorageService.loadAsResource(metadata.getStoragePath()),
            metadata.getOriginalFileName()
        );
        }

        public record MaterialDownload(org.springframework.core.io.Resource resource, String fileName) {
        }

    private void validateInstructorForCourse(Long instructorId, Long courseId) {
        userService.validateRole(instructorId, Role.INSTRUCTOR);
        Course course = getCourse(courseId);
        if (course.getInstructorId() == null || !course.getInstructorId().equals(instructorId)) {
            throw new BadRequestException("Instructor is not assigned to this course");
        }
    }
}