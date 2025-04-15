//package com.haust.easyremotemcp.config;
//
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicReference;
//
///**
// * @author: liyongbin
// * @date: 2025/4/14 22:34
// * @description:
// */
//@Component
//public class LoggingWebFilter implements WebFilter {
//
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        final AtomicReference<ServerHttpRequest>[] request = new AtomicReference[]{new AtomicReference<>(exchange.getRequest())};
//        ServerHttpResponse response = exchange.getResponse();
//
//        // Log request details
//        logRequestDetails(request[0].get());
//
//        // Capture the original body and replace it with a Flux that logs the content
//        return DataBufferUtils.join(request[0].get().getBody())
//                .flatMap(dataBuffer -> {
//                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                    dataBuffer.read(bytes);
//                    String requestBody = new String(bytes, StandardCharsets.UTF_8);
//                    DataBufferUtils.release(dataBuffer);
//                    System.out.println("Request Body: " + requestBody);
//
//                    // Create a flux from the captured bytes and set it back to the request body
//                    Flux<DataBuffer> cachedBody = Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
//                    request[0] = request[0].get().mutate().body(cachedBody).build();
//
//                    return chain.filter(exchange.mutate().request(request[0].get()).build());
//                })
//                .doOnSuccessOrError((unused, throwable) -> {
//                    if (throwable == null) {
//                        logResponseDetails(response);
//                    }
//                });
//    }
//
//    private void logRequestDetails(ServerHttpRequest request) {
//        System.out.println("Request URL: " + request.getURI());
//        System.out.println("Request Method: " + request.getMethodValue());
//
//        List<String> headers = request.getHeaders().keySet().stream()
//                .map(key -> key + ": " + request.getHeaders().getFirst(key))
//                .collect(java.util.stream.Collectors.toList());
//        System.out.println("Request Headers: " + String.join(", ", headers));
//    }
//
//    private void logResponseDetails(ServerHttpResponse response) {
//        List<String> headers = response.getHeaders().keySet().stream()
//                .map(key -> key + ": " + response.getHeaders().getFirst(key))
//                .collect(java.util.stream.Collectors.toList());
//        System.out.println("Response Status Code: " + response.getStatusCode());
//        System.out.println("Response Headers: " + String.join(", ", headers));
//
//        // Note: Logging response body in this way may not work as expected due to the nature of reactive streams.
//        // For logging response body, you might need to use a more complex approach involving ResponseDecorator or similar techniques.
//    }
//}
