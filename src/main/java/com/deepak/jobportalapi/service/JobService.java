package com.deepak.jobportalapi.service;

import com.deepak.jobportalapi.entity.Company;
import com.deepak.jobportalapi.entity.Job;
import com.deepak.jobportalapi.repository.JobRepository;
import com.deepak.jobportalapi.specification.JobSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public void deleteJob(Long id) {
        Job job = getJobById(id);
        jobRepository.delete(job);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    public Job updateJob(Long id, Job job, Company company) {
        Job existingJob = getJobById(id);

        existingJob.setTitle(job.getTitle());
        existingJob.setDescription(job.getDescription());
        existingJob.setLocation(job.getLocation());
        existingJob.setSalary(job.getSalary());
        existingJob.setCompany(company);

        // Fix: save() directly returns the persisted entity
        return jobRepository.save(existingJob);
    }

    @Transactional(readOnly = true)
    public List<Job> searchJobsByTitle(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional(readOnly = true)
    public List<Job> searchJobsByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    @Transactional(readOnly = true)
    public Page<Job> getJobsWithPagination(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Job> searchJobs(String title, String location) {
        Specification<Job> spec = buildSpecification(title, location);
        return (spec == null) ? jobRepository.findAll() : jobRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public Page<Job> searchJobsWithPagination(String title, String location, Pageable pageable) {
        Specification<Job> spec = buildSpecification(title, location);
        return (spec == null) ? jobRepository.findAll(pageable) : jobRepository.findAll(spec, pageable);
    }

    private Specification<Job> buildSpecification(String title, String location) {
        Specification<Job> spec = null;
        if (title != null && !title.isBlank()) {
            spec = JobSpecification.hasTitle(title);
        }
        if (location != null && !location.isBlank()) {
            Specification<Job> locSpec = JobSpecification.hasLocation(location);
            spec = (spec == null) ? locSpec : spec.and(locSpec);
        }
        return spec;
    }
}