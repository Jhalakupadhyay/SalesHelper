package com.a2y.salesHelper.controller;

import java.io.IOException;
import java.util.List;

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

import com.a2y.salesHelper.db.entity.ParticipantEntity;
import com.a2y.salesHelper.pojo.Participant;
import com.a2y.salesHelper.service.interfaces.ParticipantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/excel")
@Tag(name = "Participant Excel Parser", description = "All the API related to Excel parsing of Participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload the Excel Sheets", description = "Accepts Multipart file and Parses it to save data in DB")
    public ResponseEntity<String> uploadExcelFile(MultipartFile file, @RequestParam Long clientId,
            @RequestParam Long tenantId) {
        try {
            int processedCount = participantService.parseExcelFile(file, clientId, tenantId);
            return new ResponseEntity<>("Successfully processed " + processedCount + " participants.", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to process the file: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gives all The participant stored in the DB", description = "Returns all the Participants stored in DB")
    @GetMapping()
    public ResponseEntity<List<Participant>> getAllParticipants(@RequestParam Long clientId,
            @RequestParam Long tenantId) {

        List<Participant> response = participantService.getAllParticipant(clientId);
        if (response.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a participant by ID", description = "Deletes a participant from the database using their ID")
    public ResponseEntity<Boolean> deleteParticipantById(@PathVariable Long id, @RequestParam Long clientId,
            @RequestParam Long tenantId) {
        Boolean response = participantService.deleteParticipantById(id);
        return new ResponseEntity<>(response, response ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PutMapping()
    @Operation(summary = "Update a participant by ID", description = "Updates a participant's details in the database")
    public ResponseEntity<Boolean> updateParticipantById(@RequestBody Participant participant) {
        Boolean response = participantService.updateParticipantById(participant);
        return new ResponseEntity<>(response, response ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    @Operation(summary = "Search participants by name", description = "Returns a list of participants whose names contain the specified search term")
    public ResponseEntity<List<Participant>> searchParticipantsByName(@RequestParam String name,
            @RequestParam Long clientId, @RequestParam Long tenantId) {
        List<Participant> participants = participantService.searchParticipant(name, clientId);
        List<Participant> filteredParticipants = participants.stream()
                .filter(participant -> participant.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();

        if (filteredParticipants.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(filteredParticipants, HttpStatus.OK);
    }

    // filter api that will filter the participants according to the field passed
    @PostMapping("/filter")
    @Operation(summary = "Filter Participants", description = "Filters participants based on the provided field and value.")
    public ResponseEntity<List<Participant>> filterParticipants(@RequestParam String field, @RequestParam String value,
            @RequestParam Long clientId, @RequestParam Long tenantId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Participant> response = participantService.filterParticipants(field, value, clientId, startDate, endDate);
        if (response.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get Clients on the basis of companies", description = "getting clients on the basis of orgId.")
    @GetMapping("/getClientsForOrganization")
    public ResponseEntity<List<ParticipantEntity>> getClientsForOrganization(@RequestParam Long orgId,
            @RequestParam Long clientId, @RequestParam Long tenantId) {
        List<ParticipantEntity> response = participantService.getParticipantsForOrganization(orgId, clientId);
        if (response.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
