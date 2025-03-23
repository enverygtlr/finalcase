package com.definex.finalcase.service;

import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileManagerServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private FileManagerService fileManagerService;

    @Value("${minio.bucket-name}")
    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileManagerService, "bucketName", bucketName);
    }

    @Test
    void createBucketIfNotExists_shouldCreateBucket_whenBucketDoesNotExist() throws Exception {
        // Given
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        // When
        fileManagerService.createBucketIfNotExists();

        // Then
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
    }

    @Test
    void createBucketIfNotExists_shouldNotCreateBucket_whenBucketAlreadyExists() throws Exception {
        // Given
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        // When
        fileManagerService.createBucketIfNotExists();

        // Then
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
    }

    @Test
    void uploadFile_shouldReturnFileName_whenUploadSucceeds() throws Exception {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        String originalFilename = "test.txt";
        String contentType = "text/plain";
        byte[] fileData = "dummy content".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);

        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getInputStream()).thenReturn(inputStream);
        when(mockFile.getSize()).thenReturn((long) fileData.length);

        // When
        String fileName = fileManagerService.uploadFile(mockFile);

        // Then
        assertTrue(fileName.endsWith("_" + originalFilename));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void uploadFile_shouldThrowRuntimeException_whenUploadFails() throws Exception {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("fail.txt");
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated failure"));

        // When - Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileManagerService.uploadFile(mockFile));

        assertTrue(exception.getMessage().contains("Error uploading file"));
    }

    @Test
    void downloadFile_shouldReturnBytes_whenDownloadSucceeds() throws Exception {
        // Given
        String fileName = "file.txt";
        byte[] fileContent = "downloaded content".getBytes();

        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.readAllBytes()).thenReturn(fileContent);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        // When
        byte[] result = fileManagerService.downloadFile(fileName);

        // Then
        assertArrayEquals(fileContent, result);
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void downloadFile_shouldThrowRuntimeException_whenDownloadFails() throws Exception {
        // Given
        String fileName = "missing.txt";
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new IOException("Simulated failure"));

        // When - Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileManagerService.downloadFile(fileName));

        assertTrue(exception.getMessage().contains("Error downloading file"));
    }

    @Test
    void deleteFile_shouldSucceed_whenFileExists() throws Exception {
        // Given
        String fileName = "toDelete.txt";

        // When
        fileManagerService.deleteFile(fileName);

        // Then
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteFile_shouldThrowRuntimeException_whenDeleteFails() throws Exception {
        // Given
        String fileName = "error.txt";
        doThrow(new RuntimeException("Fail")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // When - Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileManagerService.deleteFile(fileName));

        assertTrue(exception.getMessage().contains("Error deleting file"));
    }
}
