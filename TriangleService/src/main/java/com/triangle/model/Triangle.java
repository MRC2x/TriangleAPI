package com.triangle.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Triangle {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("firstSide")
    private double firstSide;
    
    @JsonProperty("secondSide")
    private double secondSide;
    
    @JsonProperty("thirdSide")
    private double thirdSide;
    
    public Triangle() {}
    
    public Triangle(String id, double firstSide, double secondSide, double thirdSide) {
        this.id = id;
        this.firstSide = firstSide;
        this.secondSide = secondSide;
        this.thirdSide = thirdSide;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public double getFirstSide() {
        return firstSide;
    }
    
    public void setFirstSide(double firstSide) {
        this.firstSide = firstSide;
    }
    
    public double getSecondSide() {
        return secondSide;
    }
    
    public void setSecondSide(double secondSide) {
        this.secondSide = secondSide;
    }
    
    public double getThirdSide() {
        return thirdSide;
    }
    
    public void setThirdSide(double thirdSide) {
        this.thirdSide = thirdSide;
    }
    
    // Validation methods
    public boolean isValid() {
        return firstSide > 0 && secondSide > 0 && thirdSide > 0 &&
               firstSide + secondSide > thirdSide &&
               firstSide + thirdSide > secondSide &&
               secondSide + thirdSide > firstSide;
    }
    
    public double getPerimeter() {
        return firstSide + secondSide + thirdSide;
    }
    
    public double getArea() {
        // Using Heron's formula
        double s = getPerimeter() / 2;
        return Math.sqrt(s * (s - firstSide) * (s - secondSide) * (s - thirdSide));
    }
} 