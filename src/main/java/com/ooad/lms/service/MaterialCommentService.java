package com.ooad.lms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ooad.lms.dto.MaterialCommentRequest;
import com.ooad.lms.dto.MaterialCommentThreadResponse;
import com.ooad.lms.dto.MaterialReplyRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.Material;
import com.ooad.lms.model.MaterialComment;
import com.ooad.lms.model.Module;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.User;
import com.ooad.lms.repository.InMemoryDataStore;
import com.ooad.lms.repository.MaterialCommentRepository;

@Service
public class MaterialCommentService {
    private final MaterialCommentRepository commentRepository;
    private final InMemoryDataStore dataStore;
    private final UserService userService;
    private final CourseService courseService;
    private final NotificationService notificationService;

    public MaterialCommentService(MaterialCommentRepository commentRepository,
                                  InMemoryDataStore dataStore,
                                  UserService userService,
                                  CourseService courseService,
                                  NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.dataStore = dataStore;
        this.userService = userService;
        this.courseService = courseService;
        this.notificationService = notificationService;
    }

    public List<MaterialComment> getComments(Long materialId) {
        validateMaterialExists(materialId);
        return commentRepository.findByMaterialId(materialId);
    }

    public List<MaterialCommentThreadResponse> getCommentThreadsForInstructor(Long instructorId) {
        userService.validateRole(instructorId, Role.INSTRUCTOR);

        return courseService.getAllCourses().stream()
                .filter(course -> course.getInstructorId() != null && course.getInstructorId().equals(instructorId))
                .flatMap(course -> course.getModules().stream().flatMap(module -> module.getMaterials().stream()
                        .map(material -> new MaterialContext(course, module, material))))
                .map(context -> new MaterialCommentThreadResponse(
                        context.course().getCourseId(),
                        context.course().getTitle(),
                        context.module().getModuleId(),
                        context.module().getTitle(),
                        context.material().getFileId(),
                        context.material().getName(),
                        context.material().getContentUrl(),
                        commentRepository.findByMaterialId(context.material().getFileId())
                ))
                .filter(thread -> !thread.comments().isEmpty())
                .toList();
    }

    public MaterialComment addComment(Long studentId, Long materialId, MaterialCommentRequest request) {
        userService.validateRole(studentId, Role.STUDENT);
        MaterialContext context = getMaterialContext(materialId);
        validateStudentHasAccessToMaterial(studentId, context);

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
        MaterialComment saved = commentRepository.save(comment);
        notificationService.notifyCommentAsked(
                context.course().getInstructorId(),
                studentId,
                student.getName(),
                materialId,
                context.material().getName(),
                commentId,
                request.message()
        );
        return saved;
    }

    public MaterialComment addReply(Long userId, Long materialId, Long commentId, MaterialReplyRequest request) {
        User author = userService.getUser(userId);
        if (author.getRole() != Role.INSTRUCTOR && author.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students and instructors can reply to comments");
        }

        MaterialContext context = getMaterialContext(materialId);
        if (author.getRole() == Role.STUDENT) {
            validateStudentHasAccessToMaterial(userId, context);
        } else {
            validateInstructorHasAccessToMaterial(userId, context);
        }

        MaterialComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!comment.getMaterialId().equals(materialId)) {
            throw new BadRequestException("Comment does not belong to this material");
        }

        comment.addReply(new MaterialComment.CommentReply(
                userId,
                author.getName(),
                author.getRole(),
                request.reply(),
                LocalDateTime.now()
        ));
        MaterialComment saved = commentRepository.save(comment);
        if (author.getRole() == Role.INSTRUCTOR) {
            notificationService.notifyInstructorReply(
                    comment.getAuthorId(),
                    comment.getAuthorName(),
                    userId,
                    author.getName(),
                    materialId,
                    context.material().getName(),
                    commentId,
                    request.reply()
            );
        } else {
            notificationService.notifyStudentReply(
                    comment.getAuthorId(),
                    comment.getAuthorName(),
                    userId,
                    author.getName(),
                    materialId,
                    context.material().getName(),
                    commentId,
                    request.reply()
            );
        }
        return saved;
    }

    private void validateMaterialExists(Long materialId) {
        getMaterialContext(materialId);
    }

    private void validateStudentHasAccessToMaterial(Long studentId, MaterialContext context) {
        boolean allowed = courseService.getAllCourses().stream()
                .filter(course -> course.getEnrolledStudentIds().contains(studentId))
                .flatMap(course -> course.getModules().stream())
                .flatMap(module -> module.getMaterials().stream())
                .anyMatch(material -> material.getFileId().equals(context.material().getFileId()));
        if (!allowed) {
            throw new BadRequestException("Student does not have access to this material");
        }
    }

    private void validateInstructorHasAccessToMaterial(Long instructorId, MaterialContext context) {
        boolean allowed = courseService.getAllCourses().stream()
                .filter(course -> course.getInstructorId() != null && course.getInstructorId().equals(instructorId))
                .flatMap(course -> course.getModules().stream())
                .flatMap(module -> module.getMaterials().stream())
                .anyMatch(material -> material.getFileId().equals(context.material().getFileId()));
        if (!allowed) {
            throw new BadRequestException("Instructor does not have access to this material");
        }
    }

    private MaterialContext getMaterialContext(Long materialId) {
        return courseService.getAllCourses().stream()
                .flatMap(course -> course.getModules().stream()
                        .flatMap(module -> module.getMaterials().stream()
                                .map(material -> new MaterialContext(course, module, material))))
                .filter(context -> context.material().getFileId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Material not found"));
    }

    private record MaterialContext(Course course, Module module, Material material) {
    }
}
