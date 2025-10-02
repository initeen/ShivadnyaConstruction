package com.shivadnyaconstruction.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ContactRequest {

	@NotBlank
	private String name;
	
	@Email
	@NotBlank
	private String email;

	@NotBlank
	private String phone;
	
	private String subject;
	
	@NotBlank
	@Size(min = 5, max = 5000)
	private String message;
	
	private String preferredContactTime;

}
