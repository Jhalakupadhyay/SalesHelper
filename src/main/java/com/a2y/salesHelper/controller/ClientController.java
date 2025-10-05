package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.pojo.ClientPojo;
import com.a2y.salesHelper.pojo.ClientResponse;
import com.a2y.salesHelper.service.interfaces.CooldownService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/client")
public class ClientController {

    private final CooldownService cooldownService;

    public ClientController(CooldownService cooldownService) {
        this.cooldownService = cooldownService;
    }

    @Operation(
            summary = "Add Client",
            description = "Adds a Client"
    )
    @PostMapping()
    public ResponseEntity<Boolean> addCooldown(@RequestBody ClientPojo client) {
        Boolean isAdded = cooldownService.addCooldown(client);
        return new ResponseEntity<>(isAdded, Boolean.TRUE.equals(isAdded) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    @Operation(
            summary = "Get Clients",
            description = "Retrieves all the clients from the database."
    )
    @GetMapping("/get")
    public ResponseEntity<List<ClientResponse>> getCooldown(@RequestParam Long tenantId) {
        List<ClientResponse> cooldown = cooldownService.getClients(tenantId);
        if (cooldown == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cooldown, HttpStatus.OK);
    }

    @Operation(
            summary = "Edit Cooldown Periods",
            description = "Edits the cooldown periods for a specific client."
    )
    @PostMapping("/edit")
    public ResponseEntity<ClientResponse> editCooldownPeriods(Long clientId, Long tenantId, Long cooldownPeriod1, Long cooldownPeriod2, Long cooldownPeriod3) {
        ClientResponse updatedClient = cooldownService.editCooldownPeriods(clientId,tenantId, cooldownPeriod1, cooldownPeriod2, cooldownPeriod3);
        if (updatedClient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }
}
