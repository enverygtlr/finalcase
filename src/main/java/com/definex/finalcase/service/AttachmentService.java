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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FileManagerService fileManagerService;
    private final AttachmentMapper attachmentMapper;

    @Transactional
    public AttachmentResponse uploadAttachment(UUID taskId, UUID userId, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String fileName = fileManagerService.uploadFile(file);
        String contentType = file.getContentType();

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setExternalFileName(file.getOriginalFilename());
        attachment.setFileName(fileName);
        attachment.setContentType(contentType);
        attachment.setUploadedBy(user);

        attachment = attachmentRepository.save(attachment);

        return attachmentMapper.toResponse(attachmentRepository.save(attachment));
    }

    public FileResponse downloadAttachment(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(AttachmentNotFoundException::new);
        FileResponse response = FileResponse.builder()
                .fileName(attachment.getExternalFileName())
                .mimeType(attachment.getContentType())
                .data(fileManagerService.downloadFile(attachment.getFileName()))
                .build();
        
        return response;
    }

    @Transactional
    public void deleteAttachment(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(AttachmentNotFoundException::new);

        fileManagerService.deleteFile(attachment.getFileName());
        attachmentRepository.delete(attachment);
    }

    public List<AttachmentResponse> getAttachmentsByTask(UUID taskId) {
        return attachmentRepository.findByTaskId(taskId)
                .stream()
                .map(attachmentMapper::toResponse)
                .toList();
    }
}