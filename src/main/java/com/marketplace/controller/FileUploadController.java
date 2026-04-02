package com.marketplace.controller;

import com.marketplace.dto.ApiResponse;
import com.marketplace.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for File Upload operations
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload product image
     */
    @PostMapping("/product-image")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> uploadProductImage(@RequestParam("file") MultipartFile file) {
        log.info("Uploading product image: {}", file.getOriginalFilename());
        
        String imagePath = fileUploadService.uploadProductImage(file);
        
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", imagePath));
    }

    /**
     * Delete product image
     */
    @DeleteMapping("/product-image")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(@RequestParam String imagePath) {
        log.info("Deleting product image: {}", imagePath);
        
        fileUploadService.deleteProductImage(imagePath);
        
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
    }
}
