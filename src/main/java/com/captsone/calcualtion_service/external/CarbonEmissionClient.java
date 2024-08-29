package com.captsone.calcualtion_service.external;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import java.io.IOException;
import java.util.*;

public class CarbonEmissionClient {

    private static final String API_URL = "https://www.carboninterface.com/api/v1/estimates";
    private static final String API_KEY = "YIZLOVkgJ7LUf1rPTZCEw";

    public String from;
    public String to;

    public CarbonEmissionClient(String from, String to) {
        this.from = from;
        this.to = to;
    }


    public double[] sendFlightEmissionRequest() {
        // Set up RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() != HttpStatus.BAD_REQUEST) {
                    super.handleError(response);
                }
            }
        });

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");

        // Set up request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("type", "flight");
        requestBody.put("passengers", 1);
        requestBody.put("legs", new JSONObject[] {
            new JSONObject().put("departure_airport", from).put("destination_airport", to),
        });

        // Wrap request body in HttpEntity
        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // Send request
        try {
            ResponseEntity<HashMap> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, HashMap.class);

            Map mp = (Map) response.getBody().get("data");
            Map attribute = (Map) mp.get("attributes");
            double[] res = {Double.parseDouble(attribute.get("carbon_kg").toString()),Double.parseDouble(attribute.get("distance_value").toString())};
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new double[]{0.0,0.0};
    }

    
}
