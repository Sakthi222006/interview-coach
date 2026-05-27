package com.interviewcoach.backend.service;

import com.interviewcoach.backend.model.Resume;
import com.interviewcoach.backend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeParserService {

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final FileStorageService fileStorageService;
    private final ResumeRepository resumeRepository;
    private final DocParserUtil docParserUtil;

    public ResumePreview parseAndStoreResume(MultipartFile file, Long userId) {
        validateFile(file);
        fileStorageService.initStorage();
        FileStorageService.StoredFile stored = fileStorageService.saveFile(file);

        String extractedText = docParserUtil.extractText(stored.path());
        String previewText = extractedText == null ? "" : extractedText.length() <= 500 ? extractedText : extractedText.substring(0, 500);

        Resume resume = Resume.builder()
                .userId(userId)
                .filename(stored.originalFilename())
                .contentType(stored.contentType())
                .fileSize(stored.size())
                .parsedText(extractedText)
                .storagePath(stored.storedFilename())
                .uploadedAt(LocalDateTime.now())
                .build();

        Resume saved = resumeRepository.save(resume);
        return new ResumePreview(saved.getId(), previewText, saved.getFilename(), saved.getUploadedAt());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume file is required");
        }

        String contentType = StringUtils.hasText(file.getContentType()) ? file.getContentType() : "";
        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported resume file type: " + contentType);
        }

        if (file.getSize() > fileStorageService.getMaxFileSizeBytes()) {
            throw new IllegalArgumentException("Resume file exceeds maximum size of " + fileStorageService.getMaxFileSizeBytes() + " bytes");
        }
    }

    public record ResumePreview(Long resumeId, String parsedTextPreview, String filename, LocalDateTime uploadedAt) {
    }
}
