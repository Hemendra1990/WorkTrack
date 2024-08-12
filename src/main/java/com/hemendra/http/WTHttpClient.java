package com.hemendra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hemendra.component.WorkTrackProperties;
import com.hemendra.dto.UserActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WTHttpClient {
    private final WorkTrackProperties workTrackProperties;
    private final ObjectMapper objectMapper;


    public WTHttpClient(WorkTrackProperties workTrackProperties, ObjectMapper objectMapper) {
        this.workTrackProperties = workTrackProperties;
        this.objectMapper = objectMapper;
    }


    public void logUserActivity(UserActivityDto activityDto) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String requestJson = objectMapper.writeValueAsString(activityDto);
            HttpPost httpPost = new HttpPost(workTrackProperties.getServerUserActivityUrl());
            StringEntity entity = new StringEntity(requestJson);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                log.info("Response status code: {}", response.getStatusLine().getStatusCode());
                log.info("Response body: {}", responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
