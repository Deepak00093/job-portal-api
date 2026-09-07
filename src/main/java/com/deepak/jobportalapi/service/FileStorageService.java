package com.deepak.jobportalapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Directory where resumes will be saved locally
    private final Path storageLocation = Paths.get("uploads/resumes");

    public FileStorageService() {
        try {
            // Automatically creates the directory if it does not exist
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage folder: " + e.getMessage());
        }
    }

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot store an empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".pdf")) {
            throw new RuntimeException("Only PDF resumes are supported.");
        }

        // Generate unique filename to avoid overwriting files with identical names
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        try {
            Path targetLocation = storageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }
}
