package com.interviewcoach.backend.service;

import com.interviewcoach.backend.config.ResumeStorageProperties;
import com.interviewcoach.backend.model.Resume;
import com.interviewcoach.backend.repository.ResumeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeParserServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private ResumeParserService resumeParserService;

    private FileStorageService fileStorageService;
    private DocParserUtil docParserUtil;
    private ResumeStorageProperties storageProperties;

    @BeforeEach
    void setup() {
        storageProperties = new ResumeStorageProperties();
        storageProperties.setStoragePath("target/test-resume-storage");
        fileStorageService = new FileStorageService(storageProperties);
        docParserUtil = new DocParserUtil();
        resumeParserService = new ResumeParserService(fileStorageService, resumeRepository, docParserUtil);
    }

    @AfterEach
    void cleanup() throws Exception {
        Path root = Path.of(storageProperties.getStoragePath());
        if (Files.exists(root)) {
            Files.walk(root)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
        }
    }

    @Test
    void parseAndStoreResume_acceptsPdfUpload() throws Exception {
        MockMultipartFile file = loadResourceUpload("sample_resume.pdf", "application/pdf");
        when(resumeRepository.save(any())).thenAnswer(invocation -> {
            Resume resume = invocation.getArgument(0);
            resume.setId(1L);
            resume.setUploadedAt(LocalDateTime.now());
            return resume;
        });

        ResumeParserService.ResumePreview preview = resumeParserService.parseAndStoreResume(file, 42L);

        assertThat(preview).isNotNull();
        assertThat(preview.resumeId()).isEqualTo(1L);
        assertThat(preview.filename()).isEqualTo("sample_resume.pdf");
        assertThat(preview.parsedTextPreview()).contains("Test Resume PDF Document");
    }

    @Test
    void parseAndStoreResume_acceptsDocxUpload() throws Exception {
        MockMultipartFile file = loadResourceUpload("sample_resume.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        when(resumeRepository.save(any())).thenAnswer(invocation -> {
            Resume resume = invocation.getArgument(0);
            resume.setId(2L);
            resume.setUploadedAt(LocalDateTime.now());
            return resume;
        });

        ResumeParserService.ResumePreview preview = resumeParserService.parseAndStoreResume(file, 99L);

        assertThat(preview).isNotNull();
        assertThat(preview.resumeId()).isEqualTo(2L);
        assertThat(preview.filename()).isEqualTo("sample_resume.docx");
        assertThat(preview.parsedTextPreview()).contains("Test Resume Document");
    }

    @Test
    void parseAndStoreResume_rejectsInvalidFileType() {
        MockMultipartFile file = new MockMultipartFile("file", "resume.txt", "text/plain", "not a resume".getBytes());

        var error = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> resumeParserService.parseAndStoreResume(file, 123L));

        assertThat(error.getMessage()).contains("Unsupported resume file type");
    }

    private MockMultipartFile loadResourceUpload(String resourceName, String contentType) throws Exception {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(resourceName);
        assertThat(resource).isNotNull();
        byte[] content = resource.readAllBytes();
        return new MockMultipartFile("file", resourceName, contentType, content);
    }
}
