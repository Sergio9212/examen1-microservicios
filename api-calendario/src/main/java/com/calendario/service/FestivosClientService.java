package com.calendario.service;

import com.calendario.dto.FestivoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FestivosClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.festivos.url:http://localhost:3000}")
    private String apiFestivosUrl;

    public List<FestivoDto> listarFestivos(int anio) {
        String url = apiFestivosUrl + "/api/festivos/listar/" + anio;
        ResponseEntity<List<FestivoDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FestivoDto>>() {}
        );
        return response.getBody();
    }
}
