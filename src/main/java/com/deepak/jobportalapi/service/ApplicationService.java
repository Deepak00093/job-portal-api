package com.deepak.jobportalapi.service;

import com.deepak.jobportalapi.entity.Application;
import com.deepak.jobportalapi.entity.Job;
import com.deepak.jobportalapi.entity.User;
import com.deepak.jobportalapi.exception.ResourceNotFoundException;
import com.deepak.jobportalapi.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import com.deepak.jobportalapi.dto.ApplicationResponseDTO;
import java.util.List;
import com.deepak.jobportalapi.entity.ApplicationStatus;
import com.deepak.jobportalapi.exception.DuplicateApplicationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import com.deepak.jobportalapi.repository.UserRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final JobService jobService;
    private final UserRepository userRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserService userService,
            JobService jobService,
            UserRepository userRepository) {

        this.applicationRepository = applicationRepository;
        this.userService = userService;
        this.jobService = jobService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @Transactional
    public Application createApplication(Long jobId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found with email: " + email));

        Job job = jobService.getJobById(jobId);

        if (applicationRepository.existsByUserIdAndJobId(
                user.getId(), jobId)) {

            throw new DuplicateApplicationException(
                    "You have already applied for this job");
        }

        Application application = new Application();
        application.setUser(user);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);

        return applicationRepository.save(application);
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found with id: " + id
                        ));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    public List<Application> getApplicationsByUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: " + email));

        return applicationRepository.findByUserId(user.getId());
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public List<Application> getApplicationsByJob(Long jobId) {

        jobService.getJobById(jobId);

        return applicationRepository.findByJobId(jobId);
    }

    public ApplicationResponseDTO convertToDTO(Application application) {

        ApplicationResponseDTO dto = new ApplicationResponseDTO();

        dto.setId(application.getId());

        dto.setUserId(application.getUser().getId());
        dto.setUserName(application.getUser().getName());

        dto.setJobId(application.getJob().getId());
        dto.setJobTitle(application.getJob().getTitle());

        dto.setStatus(application.getStatus().name());

        return dto;
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @Transactional
    public Application updateApplicationStatus(
            Long id,
            ApplicationStatus status) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found with id: " + id
                        ));

        application.setStatus(status);

        return applicationRepository.save(application);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @Transactional
    public void deleteApplication(Long id) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: " + email));

        Application application = applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found with id: " + id));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You can only delete your own application");
        }

        applicationRepository.delete(application);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @Transactional
    public Application applyToJobWithResume(Long jobId, String username, String resumePath) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Job job = jobService.getJobById(jobId);

        if (applicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new DuplicateApplicationException("You have already applied for this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setUser(user);
        application.setResumePath(resumePath);
        application.setStatus(ApplicationStatus.APPLIED);

        return applicationRepository.save(application);
    }
}