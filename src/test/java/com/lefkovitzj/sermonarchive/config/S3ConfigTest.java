package com.lefkovitzj.sermonarchive.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;

class S3ConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void shouldCreateS3ClientBeanWhenPropertiesAreProvided() {
        this.contextRunner
                .withUserConfiguration(S3Config.class) // Explicitly load the config class here.
                .withPropertyValues(
                        "S3_ENDPOINT=https://mock-s3-endpoint.com",
                        "S3_ACCESS_KEY=mock-access-key",
                        "S3_SECRET_KEY=mock-secret-key",
                        "S3_REGION=us-east-1"
                )
                .run(context -> {
                    // Verify the context started and registered the client.
                    assertThat(context).hasSingleBean(S3Client.class);

                    S3Client s3Client = context.getBean(S3Client.class);
                    assertThat(s3Client).isNotNull();
                });
    }
    @Test
    void shouldFailToCreateBeanWhenPropertiesAreMissing() {
        this.contextRunner
            .withUserConfiguration(S3Config.class) // Force Spring to load the config.
            .withPropertyValues(
                    "s3.endpoint=",
                    "s3.access-key=",
                    "s3.secret-key="
            )
            .run(context -> {
                // Verify that the Spring context failed to start up.
                assertThat(context).hasFailed();
            });
    }
}