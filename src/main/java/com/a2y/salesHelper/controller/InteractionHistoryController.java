package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.pojo.EditRequest;
import com.a2y.salesHelper.pojo.InteractionHistory;
import com.a2y.salesHelper.service.interfaces.InteractionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

@RestController()
@RequestMapping("/api/history")
public class InteractionHistoryController {

    private final InteractionHistoryService interactionHistoryService;

    public InteractionHistoryController(InteractionHistoryService interactionHistoryService) {
        this.interactionHistoryService = interactionHistoryService;
    }

    @Operation(
            summary = "Add Interaction History",
            description = "Adds an interaction history entry for a participant with the specified date and details."
    )
    @PostMapping("/edit")
    public ResponseEntity<Boolean> editInteractionHistory(@RequestBody EditRequest editRequest) {
        Boolean isAdded =  interactionHistoryService.editInteractionHistory(editRequest);

        return new ResponseEntity<>(isAdded, Boolean.TRUE.equals(isAdded) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    @Operation(
            summary = "Get Interaction History",
            description = "Retrieves the interaction history for a participant by their ID."
    )
    @GetMapping("/get")
    public ResponseEntity<List<InteractionHistory>> getInteractionHistory(@RequestParam String participantName,
                                                                          @RequestParam String organization) {
        List<InteractionHistory> interactionHistory = interactionHistoryService.getInteractionHistory(participantName, organization);

        if (interactionHistory.isEmpty()) {
            return new ResponseEntity<>(interactionHistory, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(interactionHistory, HttpStatus.OK);
    }

    @Operation(
            summary = "Add Interaction History",
            description = "ADDS an interaction history entry for a participant."
    )
    @PostMapping("/add")
    public ResponseEntity<Boolean> addInteractionHistory(@RequestBody InteractionHistory interactionHistory) {
        Boolean isAdded = interactionHistoryService.addInteractionHistory(interactionHistory);

        return new ResponseEntity<>(isAdded, Boolean.TRUE.equals(isAdded) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

}
