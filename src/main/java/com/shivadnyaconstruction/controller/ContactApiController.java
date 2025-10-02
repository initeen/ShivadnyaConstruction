package com.shivadnyaconstruction.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shivadnyaconstruction.model.ContactRequest;
import com.shivadnyaconstruction.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ContactApiController {

	private final ContactService contactService;

	public ContactApiController(ContactService contactService) {
		this.contactService = contactService;
	}

	@PostMapping("/contact")
	public ResponseEntity<?> submit(@Valid @RequestBody ContactRequest req) {
		System.out.println("########Received contact: " + req);
		contactService.recordInquiry(req);
		return ResponseEntity.ok().body("Contact request received successfully");
	}
}
