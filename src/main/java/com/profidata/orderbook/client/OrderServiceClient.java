package com.profidata.orderbook.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profidata.orderbook.dto.response.CurrencyPairResponse;
import com.profidata.orderbook.dto.response.FXRateResponse;
import com.profidata.orderbook.dto.response.OrderResponse;
import com.profidata.orderbook.exception.OrderBookException;
import com.profidata.orderbook.exception.ServiceUnavailableException;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** HTTP client for communicating with the Order Service API. */
@Component
public class OrderServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceClient.class);

  private final CloseableHttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final String baseUrl;
  private final int retryAttempts;
  private final Duration retryDelay;

  public OrderServiceClient(
      CloseableHttpClient httpClient,
      ObjectMapper objectMapper,
      String baseUrl,
      int retryAttempts,
      Duration retryDelay) {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
    this.baseUrl = ensureValidBaseUrl(baseUrl);
    this.retryAttempts = retryAttempts;
    this.retryDelay = retryDelay;
  }

  public CompletableFuture<OrderResponse> createOrderAsync(OrderResponse order) {
    return CompletableFuture.supplyAsync(
        () -> {
          long startTime = System.currentTimeMillis();
          try {
            LOGGER.debug("Creating new order: {}", order);
            OrderResponse result = createOrderSync(order);
            long duration = System.currentTimeMillis() - startTime;
            LOGGER.info("Successfully created order with ID: {} in {}ms", result.id(), duration);
            return result;
          } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LOGGER.error("Failed to create order after {}ms", duration, e);
            throw new RuntimeException(e);
          }
        });
  }

  public OrderResponse createOrderSync(OrderResponse order) {
    return executeWithRetry(
        () -> {
          HttpPost post = new HttpPost(baseUrl + "/createOrder");

          try {
            String jsonBody = objectMapper.writeValueAsString(order);
            post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            LOGGER.debug("Sending POST request to /createOrder");

            return httpClient.execute(
                post,
                response -> {
                  int statusCode = response.getCode();
                  HttpEntity entity = response.getEntity();
                  String responseBody = entity != null ? EntityUtils.toString(entity) : "";

                  LOGGER.debug("Received response: status={}", statusCode);

                  if (statusCode == 200) {
                    return objectMapper.readValue(responseBody, OrderResponse.class);
                  } else {
                    throw new OrderBookException("Failed to create order. Status: " + statusCode);
                  }
                });

          } catch (IOException e) {
            throw new ServiceUnavailableException("Failed to create order", e);
          }
        });
  }

  public CompletableFuture<Boolean> cancelOrderAsync(String orderId) {
    return CompletableFuture.supplyAsync(() -> cancelOrderSync(orderId));
  }

  public Boolean cancelOrderSync(String orderId) {
    return executeWithRetry(
        () -> {
          HttpPost post = new HttpPost(baseUrl + "/cancelOrder");

          try {
            String jsonBody = objectMapper.writeValueAsString(orderId);
            post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            return httpClient.execute(
                post,
                response -> {
                  int statusCode = response.getCode();
                  HttpEntity entity = response.getEntity();
                  String responseBody = entity != null ? EntityUtils.toString(entity) : "";

                  if (statusCode == 200) {
                    return "true".equals(responseBody.trim());
                  } else {
                    throw new OrderBookException("Failed to cancel order. Status: " + statusCode);
                  }
                });

          } catch (IOException e) {
            throw new ServiceUnavailableException("Failed to cancel order", e);
          }
        });
  }

  public CompletableFuture<List<OrderResponse>> retrieveOrdersAsync() {
    return CompletableFuture.supplyAsync(this::retrieveOrdersSync);
  }

  public List<OrderResponse> retrieveOrdersSync() {
    return executeWithRetry(
        () -> {
          HttpGet get = new HttpGet(baseUrl + "/retrieveOrders");

          try {
            return httpClient.execute(
                get,
                response -> {
                  int statusCode = response.getCode();
                  HttpEntity entity = response.getEntity();
                  String responseBody = entity != null ? EntityUtils.toString(entity) : "";

                  if (statusCode == 200) {
                    return objectMapper.readValue(
                        responseBody, new TypeReference<List<OrderResponse>>() {});
                  } else {
                    throw new OrderBookException(
                        "Failed to retrieve orders. Status: " + statusCode);
                  }
                });

          } catch (IOException e) {
            throw new ServiceUnavailableException("Failed to retrieve orders", e);
          }
        });
  }

  public CompletableFuture<List<FXRateResponse>> getRateSnapshotAsync() {
    return CompletableFuture.supplyAsync(this::getRateSnapshotSync);
  }

  public List<FXRateResponse> getRateSnapshotSync() {
    return executeWithRetry(
        () -> {
          HttpGet get = new HttpGet(baseUrl + "/rateSnapshot");

          try {
            return httpClient.execute(
                get,
                response -> {
                  int statusCode = response.getCode();
                  HttpEntity entity = response.getEntity();
                  String responseBody = entity != null ? EntityUtils.toString(entity) : "";

                  if (statusCode == 200) {
                    return objectMapper.readValue(
                        responseBody, new TypeReference<List<FXRateResponse>>() {});
                  } else {
                    throw new OrderBookException("Failed to retrieve rates. Status: " + statusCode);
                  }
                });

          } catch (IOException e) {
            throw new ServiceUnavailableException("Failed to retrieve rates", e);
          }
        });
  }

  public CompletableFuture<List<CurrencyPairResponse>> getSupportedCurrencyPairsAsync() {
    return CompletableFuture.supplyAsync(this::getSupportedCurrencyPairsSync);
  }

  public List<CurrencyPairResponse> getSupportedCurrencyPairsSync() {
    return executeWithRetry(
        () -> {
          HttpGet get = new HttpGet(baseUrl + "/supportedCurrencyPairs");

          try {
            return httpClient.execute(
                get,
                response -> {
                  int statusCode = response.getCode();
                  HttpEntity entity = response.getEntity();
                  String responseBody = entity != null ? EntityUtils.toString(entity) : "";

                  if (statusCode == 200) {
                    return objectMapper.readValue(
                        responseBody, new TypeReference<List<CurrencyPairResponse>>() {});
                  } else {
                    throw new OrderBookException(
                        "Failed to retrieve supported pairs. Status: " + statusCode);
                  }
                });

          } catch (IOException e) {
            throw new ServiceUnavailableException("Failed to retrieve supported currency pairs", e);
          }
        });
  }

  public CompletableFuture<Boolean> healthCheckAsync() {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            getSupportedCurrencyPairsSync();
            return true;
          } catch (Exception e) {
            LOGGER.warn("Health check failed", e);
            return false;
          }
        });
  }

  private <T> T executeWithRetry(SupplierWithException<T> operation) {
    Exception lastException = null;

    for (int attempt = 1; attempt <= retryAttempts; attempt++) {
      try {
        return operation.get();
      } catch (Exception e) {
        lastException = e;

        if (attempt < retryAttempts) {
          LOGGER.warn("Attempt {} failed, retrying in {}ms", attempt, retryDelay.toMillis());

          try {
            Thread.sleep(retryDelay.toMillis() * attempt);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ServiceUnavailableException("Operation interrupted", ie);
          }
        } else {
          LOGGER.error("All {} attempts failed", retryAttempts);
        }
      }
    }

    if (lastException instanceof RuntimeException) {
      throw (RuntimeException) lastException;
    } else {
      throw new ServiceUnavailableException(
          "Operation failed after " + retryAttempts + " attempts", lastException);
    }
  }

  private String ensureValidBaseUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new IllegalArgumentException("Base URL cannot be null or empty");
    }

    String normalizedUrl = url.trim();
    if (normalizedUrl.endsWith("/")) {
      normalizedUrl = normalizedUrl.substring(0, normalizedUrl.length() - 1);
    }

    return normalizedUrl;
  }

  @FunctionalInterface
  private interface SupplierWithException<T> {
    T get() throws Exception;
  }
}
