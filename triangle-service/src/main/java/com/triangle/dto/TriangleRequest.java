package com.triangle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TriangleRequest {
    
    @JsonProperty("separator")
    private String separator = ";"; // Default separator
    
    @JsonProperty("input")
    private String input;
    
    public TriangleRequest() {}
    
    public TriangleRequest(String separator, String input) {
        this.separator = separator;
        this.input = input;
    }
    
    public String getSeparator() {
        return separator;
    }
    
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    
    public String getInput() {
        return input;
    }
    
    public void setInput(String input) {
        this.input = input;
    }
} 