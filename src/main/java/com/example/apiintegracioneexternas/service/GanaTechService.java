package com.example.apiintegracioneexternas.service;


import com.example.apiintegracioneexternas.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GanaTechService {
    private final RestTemplate restTemplate;
    final String baseUrl = "https://api.bg.com.bo";
    ObjectMapper oMapper = new ObjectMapper();
    public String obtenerToken (){

        Map<String, Object> data = new HashMap<>();
        data.put("client_key", "$2a$10$Ft6UCTo6ovMcGD/d5uJF.eq3uTqeiU0V.VRtHpWRjceNKHw9o01CO");
        data.put("access_code", "Ganaseguros");
        Map<String, Object> risk = new HashMap<>();
        Map<String, Object> request = new HashMap<>();

        request.put("data",data);
        request.put("risk",risk);

        ResponseEntity<Map>  resultMap = restTemplate.postForEntity(baseUrl+"/openapi-stage/v1/auth", request, Map.class);
        if (resultMap != null && resultMap.getStatusCode().value() == 200) {

            Map<String, Object> dataToken = oMapper.convertValue(resultMap.getBody().get("data"), Map.class);
            return dataToken.get("access_token").toString();

        }else{
            return null;
        }

    }
    public   Map<String, Object> ConsultaSegip (String token,String ci, String documentCity, String birthdate){
        Map<String, Object> res = new HashMap<>();
        try{
            Map<String, Object> data = new HashMap<>();
            data.put("ci", ci);
            data.put("documentCity", documentCity);
            data.put("birthdate", birthdate);

            Map<String, Object> request = new HashMap<>();

            request.put("data",data);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer "+token);
            headers.set("x-fapi-financial-id", "63");


            HttpEntity<Map> body = new HttpEntity<>(request, headers);


            ResponseEntity<Map>  resultMap = restTemplate.postForEntity(baseUrl+"/openapi-stage/v1/accounts/validations/segip", body, Map.class);
            return resultMap.getBody();

        }catch (Exception ex){
            res.put("statusCode","404");
            res.put("message",ex.toString());
            return  res;

        }
    }
}
