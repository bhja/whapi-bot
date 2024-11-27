package com.whapi.bot.config;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class Config {
    @Value("${whapi.api.url}")
    String whapiApiUrl;
    @Value("${whapi.api.token}")
    String apiToken;

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient.Builder().build();
    }

    @Value("${whapi.file.path}")
    private String filePath;
}
