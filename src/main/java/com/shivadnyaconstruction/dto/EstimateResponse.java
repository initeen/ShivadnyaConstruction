package com.shivadnyaconstruction.dto;

import lombok.Data;

@Data
public class EstimateResponse {

	public String message;
	public double estimatedCost;
	public String pdfFile; // server path

}
