package com.carelink.medicalhistory.controller;

import com.carelink.exception.BusinessLogicException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.carelink.medicalhistory.service.AzureBlobService;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final AzureBlobService azureBlobService;

    public FileUploadController(AzureBlobService azureBlobService) {
        this.azureBlobService = azureBlobService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "generic-files") String folder) {
        try {
            String url = azureBlobService.uploadFile(file, folder);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            throw new BusinessLogicException("Failed to upload file: " + e.getMessage());
        }
    }
}