package com.ooad.lms.service;

import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path storageRoot;

    public FileStorageService(@Value("${lms.storage.path:uploads/materials}") String storagePath) {
        this.storageRoot = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException e) {
            throw new BadRequestException("Unable to initialize file storage directory");
        }
    }

    public StoredFile storePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("PDF file is required");
        }

        String originalFileName = file.getOriginalFilename() == null ? "material.pdf" : file.getOriginalFilename();
        String normalizedName = originalFileName.toLowerCase(Locale.ROOT);
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);

        if (!normalizedName.endsWith(".pdf") && !contentType.contains("pdf")) {
            throw new BadRequestException("Only PDF files are allowed");
        }

        String storedFileName = UUID.randomUUID() + ".pdf";
        Path targetFile = storageRoot.resolve(storedFileName).normalize();

        try {
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequestException("Failed to store uploaded PDF file");
        }

        return new StoredFile(targetFile.toString(), originalFileName);
    }

    public Resource loadAsResource(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            throw new NotFoundException("Stored file path is missing");
        }

        Path path = Paths.get(storedPath).toAbsolutePath().normalize();
        if (!path.startsWith(storageRoot)) {
            throw new BadRequestException("Invalid file path");
        }

        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                throw new NotFoundException("Requested file not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new BadRequestException("Invalid stored file location");
        }
    }

    public record StoredFile(String storagePath, String originalFileName) {
    }
}
