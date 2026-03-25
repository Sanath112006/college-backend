package com.college.complaintportal.service;

import com.college.complaintportal.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    private final Path uploadDir;

    public FileStorageService(@Value("${app.file-upload-dir:uploads}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("File size must be less than 5 MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Only JPG, PNG, and PDF files are allowed");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = "";
        int extIndex = originalFilename.lastIndexOf('.');
        if (extIndex != -1) {
            extension = originalFilename.substring(extIndex);
        }

        String storedFileName = UUID.randomUUID() + extension;
        Path targetLocation = uploadDir.resolve(storedFileName).normalize();

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BadRequestException("Could not store file. Please try again.");
        }

        // Return relative path so we can build download URL later
        return storedFileName;
    }

    public Path resolvePath(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) {
            return null;
        }
        Path filePath = uploadDir.resolve(storedFileName).normalize();
        if (!filePath.startsWith(uploadDir)) {
            throw new BadRequestException("Invalid file path");
        }
        return filePath;
    }
}

