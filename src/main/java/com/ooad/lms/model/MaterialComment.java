package com.ooad.lms.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaterialComment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long commentId;
    private Long materialId;
    private Long authorId;
    private String authorName;
    private String message;
    private LocalDateTime timestamp;
    private String reply;
    private Long replyAuthorId;
    private String replyAuthorName;
    private LocalDateTime replyTimestamp;
    private final List<CommentReply> replies = new ArrayList<>();

    public MaterialComment(Long commentId, Long materialId, Long authorId, String authorName, String message, LocalDateTime timestamp) {
        this.commentId = commentId;
        this.materialId = materialId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getReply() {
        return reply;
    }

    public Long getReplyAuthorId() {
        return replyAuthorId;
    }

    public String getReplyAuthorName() {
        return replyAuthorName;
    }

    public LocalDateTime getReplyTimestamp() {
        return replyTimestamp;
    }

    public List<CommentReply> getReplies() {
        return replies;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setReplyAuthorId(Long replyAuthorId) {
        this.replyAuthorId = replyAuthorId;
    }

    public void setReplyAuthorName(String replyAuthorName) {
        this.replyAuthorName = replyAuthorName;
    }

    public void setReplyTimestamp(LocalDateTime replyTimestamp) {
        this.replyTimestamp = replyTimestamp;
    }

    public void addReply(CommentReply reply) {
        this.replies.add(reply);
        setReply(reply.message());
        setReplyAuthorId(reply.authorId());
        setReplyAuthorName(reply.authorName());
        setReplyTimestamp(reply.timestamp());
    }

    public record CommentReply(
            Long authorId,
            String authorName,
            Role authorRole,
            String message,
            LocalDateTime timestamp
    ) implements Serializable {
        private static final long serialVersionUID = 1L;
    }
}
