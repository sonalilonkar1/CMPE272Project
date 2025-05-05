package com.reliefcircle.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class VerifyCharityLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String API_BASE_URL = "http://localhost:8080/api/charities"; // Replace with your API URL

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(new HashMap<>(Map.of("Content-Type", "application/json")));

        try {
            // Extract charity ID from query parameters or path
            String charityId = input.getPathParameters() != null ? input.getPathParameters().get("id") : null;
            if (charityId == null || charityId.isEmpty()) {
                response.setStatusCode(400);
                response.setBody("{\"error\": \"Charity ID is required\"}");
                return response;
            }

            HttpClient client = HttpClient.newHttpClient();
            String apiUrl = API_BASE_URL + "/" + charityId + "/verify";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();

            response.setStatusCode(statusCode);
            if (statusCode == 200) {
                response.setBody("{\"message\": \"Success: " + responseBody + "\"}");
            } else if (statusCode == 404) {
                response.setBody("{\"error\": \"" + responseBody + "\"}");
            } else {
                response.setBody("{\"error\": \"Unexpected response code " + statusCode + ": " + responseBody + "\"}");
            }
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            response.setStatusCode(500);
            response.setBody("{\"error\": \"Failed to call API: " + e.getMessage() + "\"}");
        }

        return response;
    }
}