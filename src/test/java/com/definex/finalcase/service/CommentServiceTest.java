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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_shouldSucceed_whenTaskAndUserExist() {
        // Given
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder().content("Comment.").build();

        Task task = new Task();
        User user = new User();
        Comment comment = new Comment();
        Comment savedComment = new Comment();
        CommentResponse expected = Mockito.mock(CommentResponse.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentMapper.toEntity(request)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(commentMapper.toResponse(savedComment)).thenReturn(expected);

        // When
        var actual = commentService.addComment(taskId, userId, request);

        // Then
        assertEquals(expected, actual);
        assertEquals(task, comment.getTask());
        assertEquals(user, comment.getUser());
    }

    @Test
    void addComment_shouldThrow_whenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder().content("Test").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> commentService.addComment(taskId, userId, request));
    }

    @Test
    void addComment_shouldThrow_whenUserNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder().content("Test").build();
        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> commentService.addComment(taskId, userId, request));
    }

    @Test
    void getCommentById_shouldReturnResponse_whenCommentExists() {
        UUID commentId = UUID.randomUUID();
        Comment comment = new Comment();
        CommentResponse expected = Mockito.mock(CommentResponse.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toResponse(comment)).thenReturn(expected);

        var actual = commentService.getCommentById(commentId);

        assertEquals(expected, actual);
    }

    @Test
    void getCommentById_shouldThrow_whenCommentNotFound() {
        UUID commentId = UUID.randomUUID();
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(commentId));
    }

    @Test
    void getCommentsForTask_shouldReturnMappedResponses() {
        UUID taskId = UUID.randomUUID();
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        List<Comment> comments = List.of(c1, c2);

        CommentResponse r1 = Mockito.mock(CommentResponse.class);
        CommentResponse r2 = Mockito.mock(CommentResponse.class);

        when(commentRepository.findByTaskId(taskId)).thenReturn(comments);
        when(commentMapper.toResponse(c1)).thenReturn(r1);
        when(commentMapper.toResponse(c2)).thenReturn(r2);

        var result = commentService.getCommentsForTask(taskId);

        assertEquals(List.of(r1, r2), result);
        verify(commentRepository).findByTaskId(taskId);
    }

    @Test
    void updateComment_shouldSucceed_whenUserIsOwner() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder().content("Updated comment").build();

        User user = new User();
        user.setId(userId);

        Comment comment = new Comment();
        comment.setUser(user);

        Comment savedComment = new Comment();
        CommentResponse expected = Mockito.mock(CommentResponse.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(commentMapper.toResponse(savedComment)).thenReturn(expected);

        var actual = commentService.updateComment(commentId, userId, request);

        assertEquals(expected, actual);
        assertEquals("Updated comment", comment.getContent());
    }

    @Test
    void updateComment_shouldThrow_whenCommentNotFound() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder().content("Try update").build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(commentId, userId, request));
    }

    @Test
    void updateComment_shouldThrow_whenUserIsNotOwner() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User differentUser = new User();
        differentUser.setId(UUID.randomUUID());

        Comment comment = new Comment();
        comment.setUser(differentUser);

        CommentRequest request = CommentRequest.builder().content("Comment by another user.").build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(UnauthorizedActionException.class, () -> commentService.updateComment(commentId, userId, request));
    }

    @Test
    void deleteComment_shouldSucceed_whenUserIsOwner() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Comment comment = new Comment();
        comment.setUser(user);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(commentId, userId);

        // Then
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldThrow_whenCommentNotFound() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId, userId));
    }

    @Test
    void deleteComment_shouldThrow_whenUserIsNotOwner() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User differentUser = new User();
        differentUser.setId(UUID.randomUUID());

        Comment comment = new Comment();
        comment.setUser(differentUser);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(UnauthorizedActionException.class, () -> commentService.deleteComment(commentId, userId));
    }
}

