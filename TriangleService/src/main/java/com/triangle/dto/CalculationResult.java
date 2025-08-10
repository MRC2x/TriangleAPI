package com.triangle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CalculationResult {
    
    @JsonProperty("result")
    private double result;
    
    public CalculationResult() {}
    
    public CalculationResult(double result) {
        this.result = result;
    }
    
    public double getResult() {
        return result;
    }
    
    public void setResult(double result) {
        this.result = result;
    }
} 