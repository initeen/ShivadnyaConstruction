package com.shivadnyaconstruction.service;

import com.shivadnyaconstruction.dto.EstimateRequest;
import com.shivadnyaconstruction.dto.EstimateResponse;

public interface EstimateService {

	EstimateResponse createAndSendEstimate(EstimateRequest req) throws Exception;
}
