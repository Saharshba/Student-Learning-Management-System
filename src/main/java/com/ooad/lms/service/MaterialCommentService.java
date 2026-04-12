package com.ooad.lms.service;

import com.ooad.lms.dto.MaterialCommentRequest;
import com.ooad.lms.dto.MaterialReplyRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.MaterialComment;
import com.ooad.lms.model.Role;
import com.ooad.lms.repository.InMemoryDataStore;
import com.ooad.lms.repository.MaterialCommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaterialCommentService {
    private final MaterialCommentRepository commentRepository;
    private final InMemoryDataStore dataStore;
    private final UserService userService;
    private final CourseService courseService;

    public MaterialCommentService(MaterialCommentRepository commentRepository,
                                  InMemoryDataStore dataStore,
                                  UserService userService,
                                  CourseService courseService) {
        this.commentRepository = commentRepository;
        this.dataStore = dataStore;
        this.userService = userService;
        this.courseService = courseService;
    }

    public List<MaterialComment> getComments(Long materialId) {
        validateMaterialExists(materialId);
        return commentRepository.findByMaterialId(materialId);
    }

    public MaterialComment addComment(Long studentId, Long materialId, MaterialCommentRequest request) {
        userService.validateRole(studentId, Role.STUDENT);
        validateStudentHasAccessToMaterial(studentId, materialId);

        var student = userService.getUser(studentId);
        var commentId = dataStore.nextMaterialCommentId();
        MaterialComment comment = new MaterialComment(
                commentId,
                materialId,
                studentId,
                student.getName(),
                request.message(),
                LocalDateTime.now()
        );
        return commentRepository.save(comment);
    }

    public MaterialComment addReply(Long instructorId, Long materialId, Long commentId, MaterialReplyRequest request) {
        userService.validateRole(instructorId, Role.INSTRUCTOR);
        validateInstructorHasAccessToMaterial(instructorId, materialId);

        MaterialComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!comment.getMaterialId().equals(materialId)) {
            throw new BadRequestException("Comment does not belong to this material");
        }

        var instructor = userService.getUser(instructorId);
        comment.setReply(request.reply());
        comment.setReplyAuthorId(instructorId);
        comment.setReplyAuthorName(instructor.getName());
        comment.setReplyTimestamp(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private void validateMaterialExists(Long materialId) {
        boolean exists = courseService.getAllCourses().stream()
                .flatMap(course -> course.getModules().stream())
                .flatMap(module -> module.getMaterials().stream())
                .anyMatch(material -> material.getFileId().equals(materialId));
        if (!exists) {
            throw new NotFoundException("Material not found");
        }
    }

    private void validateStudentHasAccessToMaterial(Long studentId, Long materialId) {
        boolean allowed = courseService.getAllCourses().stream()
                .filter(course -> course.getEnrolledStudentIds().contains(studentId))
                .flatMap(course -> course.getModules().stream())
                .flatMap(module -> module.getMaterials().stream())
                .anyMatch(material -> material.getFileId().equals(materialId));
        if (!allowed) {
            throw new BadRequestException("Student does not have access to this material");
        }
    }

    private void validateInstructorHasAccessToMaterial(Long instructorId, Long materialId) {
        boolean allowed = courseService.getAllCourses().stream()
                .filter(course -> course.getInstructorId() != null && course.getInstructorId().equals(instructorId))
                .flatMap(course -> course.getModules().stream())
                .flatMap(module -> module.getMaterials().stream())
                .anyMatch(material -> material.getFileId().equals(materialId));
        if (!allowed) {
            throw new BadRequestException("Instructor does not have access to this material");
        }
    }
}
