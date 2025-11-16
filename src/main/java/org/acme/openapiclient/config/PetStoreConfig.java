package org.acme.openapiclient.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * Configuration properties for the Pet Store API client.
 */
@ConfigMapping(prefix = "petstore.api")
public interface PetStoreConfig {

    /**
     * The base URL of the Pet Store API.
     *
     * @return The API URL
     */
    @WithName("url")
    @WithDefault("http://localhost:8080")
    String url();

    /**
     * Connection timeout in milliseconds.
     *
     * @return The connection timeout
     */
    @WithName("connect-timeout")
    @WithDefault("5000")
    int connectTimeout();

    /**
     * Read timeout in milliseconds.
     *
     * @return The read timeout
     */
    @WithName("read-timeout")
    @WithDefault("30000")
    int readTimeout();
}

