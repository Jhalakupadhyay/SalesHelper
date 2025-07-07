package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.pojo.Companies;
import com.a2y.salesHelper.service.interfaces.CompaniesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    @Operation(
            summary = "Upload the Excel Sheets",
            description = "Accepts Multipart file and Parses it to save data in DB"
    )
    public ResponseEntity<Integer> uploadExcelFile(MultipartFile file) throws IOException {
        Integer processedCount = companiesService.parseExcelFile(file);
        return new ResponseEntity<>(processedCount, HttpStatus.OK);
    }
    /**
     * Retrieves a company by its ID.
     *
     * @param id the ID of the company to retrieve
     * @return the company with the specified ID, or a 404 status if not found
     */
    @Operation(
            summary = "Get Company by ID",
            description = "Returns a company by its ID"
    )
    @PostMapping()
    public ResponseEntity<Companies> getCompanyById(Long id) {
        Companies response = companiesService.getCompanyById(id);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @Operation(
            summary = "Gives all the companies stored in the DB",
            description = "Returns all the Companies stored in DB"
    )
    @PostMapping("/getAll")
    public ResponseEntity<List<Companies>> getAllCompanies() {
        List<Companies> response = companiesService.getAllCompanies();
        if (response.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Search Companies",
            description = "Searches companies by account, account owner, customer name, or email."
    )
    @PostMapping("/search")
    public ResponseEntity<List<Companies>> searchCompanies(String searchQuery) {
        List<Companies> response = companiesService.searchCompanies(searchQuery);
        if (response.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //filter api that will filter the companies according to the field passed
    @Operation(
            summary = "Filter Companies",
            description = "Filters companies based on the provided field and value."
    )
    @PostMapping("/filter")
    public ResponseEntity<List<Companies>> filterCompanies(String field, String value) {
        List<Companies> response = companiesService.filterCompanies(field, value);
        if (response.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Update Company by ID",
            description = "Updates a company in the database using its ID"
    )
    @PutMapping("/update")
    public ResponseEntity<Companies> updateCompanyById(Companies company) {
        Companies response = companiesService.updateCompanyById(company);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete Company by ID",
            description = "Deletes a company from the database using its ID"
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteCompanyById(@PathVariable Long id) {
        Boolean response = companiesService.deleteCompanyById(id);
        if (!response) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
