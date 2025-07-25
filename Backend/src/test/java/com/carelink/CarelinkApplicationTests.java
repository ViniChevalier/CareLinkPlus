package com.carelink;

import com.azure.storage.blob.BlobServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CarelinkApplicationTests {

    @MockBean
    private BlobServiceClient blobServiceClient;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void contextLoads() {
    }

}