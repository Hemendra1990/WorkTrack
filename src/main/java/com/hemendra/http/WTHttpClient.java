package com.hemendra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hemendra.component.WorkTrackProperties;
import com.hemendra.dto.UserActivityDto;
import com.hemendra.dto.UserWebsiteActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Component;

import java.io.File;
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
                log.info("Response status code: {}", response.getCode());
                log.info("Response body: {}", responseBody);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void logUserWebsiteActivity(UserWebsiteActivityDto websiteActivityDto) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String requestJson = objectMapper.writeValueAsString(websiteActivityDto);
            HttpPost httpPost = new HttpPost(workTrackProperties.getServerUserWebsiteActivityUrl());
            StringEntity entity = new StringEntity(requestJson);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                log.info("Response status code: {}", response.getCode());
                log.info("Response body: {}", responseBody);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadScreenshot(File imageFile, String userName) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create HTTP POST request
            HttpPost uploadFile = new HttpPost(workTrackProperties.getServerUserScreenshotUploadUrl());

            // Create MultipartEntityBuilder and add file and string data
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("image", new FileBody(imageFile, ContentType.DEFAULT_BINARY));
            builder.addPart("userName", new StringBody(userName, ContentType.TEXT_PLAIN));

            // Set the entity to the HttpPost request
            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                // Get the response entity
                HttpEntity responseEntity = response.getEntity();

                if (responseEntity != null) {
                    // Convert the response entity to a string and print
                    String responseString = EntityUtils.toString(responseEntity);
                    log.info("Response: {}", responseString);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
