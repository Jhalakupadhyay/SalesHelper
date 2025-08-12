package com.a2y.salesHelper.controller;


import com.a2y.salesHelper.pojo.Persona;
import com.a2y.salesHelper.service.interfaces.PersonaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/persona")
@Slf4j
public class  PersonaController {

    private final PersonaService companyContactService;

    public PersonaController(PersonaService companyContactService) {
        this.companyContactService = companyContactService;
    }

    /**
     * Upload and parse Excel file containing company contacts
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCompanyContactsFile(
            MultipartFile file,
            @RequestParam("clientId") Long clientId) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls"))) {
                return ResponseEntity.badRequest().body("Only Excel files (.xlsx, .xls) are supported");
            }

            Integer parsedCount = companyContactService.parseExcelFile(file, clientId);

            return ResponseEntity.ok().body("Successfully parsed and saved " + parsedCount + " company contacts");

        } catch (IOException e) {
            log.error("Error parsing file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error parsing file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Get all company contacts for a client
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<List<Persona>> getAllCompanyContacts(@PathVariable Long clientId) {
        try {
            List<Persona> contacts = companyContactService.getAllCompanyContacts(clientId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("Error retrieving company contacts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a company contact by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompanyContact(@PathVariable Long id) {
        try {
            Boolean deleted = companyContactService.deleteCompanyContactById(id);
            if (deleted) {
                return ResponseEntity.ok().body("Company contact deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to delete company contact");
            }
        } catch (Exception e) {
            log.error("Error deleting company contact: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting company contact: " + e.getMessage());
        }
    }

    /**
     * Update a company contact
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompanyContact(
            @PathVariable Long id,
            @RequestBody Persona companyContact) {

        try {
            companyContact.setId(id);
            Boolean updated = companyContactService.updateCompanyContactById(companyContact);

            if (updated) {
                return ResponseEntity.ok().body("Company contact updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to update company contact");
            }
        } catch (Exception e) {
            log.error("Error updating company contact: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating company contact: " + e.getMessage());
        }
    }

    /**
     * Search company contacts by company name
     */
    @GetMapping("/{clientId}/search/company")
    public ResponseEntity<List<Persona>> searchByCompany(
            @PathVariable Long clientId,
            @RequestParam String company) {

        try {
            List<Persona> contacts = companyContactService.searchByCompany(company, clientId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("Error searching by company: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search company contacts by person name
     */
    @GetMapping("/{clientId}/search/name")
    public ResponseEntity<List<Persona>> searchByName(
            @PathVariable Long clientId,
            @RequestParam String name) {

        try {
            List<Persona> contacts = companyContactService.searchByName(name, clientId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("Error searching by name: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
