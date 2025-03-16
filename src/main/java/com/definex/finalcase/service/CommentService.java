package com.definex.finalcase.service;

import com.definex.finalcase.domain.entity.Comment;
import com.definex.finalcase.domain.entity.Task;
import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.request.CommentRequest;
import com.definex.finalcase.domain.response.CommentResponse;
import com.definex.finalcase.exception.CommentNotFoundException;
import com.definex.finalcase.exception.TaskNotFoundException;
import com.definex.finalcase.exception.UnauthorizedActionException;
import com.definex.finalcase.exception.UserNotFoundException;
import com.definex.finalcase.mapper.CommentMapper;
import com.definex.finalcase.repository.CommentRepository;
import com.definex.finalcase.repository.TaskRepository;
import com.definex.finalcase.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentResponse addComment(UUID taskId, UUID userId, CommentRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Comment comment = commentMapper.toEntity(request);
        comment.setTask(task);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponse(savedComment);
    }

    public CommentResponse getCommentById(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        return commentMapper.toResponse(comment);
    }

    public List<CommentResponse> getCommentsForTask(UUID taskId) {
        List<Comment> comments = commentRepository.findByTaskId(taskId);
        return comments.stream().map(commentMapper::toResponse).toList();
    }

    @Transactional
    public CommentResponse updateComment(UUID commentId, UUID userId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException();
        }

        comment.setContent(request.content());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException();
        }

        commentRepository.delete(comment);
    }
}
