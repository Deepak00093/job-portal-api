package com.deepak.jobportalapi.repository;

import com.deepak.jobportalapi.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@Repository
public interface JobRepository
        extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    List<Job> findByTitleContainingIgnoreCase(String title);
    Page<Job> findAll(Pageable pageable);
    List<Job> findByLocationContainingIgnoreCase(String location);
}
