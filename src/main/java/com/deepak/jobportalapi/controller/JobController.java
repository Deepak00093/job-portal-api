package com.deepak.jobportalapi.controller;

import com.deepak.jobportalapi.dto.JobRequestDTO;
import com.deepak.jobportalapi.dto.JobResponseDTO;
import com.deepak.jobportalapi.entity.Company;
import com.deepak.jobportalapi.entity.Job;
import com.deepak.jobportalapi.service.CompanyService;
import com.deepak.jobportalapi.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final CompanyService companyService;

    public JobController(JobService jobService, CompanyService companyService) {
        this.jobService = jobService;
        this.companyService = companyService;
    }

    private JobResponseDTO mapToDTO(Job job) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());
        dto.setPostedAt(job.getPostedAt());

        if (job.getCompany() != null) {
            dto.setCompanyId(job.getCompany().getId());
            dto.setCompanyName(job.getCompany().getName());
        }
        return dto;
    }

    @PostMapping
    public ResponseEntity<JobResponseDTO> createJob(@Valid @RequestBody JobRequestDTO dto) {
        Company company = companyService.getCompanyById(dto.getCompanyId());

        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setCompany(company);

        Job savedJob = jobService.createJob(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(savedJob));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDTO> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(jobService.getJobById(id)));
    }

    @GetMapping
    public ResponseEntity<List<JobResponseDTO>> getAllJobs() {
        List<JobResponseDTO> jobs = jobService.getAllJobs().stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<JobResponseDTO>> getJobsWithPagination(Pageable pageable) {
        return ResponseEntity.ok(jobService.getJobsWithPagination(pageable).map(this::mapToDTO));
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobResponseDTO>> searchJobs(@RequestParam String title) {
        List<JobResponseDTO> jobs = jobService.searchJobsByTitle(title).stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/search/location")
    public ResponseEntity<List<JobResponseDTO>> searchJobsByLocation(@RequestParam String location) {
        List<JobResponseDTO> jobs = jobService.searchJobsByLocation(location).stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<JobResponseDTO>> filterJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location) {

        List<JobResponseDTO> jobs = jobService.searchJobs(title, location).stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/filter/page")
    public ResponseEntity<Page<JobResponseDTO>> searchJobsWithPagination(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            Pageable pageable) {

        return ResponseEntity.ok(
                jobService.searchJobsWithPagination(title, location, pageable).map(this::mapToDTO)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponseDTO> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDTO dto) {

        Company company = companyService.getCompanyById(dto.getCompanyId());

        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());

        Job updatedJob = jobService.updateJob(id, job, company);
        return ResponseEntity.ok(mapToDTO(updatedJob));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}