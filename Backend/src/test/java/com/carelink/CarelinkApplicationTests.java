package com.carelink;

import com.carelink.medicalhistory.service.AzureBlobService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CarelinkApplicationTests {

    @MockBean
    private AzureBlobService azureBlobService;

    @Test
    void contextLoads() {
    }
}