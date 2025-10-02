package com.shivadnyaconstruction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivadnyaconstruction.dto.EstimateRequest;
import com.shivadnyaconstruction.dto.EstimateResponse;
import com.shivadnyaconstruction.service.EstimateService;

@RestController
@RequestMapping("/api/estimates")
public class EstimateController {

	private final EstimateService service;
    public EstimateController(EstimateService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<EstimateResponse> create(@RequestBody EstimateRequest req) {
        try {
            EstimateResponse resp = service.createAndSendEstimate(req);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
}
