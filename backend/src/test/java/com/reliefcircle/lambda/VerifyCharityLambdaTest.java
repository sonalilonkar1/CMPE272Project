package com.reliefcircle.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.MockedStatic;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VerifyCharityLambdaTest {

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;

    @Spy
    private VerifyCharityLambda lambda;

    private APIGatewayProxyRequestEvent request;
    private Map<String, String> pathParameters;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new APIGatewayProxyRequestEvent();
        pathParameters = new HashMap<>();
        request.setPathParameters(pathParameters);
        
        // Set up the logger mock
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void testHandleRequest_Success() throws Exception {
        // Arrange
        pathParameters.put("id", "123");
        request.setPathParameters(pathParameters);

        // Mock the static HttpClient.newHttpClient() method
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            HttpClient mockClient = mock(HttpClient.class);
            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn("Charity verified successfully");
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
            
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockClient);

            // Act
            APIGatewayProxyResponseEvent response = lambda.handleRequest(request, context);

            // Assert
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getBody().contains("Success"));
        }
    }

    @Test
    void testHandleRequest_MissingCharityId() {
        // Act
        APIGatewayProxyResponseEvent response = lambda.handleRequest(request, context);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Charity ID is required"));
    }

    @Test
    void testHandleRequest_ApiError() throws Exception {
        // Arrange
        pathParameters.put("id", "invalid");
        request.setPathParameters(pathParameters);

        // Mock the static HttpClient.newHttpClient() method
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            HttpClient mockClient = mock(HttpClient.class);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("API Error"));
            
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockClient);

            // Act
            APIGatewayProxyResponseEvent response = lambda.handleRequest(request, context);

            // Assert
            assertEquals(500, response.getStatusCode());
            assertTrue(response.getBody().contains("Failed to call API"));
            verify(logger).log(contains("Error: API Error"));
        }
    }
} 