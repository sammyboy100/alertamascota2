package com.alertamascota.controller;

import com.alertamascota.model.Alert;
import com.alertamascota.model.Sighting;
import com.alertamascota.patterns.facade.AlertFacade;
import com.alertamascota.service.strategy.SearchContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertController {

    private final AlertFacade alertFacade;
    private final SearchContext searchContext;

    @PostMapping
    public ResponseEntity<Alert> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(alertFacade.createAlert(body));
    }

    @GetMapping
    public ResponseEntity<List<Alert>> getActive() {
        return ResponseEntity.ok(alertFacade.getActiveAlerts());
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<Alert> resolve(@PathVariable String id) {
        return ResponseEntity.ok(alertFacade.resolveAlert(id));
    }

    /** RF 1.3 — avistamiento */
    @PostMapping("/avistamientos")
    public ResponseEntity<Sighting> sighting(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(alertFacade.addSighting(body));
    }

    @GetMapping("/{id}/avistamientos")
    public ResponseEntity<List<Sighting>> getSightings(@PathVariable String id) {
        return ResponseEntity.ok(alertFacade.getSightings(id));
    }

    /** Strategy — búsqueda por criterio */
    @GetMapping("/buscar")
    public ResponseEntity<List<Alert>> search(
            @RequestParam(defaultValue = "all") String tipo,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed) {
        Map<String, String> criteria = Map.of(
                "species", species != null ? species : "",
                "breed",   breed   != null ? breed   : ""
        );
        return ResponseEntity.ok(searchContext.search(tipo, criteria));
    }
}
