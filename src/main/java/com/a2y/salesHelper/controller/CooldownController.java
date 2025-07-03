package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.pojo.Cooldown;
import com.a2y.salesHelper.service.interfaces.CooldownService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController( "api/cooldown" )
public class CooldownController {

    private final CooldownService cooldownService;

    public CooldownController(CooldownService cooldownService) {
        this.cooldownService = cooldownService;
    }

    @PostMapping()
    public ResponseEntity<Boolean> addCooldown(@RequestBody Cooldown cooldown) {
        Boolean isAdded = cooldownService.addCooldown(cooldown);
        return new ResponseEntity<>(isAdded, Boolean.TRUE.equals(isAdded) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }
}
