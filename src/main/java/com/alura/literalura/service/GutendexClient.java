package com.leticia.literalura.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leticia.literalura.dto.GutendexBookDTO;
import com.leticia.literalura.dto.GutendexResponseDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class GutendexClient {

    private static final String BASE_URL = "https://gutendex.com/books/?search=";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Optional<GutendexBookDTO> searchFirstByTitle(String title) {
        try {
            String q = URLEncoder.encode(title, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + q))
                    .GET()
                    .build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() >= 200 && res.statusCode() < 300) {
                GutendexResponseDTO body = mapper.readValue(res.body(), GutendexResponseDTO.class);
                List<GutendexBookDTO> results = body.getResults();
                if (results != null && !results.isEmpty()) {
                    return Optional.of(results.get(0));
                }
            } else {
                System.out.println("Gutendex retornou status: " + res.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erro ao consultar Gutendex: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
