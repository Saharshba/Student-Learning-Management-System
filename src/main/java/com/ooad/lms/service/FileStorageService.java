package com.ooad.lms.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public StoredFile storePdf(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // Store only the file name so persisted metadata remains portable across machines.
            return new StoredFile(targetLocation.getFileName().toString(), file.getOriginalFilename());
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadAsResource(String filePath) {
        try {
            Path filePathObj = Paths.get(filePath).normalize();
            if (!filePathObj.isAbsolute()) {
                filePathObj = fileStorageLocation.resolve(filePathObj).normalize();
            }

            // Backward compatibility for older metadata that saved an absolute path on another machine.
            if (!Files.exists(filePathObj) || !Files.isReadable(filePathObj)) {
                Path fileNameOnlyPath = filePathObj.getFileName();
                if (fileNameOnlyPath != null) {
                    Path fallbackPath = fileStorageLocation.resolve(fileNameOnlyPath).normalize();
                    if (Files.exists(fallbackPath) && Files.isReadable(fallbackPath)) {
                        filePathObj = fallbackPath;
                    }
                }
            }

            Resource resource = new UrlResource(filePathObj.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filePath);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not read file: " + filePath, ex);
        }
    }

    public static class StoredFile {
        private final String storagePath;
        private final String originalFileName;

        public StoredFile(String storagePath, String originalFileName) {
            this.storagePath = storagePath;
            this.originalFileName = originalFileName;
        }

        public String storagePath() {
            return storagePath;
        }

        public String originalFileName() {
            return originalFileName;
        }
    }
}