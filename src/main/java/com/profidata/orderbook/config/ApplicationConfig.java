package com.profidata.orderbook.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.profidata.orderbook.client.OrderServiceClient;
import com.profidata.orderbook.domain.FXRate;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/** Application configuration with production-ready defaults. */
@Configuration
@ConfigurationProperties(prefix = "fx-orderbook")
@Validated
public class ApplicationConfig {

  @NotNull private HttpClientConfig httpClient = new HttpClientConfig();

  @NotNull private CacheConfig cache = new CacheConfig();

  @NotNull private OrderServiceConfig orderService = new OrderServiceConfig();

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
  }

  @Bean
  public CloseableHttpClient httpClient() {
    var connectionConfig =
        ConnectionConfig.custom()
            .setSocketTimeout(Timeout.of(httpClient.getSocketTimeout()))
            .setConnectTimeout(Timeout.of(httpClient.getConnectTimeout()))
            .build();

    var connectionManager =
        PoolingHttpClientConnectionManagerBuilder.create()
            .setDefaultConnectionConfig(connectionConfig)
            .setMaxConnTotal(httpClient.getMaxConnections())
            .setMaxConnPerRoute(httpClient.getMaxConnectionsPerRoute())
            .build();

    var requestConfig =
        RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.of(httpClient.getConnectionRequestTimeout()))
            .setResponseTimeout(Timeout.of(httpClient.getResponseTimeout()))
            .build();

    return HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setDefaultRequestConfig(requestConfig)
        .build();
  }

  @Bean
  public Cache<String, List<FXRate>> fxRateCache() {
    return Caffeine.newBuilder()
        .maximumSize(cache.getMaxSize())
        .expireAfterWrite(cache.getExpireAfterWrite().toSeconds(), TimeUnit.SECONDS)
        .recordStats()
        .build();
  }

  @Bean
  public OrderServiceClient orderServiceClient(
      CloseableHttpClient httpClient, ObjectMapper objectMapper) {
    return new OrderServiceClient(
        httpClient,
        objectMapper,
        orderService.getBaseUrl(),
        orderService.getRetryAttempts(),
        orderService.getRetryDelay());
  }

  public HttpClientConfig getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClientConfig httpClient) {
    this.httpClient = httpClient;
  }

  public CacheConfig getCache() {
    return cache;
  }

  public void setCache(CacheConfig cache) {
    this.cache = cache;
  }

  public OrderServiceConfig getOrderService() {
    return orderService;
  }

  public void setOrderService(OrderServiceConfig orderService) {
    this.orderService = orderService;
  }

  public static class HttpClientConfig {
    @DurationMin(seconds = 1)
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration socketTimeout = Duration.ofSeconds(30);

    @DurationMin(seconds = 1)
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration connectTimeout = Duration.ofSeconds(10);

    @DurationMin(seconds = 1)
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration connectionRequestTimeout = Duration.ofSeconds(30);

    @DurationMin(seconds = 1)
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration responseTimeout = Duration.ofSeconds(60);

    @Min(1)
    @Max(1000)
    private int maxConnections = 50;

    @Min(1)
    @Max(100)
    private int maxConnectionsPerRoute = 10;

    public Duration getSocketTimeout() {
      return socketTimeout;
    }

    public void setSocketTimeout(Duration socketTimeout) {
      this.socketTimeout = socketTimeout;
    }

    public Duration getConnectTimeout() {
      return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
      this.connectTimeout = connectTimeout;
    }

    public Duration getConnectionRequestTimeout() {
      return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Duration connectionRequestTimeout) {
      this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public Duration getResponseTimeout() {
      return responseTimeout;
    }

    public void setResponseTimeout(Duration responseTimeout) {
      this.responseTimeout = responseTimeout;
    }

    public int getMaxConnections() {
      return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
      this.maxConnections = maxConnections;
    }

    public int getMaxConnectionsPerRoute() {
      return maxConnectionsPerRoute;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
      this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }
  }

  public static class CacheConfig {
    @Min(10)
    @Max(10000)
    private long maxSize = 1000;

    @NotNull private Duration expireAfterWrite = Duration.ofMinutes(5);

    public long getMaxSize() {
      return maxSize;
    }

    public void setMaxSize(long maxSize) {
      this.maxSize = maxSize;
    }

    public Duration getExpireAfterWrite() {
      return expireAfterWrite;
    }

    public void setExpireAfterWrite(Duration expireAfterWrite) {
      this.expireAfterWrite = expireAfterWrite;
    }
  }

  public static class OrderServiceConfig {
    @NotBlank private String baseUrl = "http://localhost:8888";

    @Min(1)
    @Max(10)
    private int retryAttempts = 3;

    @NotNull private Duration retryDelay = Duration.ofMillis(500);

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    public int getRetryAttempts() {
      return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
      this.retryAttempts = retryAttempts;
    }

    public Duration getRetryDelay() {
      return retryDelay;
    }

    public void setRetryDelay(Duration retryDelay) {
      this.retryDelay = retryDelay;
    }
  }

  @Bean
  public MeterRegistry meterRegistry() {
    return new SimpleMeterRegistry();
  }
}
