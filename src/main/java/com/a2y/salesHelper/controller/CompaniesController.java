package com.a2y.salesHelper.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a2y.salesHelper.config.CurrentUser;
import com.a2y.salesHelper.pojo.Companies;
import com.a2y.salesHelper.service.interfaces.CompaniesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/companies/excel")
@Tag(name = "Companies Excel Parser", description = "All the API related to Companies")
public class CompaniesController {

    private final CompaniesService companiesService;

    public CompaniesController(CompaniesService companiesService) {
        this.companiesService = companiesService;
    }

    /**
     * Parses the uploaded Excel file and returns the number of records processed.
     *
     * @param file the Excel file to parse
     * @return the number of records processed
     * @throws IOException if an error occurs while reading the file
     */
    @PostMapping("/upload")
    @Operation(summary = "Upload the Excel Sheets", description = "Accepts Multipart file and Parses it to save data in DB")
    public ResponseEntity<Integer> uploadExcelFile(MultipartFile file, @RequestParam Long clientId) throws IOException {
        Long tenantId = CurrentUser.getTenantId();
        Integer processedCount = companiesService.parseExcelFile(file, clientId, tenantId);
        return new ResponseEntity<>(processedCount, HttpStatus.OK);
    }

    /**
     * Retrieves a company by its ID.
     *
     * @param id       the ID of the company to retrieve
     * @param clientId the ID of the client to which the company belongs
     * @return the company with the specified ID, or a 404 status if not found
     */
    @Operation(summary = "Get Company by ID", description = "Returns a company by its ID")
    @PostMapping()
    public ResponseEntity<Companies> getCompanyById(@RequestParam Long id, @RequestParam Long clientId) {
        Long tenantId = CurrentUser.getTenantId();
        Companies response = companiesService.getCompanyById(id, clientId, tenantId);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Gives all the companies stored in the DB", description = "Returns all the Companies stored in DB")
    @PostMapping("/getAll")
    public ResponseEntity<List<Companies>> getAllCompanies(@RequestParam Long clientId) {
        Long tenantId = CurrentUser.getTenantId();
        List<Companies> response = companiesService.getAllCompanies(clientId, tenantId);
        if (response.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // filter api that will filter the companies according to the field passed
    @Operation(summary = "Filter Companies", description = "Filters companies based on the provided field and value.")
    @PostMapping("/filter")
    public ResponseEntity<List<Companies>> filterCompanies(String field, String value, Long clientId) {
        Long tenantId = CurrentUser.getTenantId();
        List<Companies> response = companiesService.filterCompanies(field, value, clientId, tenantId);
        if (response.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update Company by ID", description = "Updates a company in the database using its ID")
    @PutMapping("/update")
    public ResponseEntity<Companies> updateCompanyById(Companies company) {
        Companies response = companiesService.updateCompanyById(company);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete Company by ID", description = "Deletes a company from the database using its ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteCompanyById(@PathVariable Long id, @RequestParam Long clientId) {
        Long tenantId = CurrentUser.getTenantId();
        Boolean response = companiesService.deleteCompanyById(id, clientId, tenantId);
        if (!response) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
