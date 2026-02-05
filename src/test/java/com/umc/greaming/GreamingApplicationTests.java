package com.umc.greaming;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
class GreamingApplicationTests {

    @MockitoBean
    private S3Presigner s3Presigner;

    @Test
    void contextLoads() {
    }

}
