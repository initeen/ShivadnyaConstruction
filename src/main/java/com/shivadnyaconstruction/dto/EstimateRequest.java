package com.shivadnyaconstruction.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EstimateRequest {

	public String name;
	public String email;
	public String phone;
	public String cityArea;
	public String plotRange;
	public String slabRange;
	public int floors;
	public String workType;
	public String material;
	public String quality;
	public Map<String, Object> selections;
	public boolean includeConnections = true;
	public String notes;
	
}
