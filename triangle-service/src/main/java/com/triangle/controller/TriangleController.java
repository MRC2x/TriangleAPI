package com.triangle.controller;

import com.triangle.dto.*;
import com.triangle.model.Triangle;
import com.triangle.service.TriangleService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/triangle")
@Tag(name = "Triangle API", description = "Operations for managing triangles")
public class TriangleController {
    
    @Autowired
    private TriangleService triangleService;
    
    @PostMapping("/")
    @Operation(
        summary = "Create a new triangle",
        description = "Creates a new triangle with the specified side lengths. Maximum 10 triangles allowed."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Triangle created successfully",
            content = @Content(schema = @Schema(implementation = Triangle.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid input format",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Invalid triangle data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> createTriangle(
        @Parameter(description = "Triangle request with sides and separator", 
                  content = @Content(examples = {
                      @ExampleObject(name = "Valid Triangle", value = "{\"separator\": \";\", \"input\": \"3;4;5\"}"),
                      @ExampleObject(name = "Without Separator", value = "{\"input\": \"3;4;5\"}")
                  }))
        @RequestBody(required = false) TriangleRequest request,
        HttpServletRequest httpRequest) {
        
        // Check authentication
        String token = httpRequest.getHeader("X-User");

        if (!triangleService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Invalid or missing token"));
        }
        
        // Handle missing request body
        if (request == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Bad Request", "Required request body is missing"));
        }
        
        try {
            Triangle triangle = triangleService.createTriangle(request);
            return ResponseEntity.ok(triangle);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message.contains("Maximum number of triangles reached")) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new ErrorResponse("Unprocessable Entity", "Cannot process input"));
            } else if (message.contains("Invalid separator")) {
                // BUG: Some separators cause 500 error instead of 422
                if (request.getSeparator() != null && 
                    (request.getSeparator().equals("[") || 
                     request.getSeparator().equals(")") || 
                     request.getSeparator().equals("*"))) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ErrorResponse("Internal Server Error", "Server error"));
                }
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new ErrorResponse("Unprocessable Entity", "Cannot process input"));
            } else if (message.contains("Sides must be positive") || 
                       message.contains("Invalid triangle")) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new ErrorResponse("Unprocessable Entity", "Cannot process input"));
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new ErrorResponse("Unprocessable Entity", "Cannot process input"));
            }
        }
    }
    
    @GetMapping("/all")
    @Operation(
        summary = "Get all triangles",
        description = "Retrieves a list of all existing triangles"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of triangles retrieved successfully",
            content = @Content(schema = @Schema(implementation = Triangle.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getAllTriangles(HttpServletRequest httpRequest) {
        // Check authentication
        String token = httpRequest.getHeader("X-User");

        if (!triangleService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Invalid or missing token"));
        }
        
        List<Triangle> triangles = triangleService.getAllTriangles();
        return ResponseEntity.ok(triangles);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get triangle by ID",
        description = "Retrieves a specific triangle by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Triangle retrieved successfully",
            content = @Content(schema = @Schema(implementation = Triangle.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Triangle not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getTriangle(
        @Parameter(description = "Triangle ID", example = "uuid-string")
        @PathVariable("id") String id,
        HttpServletRequest httpRequest) {
        // Check authentication
        String token = httpRequest.getHeader("X-User");

        if (!triangleService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Invalid or missing token"));
        }
        
        try {
            Triangle triangle = triangleService.getTriangle(id);
            return ResponseEntity.ok(triangle);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", "Not Found"));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete triangle by ID",
        description = "Deletes a specific triangle by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Triangle deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> deleteTriangle(
        @Parameter(description = "Triangle ID", example = "uuid-string")
        @PathVariable("id") String id,
        HttpServletRequest httpRequest) {
        // Check authentication
        String token = httpRequest.getHeader("X-User");

        if (!triangleService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Invalid or missing token"));
        }
        
        // BUG: The service accepts any ID and returns 200, should validate existence
        triangleService.deleteTriangle(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/area")
    @Operation(
        summary = "Get triangle area",
        description = "Calculates and returns the area of a specific triangle using Heron's formula"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Triangle area calculated successfully",
            content = @Content(schema = @Schema(implementation = CalculationResult.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Triangle not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getTriangleArea(
        @Parameter(description = "Triangle ID", example = "uuid-string")
        @PathVariable("id") String id,
        HttpServletRequest httpRequest) {
        // Check authentication
        String token = httpRequest.getHeader("X-User");

        if (!triangleService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Invalid or missing token"));
        }
        
        try {
            double area = triangleService.getTriangleArea(id);
            return ResponseEntity.ok(new CalculationResult(area));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", "Not Found"));
        }
    }
    
    @GetMapping("/{id}/perimeter")
    @Operation(
        summary = "Get triangle perimeter",
        description = "Calculates and returns the perimeter of a specific triangle"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Triangle perimeter calculated successfully",
            content = @Content(schema = @Schema(implementation = CalculationResult.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Triangle not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getTrianglePerimeter(
        @Parameter(description = "Triangle ID", example = "uuid-string")
        @PathVariable("id") String id,
        HttpServletRequest httpRequest) {
        // Check authentication
        String token = httpRequest.getHeader("X-User");

        if (!triangleService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Invalid or missing token"));
        }
        
        try {
            double perimeter = triangleService.getTrianglePerimeter(id);
            return ResponseEntity.ok(new CalculationResult(perimeter));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", "Not Found"));
        }
    }
    
    // Handle unsupported HTTP methods
    @Operation(hidden = true)
    @Hidden
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> unsupportedMethods() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("Method Not Allowed", "Request method not supported"));
    }

    @Operation(hidden = true)
    @Hidden
    @RequestMapping(value = "/all", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> unsupportedMethodsForAll() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("Method Not Allowed", "Request method not supported"));
    }

    @Operation(hidden = true)
    @Hidden
    @RequestMapping(value = "/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> unsupportedMethodsForId() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("Method Not Allowed", "Request method not supported"));
    }

    @Operation(hidden = true)
    @Hidden
    @RequestMapping(value = "/{id}/area", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> unsupportedMethodsForArea() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("Method Not Allowed", "Request method not supported"));
    }

    @Operation(hidden = true)
    @Hidden
    @RequestMapping(value = "/{id}/perimeter", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> unsupportedMethodsForPerimeter() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("Method Not Allowed", "Request method not supported"));
    }
}
