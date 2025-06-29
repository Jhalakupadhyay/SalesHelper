package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.pojo.Companies;
import com.a2y.salesHelper.service.interfaces.CompaniesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
