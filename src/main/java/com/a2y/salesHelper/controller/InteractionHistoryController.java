package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.pojo.InteractionHistory;
import com.a2y.salesHelper.service.interfaces.InteractionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController("api/interaction")
public class InteractionHistoryController {

    private final InteractionHistoryService interactionHistoryService;

    public InteractionHistoryController(InteractionHistoryService interactionHistoryService) {
        this.interactionHistoryService = interactionHistoryService;
    }

    @Operation(
            summary = "Add Interaction History",
            description = "Adds an interaction history entry for a participant with the specified date and details."
    )
    @PostMapping("/add")
    public ResponseEntity<Boolean> addInteractionHistory(Long participantId, OffsetDateTime interactionDate, String interactionDetails) {
        Boolean isAdded =  interactionHistoryService.addInteractionHistory(participantId, interactionDate, interactionDetails);

        return new ResponseEntity<>(isAdded, Boolean.TRUE.equals(isAdded) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    @Operation(
            summary = "Get Interaction History",
            description = "Retrieves the interaction history for a participant by their ID."
    )
    @GetMapping("/get")
    public ResponseEntity<List<InteractionHistory>> getInteractionHistory(Long participantId) {
        List<InteractionHistory> interactionHistory = interactionHistoryService.getInteractionHistory(participantId);

        if (interactionHistory.isEmpty()) {
            return new ResponseEntity<>(interactionHistory, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(interactionHistory, HttpStatus.OK);
    }
}
