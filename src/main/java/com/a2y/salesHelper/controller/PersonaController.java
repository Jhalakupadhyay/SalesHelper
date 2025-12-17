package com.a2y.salesHelper.controller;

import java.io.IOException;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a2y.salesHelper.config.CurrentUser;
import com.a2y.salesHelper.exception.ExcelValidationException;
import com.a2y.salesHelper.pojo.Persona;
import com.a2y.salesHelper.service.interfaces.PersonaService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/persona")
@Slf4j
public class PersonaController {

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
            if (fileName == null
                    || (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls"))) {
                return ResponseEntity.badRequest().body("Only Excel files (.xlsx, .xls) are supported");
            }

            Long tenantId = CurrentUser.getTenantId();
            Integer parsedCount = companyContactService.parseExcelFile(file, clientId, tenantId);

            return ResponseEntity.ok().body("Successfully parsed and saved " + parsedCount + " company contacts");

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(e.getMessage());
        } catch (ExcelValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            log.error("Error parsing file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error parsing file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during file upload", e);
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
            Long tenantId = CurrentUser.getTenantId();
            List<Persona> contacts = companyContactService.getAllCompanyContacts(clientId, tenantId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("Error retrieving company contacts for client {}", clientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a company contact by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompanyContact(@PathVariable Long id, @RequestParam Long clientId) {
        try {
            Long tenantId = CurrentUser.getTenantId();
            Boolean deleted = companyContactService.deleteCompanyContactById(id, clientId, tenantId);
            if (deleted) {
                return ResponseEntity.ok().body("Company contact deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to delete company contact");
            }
        } catch (Exception e) {
            log.error("Error deleting company contact with ID {}", id, e);
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
            log.error("Error updating company contact with ID {}", id, e);
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
            Long tenantId = CurrentUser.getTenantId();
            List<Persona> contacts = companyContactService.searchByCompany(company, clientId, tenantId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("Error searching by company '{}' for client {}", company, clientId, e);
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
            Long tenantId = CurrentUser.getTenantId();
            List<Persona> contacts = companyContactService.searchByName(name, clientId, tenantId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("Error searching by name '{}' for client {}", name, clientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Delete multiple persona by IDs", description = "Deletes multiple persona from the database using their IDs")
    @PostMapping("/bulkDelete")
    public ResponseEntity<Boolean> deleteMultiplePersonaByIds(@RequestParam List<Long> ids, @RequestParam Long clientId) {
        Long tenantId = CurrentUser.getTenantId();
        Boolean response = companyContactService.deleteMultiplePersonaByIds(ids, clientId, tenantId);
        if (!response) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
