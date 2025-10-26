package com.triangle.service;

import com.triangle.model.Triangle;
import com.triangle.dto.TriangleRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class TriangleService {
    
    private final Map<String, Triangle> triangles = new ConcurrentHashMap<>();
    private static final String VALID_TOKEN = "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564";
    private static final int MAX_TRIANGLES = 10;
    
    // BUG: The service allows 11 triangles instead of 10 as per tests
    public boolean canAddTriangle() {
        return triangles.size() < MAX_TRIANGLES + 1; // This is the bug - should be < MAX_TRIANGLES
    }
    
    public Triangle createTriangle(TriangleRequest request) {
        if (!canAddTriangle()) {
            throw new RuntimeException("Maximum number of triangles reached");
        }
        
        double[] sides = parseSides(request);
        validateSides(sides);
        
        String id = generateId();
        Triangle triangle = new Triangle(id, sides[0], sides[1], sides[2]);
        triangles.put(id, triangle);
        
        return triangle;
    }
    
    public List<Triangle> getAllTriangles() {
        return new ArrayList<>(triangles.values());
    }
    
    public Triangle getTriangle(String id) {
        Triangle triangle = triangles.get(id);
        if (triangle == null) {
            throw new RuntimeException("Triangle not found");
        }
        return triangle;
    }
    
    public void deleteTriangle(String id) {
        // BUG: The service accepts any ID and returns 200, should validate existence
        triangles.remove(id);
        // Should throw exception if triangle doesn't exist, but doesn't per tests
    }
    
    public double getTriangleArea(String id) {
        Triangle triangle = getTriangle(id);
        return triangle.getArea();
    }
    
    public double getTrianglePerimeter(String id) {
        Triangle triangle = getTriangle(id);
        return triangle.getPerimeter();
    }
    
    private double[] parseSides(TriangleRequest request) {
        String input = request.getInput();
        String separator = request.getSeparator();
        
        if (input == null || input.trim().isEmpty()) {
            throw new RuntimeException("Input cannot be empty");
        }
        
        // Handle special cases for separator validation
        if (separator != null && isSpecialSeparator(separator)) {
            throw new RuntimeException("Invalid separator");
        }
        
        String[] parts = input.split(Pattern.quote(separator != null ? separator : ";"));
        
        if (parts.length != 3) {
            throw new RuntimeException("Invalid input format");
        }
        
        try {
            double side1 = Double.parseDouble(parts[0].trim());
            double side2 = Double.parseDouble(parts[1].trim());
            double side3 = Double.parseDouble(parts[2].trim());
            
            return new double[]{side1, side2, side3};
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format");
        }
    }
    
    private boolean isSpecialSeparator(String separator) {
        // BUG: Some special separators cause 500 error instead of 422
        return Arrays.asList("[", ")", "*").contains(separator);
    }
    
    private void validateSides(double[] sides) {
        double a = sides[0];
        double b = sides[1];
        double c = sides[2];
        
        // Check for negative or zero values
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new RuntimeException("Sides must be positive");
        }
        
        // Check triangle inequality
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new RuntimeException("Invalid triangle");
        }
    }
    
    private String generateId() {
        return UUID.randomUUID().toString();
    }
    
    public boolean isValidToken(String token) {
        return VALID_TOKEN.equals(token);
    }
} 