package com.deepak.jobportalapi.controller;

import com.deepak.jobportalapi.entity.Company;
import com.deepak.jobportalapi.service.CompanyService;
import org.springframework.web.bind.annotation.*;
import com.deepak.jobportalapi.dto.CompanyRequestDTO;
import jakarta.validation.Valid;
import java.util.List;
import com.deepak.jobportalapi.dto.CompanyResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(
            @Valid @RequestBody CompanyRequestDTO companyRequestDTO) {

        Company company = new Company();

        company.setName(companyRequestDTO.getName());
        company.setDescription(companyRequestDTO.getDescription());
        company.setLocation(companyRequestDTO.getLocation());
        company.setWebsite(companyRequestDTO.getWebsite());

        Company savedCompany = companyService.createCompany(company);

        CompanyResponseDTO dto = new CompanyResponseDTO();

        dto.setId(savedCompany.getId());
        dto.setName(savedCompany.getName());
        dto.setDescription(savedCompany.getDescription());
        dto.setLocation(savedCompany.getLocation());
        dto.setWebsite(savedCompany.getWebsite());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping("/{id}")
    public CompanyResponseDTO getCompanyById(@PathVariable Long id) {

        Company company = companyService.getCompanyById(id);

        CompanyResponseDTO dto = new CompanyResponseDTO();

        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setLocation(company.getLocation());
        dto.setWebsite(company.getWebsite());

        return dto;
    }

    @GetMapping
    public List<CompanyResponseDTO> getAllCompanies() {

        List<Company> companies = companyService.getAllCompanies();

        return companies.stream()
                .map(company -> {
                    CompanyResponseDTO dto = new CompanyResponseDTO();

                    dto.setId(company.getId());
                    dto.setName(company.getName());
                    dto.setDescription(company.getDescription());
                    dto.setLocation(company.getLocation());
                    dto.setWebsite(company.getWebsite());

                    return dto;
                })
                .toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {

        companyService.deleteCompany(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequestDTO companyRequestDTO) {

        Company company = new Company();

        company.setName(companyRequestDTO.getName());
        company.setDescription(companyRequestDTO.getDescription());
        company.setLocation(companyRequestDTO.getLocation());
        company.setWebsite(companyRequestDTO.getWebsite());

        Company updatedCompany = companyService.updateCompany(id, company);

        CompanyResponseDTO dto = new CompanyResponseDTO();

        dto.setId(updatedCompany.getId());
        dto.setName(updatedCompany.getName());
        dto.setDescription(updatedCompany.getDescription());
        dto.setLocation(updatedCompany.getLocation());
        dto.setWebsite(updatedCompany.getWebsite());

        return ResponseEntity.ok(dto);
    }
}