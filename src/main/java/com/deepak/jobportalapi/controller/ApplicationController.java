package com.deepak.jobportalapi.controller;

import com.deepak.jobportalapi.dto.ApplicationRequestDTO;
import com.deepak.jobportalapi.entity.Application;
import com.deepak.jobportalapi.service.ApplicationService;
import com.deepak.jobportalapi.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.deepak.jobportalapi.dto.ApplicationResponseDTO;
import java.util.List;
import com.deepak.jobportalapi.dto.ApplicationStatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.deepak.jobportalapi.dto.ApplicationStatusDTO; // or ApplicationStatusUpdateDTO
import com.deepak.jobportalapi.entity.ApplicationStatus;



@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final FileStorageService fileStorageService;

    public ApplicationController(
            ApplicationService applicationService,
            FileStorageService fileStorageService) {
        this.applicationService = applicationService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationResponseDTO> applyWithResume(
            @RequestParam("jobId") Long jobId,
            @RequestParam("resume") MultipartFile resumeFile,
            Authentication authentication) {

        // 1. Save file to disk and get stored file path
        String savedFilePath = fileStorageService.storeFile(resumeFile);

        // 2. Save application record in database
        String username = authentication.getName();
        Application application = applicationService.applyToJobWithResume(jobId, username, savedFilePath);

        // 3. Map to DTO
        ApplicationResponseDTO dto = new ApplicationResponseDTO();
        dto.setId(application.getId());
        dto.setJobId(application.getJob().getId());
        dto.setJobTitle(application.getJob().getTitle());
        dto.setUserId(application.getUser().getId());
        dto.setUserName(application.getUser().getEmail());
        dto.setStatus(application.getStatus().name());
        dto.setResumePath(application.getResumePath());
        dto.setAppliedAt(application.getAppliedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ApplicationResponseDTO getApplicationById(@PathVariable Long id) {

        Application application =
                applicationService.getApplicationById(id);

        return applicationService.convertToDTO(application);
    }

    @GetMapping
    public List<ApplicationResponseDTO> getAllApplications() {

        List<Application> applications =
                applicationService.getAllApplications();

        return applications.stream()
                .map(applicationService::convertToDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ApplicationResponseDTO> createApplication(
            @Valid @RequestBody ApplicationRequestDTO requestDTO) {

        Application application = applicationService.createApplication(
                requestDTO.getJobId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.convertToDTO(application));
    }

    @GetMapping("/user")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByUser() {

        List<Application> applications =
                applicationService.getApplicationsByUser();

        List<ApplicationResponseDTO> response = applications.stream()
                .map(applicationService::convertToDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/job/{jobId}")
    public List<ApplicationResponseDTO> getApplicationsByJob(
            @PathVariable Long jobId) {

        List<Application> applications =
                applicationService.getApplicationsByJob(jobId);

        return applications.stream()
                .map(applicationService::convertToDTO)
                .toList();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatusDTO statusDTO) {

        Application updated = applicationService.updateApplicationStatus(
                id,
                statusDTO.getStatus()
        );

        ApplicationResponseDTO dto = applicationService.convertToDTO(updated);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {

        applicationService.deleteApplication(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);

        if (application.getResumePath() == null) {
            throw new RuntimeException("No resume file attached to this application");
        }

        try {
            Path filePath = Paths.get(application.getResumePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Resume file not found or unreadable on disk");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Could not read file: " + e.getMessage());
        }
    }
}