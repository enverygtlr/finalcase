package com.definex.finalcase.service;

import com.definex.finalcase.domain.entity.Attachment;
import com.definex.finalcase.domain.entity.Task;
import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.response.AttachmentResponse;
import com.definex.finalcase.domain.response.FileResponse;
import com.definex.finalcase.exception.AttachmentNotFoundException;
import com.definex.finalcase.exception.TaskNotFoundException;
import com.definex.finalcase.exception.UserNotFoundException;
import com.definex.finalcase.mapper.AttachmentMapper;
import com.definex.finalcase.repository.AttachmentRepository;
import com.definex.finalcase.repository.TaskRepository;
import com.definex.finalcase.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileManagerService fileManagerService;

    @Mock
    private AttachmentMapper attachmentMapper;

    @InjectMocks
    private AttachmentService attachmentService;

    @Test
    void uploadAttachment_shouldSucceed() {
        // Given
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String originalFileName = "doc.pdf";
        String uploadedFileName = "UUID_doc.pdf";
        String contentType = "application/pdf";

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(originalFileName);
        when(file.getContentType()).thenReturn(contentType);

        Task task = new Task();
        User user = new User();

        Attachment attachment = new Attachment();
        Attachment savedAttachment = new Attachment();
        AttachmentResponse expected = mock(AttachmentResponse.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileManagerService.uploadFile(file)).thenReturn(uploadedFileName);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(savedAttachment);
        when(attachmentMapper.toResponse(savedAttachment)).thenReturn(expected);

        // When
        AttachmentResponse actual = attachmentService.uploadAttachment(taskId, userId, file);

        // Then
        assertEquals(expected, actual);
        verify(fileManagerService).uploadFile(file);
        verify(attachmentRepository, times(2)).save(any()); // once in variable, once in return
        verify(attachmentMapper).toResponse(savedAttachment);
    }

    @Test
    void uploadAttachment_shouldThrow_whenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> attachmentService.uploadAttachment(taskId, userId, file));
    }

    @Test
    void uploadAttachment_shouldThrow_whenUserNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> attachmentService.uploadAttachment(taskId, userId, file));
    }

    @Test
    void downloadAttachment_shouldReturnFileResponse_whenAttachmentExists() {
        // Given
        UUID attachmentId = UUID.randomUUID();
        Attachment attachment = new Attachment();
        attachment.setFileName("file_123.pdf");
        attachment.setExternalFileName("original.pdf");
        attachment.setContentType("application/pdf");

        byte[] fileData = "file content".getBytes();

        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));
        when(fileManagerService.downloadFile("file_123.pdf")).thenReturn(fileData);

        // When
        FileResponse response = attachmentService.downloadAttachment(attachmentId);

        // Then
        assertEquals("original.pdf", response.fileName());
        assertEquals("application/pdf", response.mimeType());
        assertArrayEquals(fileData, response.data());
        verify(fileManagerService).downloadFile("file_123.pdf");
    }

    @Test
    void downloadAttachment_shouldThrow_whenAttachmentNotFound() {
        UUID attachmentId = UUID.randomUUID();
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        assertThrows(AttachmentNotFoundException.class, () -> attachmentService.downloadAttachment(attachmentId));
    }

    @Test
    void deleteAttachment_shouldSucceed_whenAttachmentExists() {
        // Given
        UUID attachmentId = UUID.randomUUID();
        Attachment attachment = new Attachment();
        attachment.setFileName("delete_me.pdf");

        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));

        // When
        attachmentService.deleteAttachment(attachmentId);

        // Then
        verify(fileManagerService).deleteFile("delete_me.pdf");
        verify(attachmentRepository).delete(attachment);
    }

    @Test
    void deleteAttachment_shouldThrow_whenAttachmentNotFound() {
        UUID attachmentId = UUID.randomUUID();
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        assertThrows(AttachmentNotFoundException.class, () -> attachmentService.deleteAttachment(attachmentId));
    }

    @Test
    void getAttachmentsByTask_shouldReturnMappedResponses() {
        // Given
        UUID taskId = UUID.randomUUID();
        Attachment a1 = new Attachment();
        Attachment a2 = new Attachment();
        List<Attachment> attachments = List.of(a1, a2);

        AttachmentResponse r1 = mock(AttachmentResponse.class);
        AttachmentResponse r2 = mock(AttachmentResponse.class);

        when(attachmentRepository.findByTaskId(taskId)).thenReturn(attachments);
        when(attachmentMapper.toResponse(a1)).thenReturn(r1);
        when(attachmentMapper.toResponse(a2)).thenReturn(r2);

        // When
        List<AttachmentResponse> result = attachmentService.getAttachmentsByTask(taskId);

        // Then
        assertEquals(List.of(r1, r2), result);
        verify(attachmentRepository).findByTaskId(taskId);
    }
}
