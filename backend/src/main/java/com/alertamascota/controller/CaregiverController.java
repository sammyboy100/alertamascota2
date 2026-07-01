package com.alertamascota.controller;

import com.alertamascota.model.Caregiver;
import com.alertamascota.service.CaregiverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuidadores")
@RequiredArgsConstructor
public class CaregiverController {

    private final CaregiverService service;

    @GetMapping
    public ResponseEntity<List<Caregiver>> getVerified() {
        return ResponseEntity.ok(service.getVerified());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Caregiver>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<Caregiver> register(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.register(body));
    }

    @PatchMapping("/{id}/alertas")
    public ResponseEntity<Caregiver> toggleAlerts(
            @PathVariable String id,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok(service.toggleAlerts(id, enabled));
    }

    @PatchMapping("/{id}/verificar")
    public ResponseEntity<Caregiver> verify(@PathVariable String id) {
        return ResponseEntity.ok(service.verify(id));
    }
}
