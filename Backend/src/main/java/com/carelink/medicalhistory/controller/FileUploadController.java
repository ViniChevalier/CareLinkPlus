package com.carelink.medicalhistory.controller;

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
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        String url = azureBlobService.uploadFile(file);
        return ResponseEntity.ok(url);
    }
}