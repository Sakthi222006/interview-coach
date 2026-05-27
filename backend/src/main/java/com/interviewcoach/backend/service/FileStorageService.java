package com.interviewcoach.backend.service;

import com.interviewcoach.backend.config.ResumeStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final ResumeStorageProperties storageProperties;

    public Path getStorageLocation() {
        return Paths.get(storageProperties.getStoragePath()).toAbsolutePath().normalize();
    }

    public long getMaxFileSizeBytes() {
        return storageProperties.getMaxFileSizeBytes();
    }

    public void initStorage() {
        try {
            Path dir = getStorageLocation();
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create resume storage directory", ex);
        }
    }

    public StoredFile saveFile(MultipartFile file) {
        Objects.requireNonNull(file, "Uploaded file cannot be null");
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        long maxSize = storageProperties.getMaxFileSizeBytes();
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File exceeds maximum allowed size of " + maxSize + " bytes");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence");
        }

        String extension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueName = UUID.randomUUID().toString() + (extension != null ? "." + extension : "");

        initStorage();
        Path destination = getStorageLocation().resolve(uniqueName);
        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store uploaded file", ex);
        }

        return new StoredFile(uniqueName, originalFilename, file.getContentType(), file.getSize(), destination);
    }

    public boolean exists(String filename) {
        if (!StringUtils.hasText(filename)) {
            return false;
        }
        return Files.exists(getStorageLocation().resolve(filename));
    }

    public void deleteFile(String filename) {
        if (!StringUtils.hasText(filename)) {
            return;
        }
        try {
            Files.deleteIfExists(getStorageLocation().resolve(filename));
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to delete file: " + filename, ex);
        }
    }

    public Path getPath(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new IllegalArgumentException("Filename is required");
        }
        return getStorageLocation().resolve(filename).normalize();
    }

    public static record StoredFile(String storedFilename, String originalFilename, String contentType, long size, Path path) {
    }
}
