package com.andreidodu.fromgtog.service.impl;

import com.andreidodu.fromgtog.dto.gitea.GiteaRepositoryDTO;
import com.andreidodu.fromgtog.dto.gitea.GiteaUserDTO;
import com.andreidodu.fromgtog.exception.CloningSourceException;
import com.andreidodu.fromgtog.service.GiteaService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GiteaServiceImpl implements GiteaService {

    private final static Logger log = LoggerFactory.getLogger(GiteaServiceImpl.class);
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "token ";
    private static final String CONTENT_TYPE_JSON = "application/json";

    private static GiteaService instance;

    public static GiteaService getInstance() {
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

    @Override
    public List<GiteaRepositoryDTO> tryToRetrieveUserRepositories(String baseUrl, String token) {
        try {
            List<GiteaRepositoryDTO> repositoryDTOList = new ArrayList<>();
            loadByPage(baseUrl, token, 1, repositoryDTOList);
            return repositoryDTOList;
        } catch (Exception e) {
            log.error("Failed to fetch repositories", e);
            throw new CloningSourceException("failed to fetch repositories from Gitea", e);
        }
    }

    private void loadByPage(String baseUrl, String token, int pageNumber, List<GiteaRepositoryDTO> repositoryDTOList) {
        List<GiteaRepositoryDTO> repos = fetchRepos(baseUrl + "/api/v1/user/repos?type=owner&page=" + pageNumber + "&limit=1000", token);
        if (repos.isEmpty()) {
            return;
        }
        repositoryDTOList.addAll(repos);
        loadByPage(baseUrl, token, pageNumber + 1, repositoryDTOList);
    }


    @Override
    public List<GiteaRepositoryDTO> tryToRetrieveStarredRepositories(String baseUrl, String token) {
        try {
            return fetchRepos(baseUrl + "/api/v1/user/starred", token);
        } catch (Exception e) {
            log.error("Failed to fetch repositories", e);
            throw new CloningSourceException("failed to fetch starred repositories from Gitea", e);
        }
    }

    private List<GiteaRepositoryDTO> fetchRepos(String apiUrl, String token) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty(HEADER_AUTHORIZATION, TOKEN_PREFIX + token);
            conn.setRequestProperty(HEADER_ACCEPT, CONTENT_TYPE_JSON);

            int responseCode = conn.getResponseCode();
            log.info("Fetching {} -> Status: {}", apiUrl, responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to fetch: " + apiUrl);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String stringResponse = response.toString();
            log.info("GiteaRepositoryDTO: {}", stringResponse);
            List<GiteaRepositoryDTO> giteaRepositoryDTOS = mapper.readValue(stringResponse, new TypeReference<List<GiteaRepositoryDTO>>() {
            });
            return new ArrayList<>(giteaRepositoryDTOS.stream().toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
