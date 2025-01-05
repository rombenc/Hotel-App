package com.team2.djavaluxury.config;


import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        String CLOUD_NAME = "@djava-luxury";
        config.put("cloud_name", CLOUD_NAME);
        String API_KEY = "795916379757119";
        config.put("api_key", API_KEY);
        String API_SECRET = "0Dkwl0kDau8SfSPGTHtmgGhexZ4";
        config.put("api_secret", API_SECRET);
        return new Cloudinary(config);
    }
}
