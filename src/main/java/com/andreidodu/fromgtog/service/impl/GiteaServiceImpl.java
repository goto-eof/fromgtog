package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GiteaServiceImpl implements GiteaService {

    private final static Logger log = LoggerFactory.getLogger(GiteaServiceImpl.class);
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "token ";
    private static final String CONTENT_TYPE_JSON = "application/json";

    private static GiteaServiceImpl instance;

    public static GiteaServiceImpl getInstance() {
        if (instance == null) {
            instance = new GiteaServiceImpl();
        }
        return instance;
    }

    @Override
    public GiteaUserDTO getMyself(String token, String urlString) {
        try {
            String giteaApiUrl = urlString + "/api/v1/user";
            URL url = new URL(giteaApiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty(HEADER_AUTHORIZATION, TOKEN_PREFIX + token);
            conn.setRequestProperty(HEADER_ACCEPT, CONTENT_TYPE_JSON);

            int responseCode = conn.getResponseCode();
            log.info("Response Code: {}", responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder response = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            log.info("User Info: {}", response.toString());
            return mapper.readValue(response.toString(), GiteaUserDTO.class);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }
}
