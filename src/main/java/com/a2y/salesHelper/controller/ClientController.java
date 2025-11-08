package com.a2y.salesHelper.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a2y.salesHelper.config.CurrentUser;
import com.a2y.salesHelper.pojo.ClientPojo;
import com.a2y.salesHelper.pojo.ClientResponse;
import com.a2y.salesHelper.service.interfaces.CooldownService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/client")
public class ClientController {

    private final CooldownService cooldownService;

    public ClientController(CooldownService cooldownService) {
        this.cooldownService = cooldownService;
    }

    @Operation(summary = "Add Client", description = "Adds a Client")
    @PostMapping()
    public ResponseEntity<Boolean> addCooldown(@RequestBody ClientPojo client) {
        try {
            Boolean isAdded = cooldownService.addCooldown(client);
            return new ResponseEntity<>(isAdded,
                    Boolean.TRUE.equals(isAdded) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error adding client", e);
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get Clients", description = "Retrieves all the clients for the current tenant.")
    @GetMapping("/get")
    public ResponseEntity<?> getCooldown() {
        try {
            Long tenantId = CurrentUser.getTenantId();
            List<ClientResponse> cooldown = cooldownService.getClients(tenantId);
            if (cooldown == null || cooldown.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No clients found for this tenant");
            }
            return new ResponseEntity<>(cooldown, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving clients", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving clients");
        }
    }

    @Operation(summary = "Edit Cooldown Periods", description = "Edits the cooldown periods for a specific client within the current tenant.")
    @PostMapping("/edit")
    public ResponseEntity<?> editCooldownPeriods(Long clientId, Long cooldownPeriod1, Long cooldownPeriod2,
            Long cooldownPeriod3) {
        try {
            Long tenantId = CurrentUser.getTenantId();
            ClientResponse updatedClient = cooldownService.editCooldownPeriods(clientId, tenantId, cooldownPeriod1,
                    cooldownPeriod2, cooldownPeriod3);
            if (updatedClient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Client not found or you don't have access to this client");
            }
            return new ResponseEntity<>(updatedClient, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error editing cooldown periods for client {}", clientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating cooldown periods");
        }
    }
}
