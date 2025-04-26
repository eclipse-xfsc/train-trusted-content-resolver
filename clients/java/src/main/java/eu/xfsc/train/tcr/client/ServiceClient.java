package eu.xfsc.train.tcr.client;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

public abstract class ServiceClient {
    
    protected final String baseUrl; // do we need it?
    protected final ObjectMapper mapper;
    protected final WebClient client;

    public ServiceClient(String baseUrl, String jwt) {
        this.baseUrl = baseUrl;
        mapper = new ObjectMapper()
            .findAndRegisterModules()   // .registerModule(new ParanamerModule()) .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        WebClient.Builder builder = WebClient.builder()
            .baseUrl(baseUrl)
            .codecs(configurer -> {
                configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON));
                configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));
            })
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (jwt != null) {
            builder = builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        }
        this.client = builder.build();
//      this.template.setErrorHandler(new ErrorHandler(mapper));
    }

    public ServiceClient(String baseUrl, WebClient client) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.mapper = new ObjectMapper()
                .findAndRegisterModules()   // .registerModule(new ParanamerModule()) .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    
    public String getUrl() {
    	return this.baseUrl;
    }

    protected <T> T doGet(String path, Map<String, Object> params, Class<T> reType) {
        return client
            .get()
            .uri(path, builder -> builder.build(params))
            .retrieve()
            .bodyToMono(reType) 
            .block();
    }

    // TODO: add asynch methods also

    protected <T> T doPost(String path, Object body, Map<String, Object> params, Class<T> reType) {
        return client
            .post()
            .uri(path, builder -> builder.build(params))
            .bodyValue(body)
            .retrieve()
            .bodyToMono(reType) 
            .block();
    }

    protected <T> T doPost(String path, Map<String, Object> params, Class<T> reType) {
        return client
            .post()
            .uri(path, builder -> builder.build(params))
            .retrieve()
            .bodyToMono(reType)
            .block();
    }
    
    protected <T> Mono<T> doPostAsync(String path, Object body, Map<String, Object> params, Class<T> reType) {
        return client
            .post()
            .uri(path, builder -> builder.build(params))
            .bodyValue(body)
            .retrieve()
            .bodyToMono(reType); 
    }
    
    protected <T> Mono<T> doPostAsync(String path, Map<String, Object> params, Class<T> reType) {
        return client
            .post()
            .uri(path, builder -> builder.build(params))
            .retrieve()
            .bodyToMono(reType);
    }
    
    protected <T> T doPut(String path, Object body, Map<String, Object> params, Class<T> reType) {
        return client
            .put()
            .uri(path, builder -> builder.build(params))
            .bodyValue(body)
            .retrieve()
            .bodyToMono(reType) 
            .block();
    }

    protected <T> T doDelete(String path, Map<String, Object> params, Class<T> reType) {
        return client
            .delete()
            .uri(path, builder -> builder.build(params))
            .retrieve()
            .bodyToMono(reType) 
            .block();
    }
    
}
