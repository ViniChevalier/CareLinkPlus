package com.carelink.medicalhistory.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AzureBlobService {

    private final BlobServiceClient blobServiceClient;
    private final String containerName = "medicalrecords";

    public AzureBlobService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    public String uploadFile(MultipartFile file) throws Exception {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        if (!containerClient.exists()) {
            containerClient.create();
        }

        BlobClient blobClient = containerClient.getBlobClient(file.getOriginalFilename());

        blobClient.upload(file.getInputStream(), file.getSize(), true);

        return blobClient.getBlobUrl();
    }
}