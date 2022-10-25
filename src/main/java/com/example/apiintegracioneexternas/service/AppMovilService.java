package com.example.apiintegracioneexternas.service;

import com.example.apiintegracioneexternas.dto.ResponseDto;
import com.example.apiintegracioneexternas.utils.constantes.ConstDiccionarioMensaje;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppMovilService {
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

        ResponseEntity<Map> resultMap = restTemplate.postForEntity(baseUrl+"/openapi-stage/v1/auth", request, Map.class);
        if (resultMap != null && resultMap.getStatusCode().value() == 200) {

            Map<String, Object> dataToken = oMapper.convertValue(resultMap.getBody().get("data"), Map.class);
            return dataToken.get("access_token").toString();

        }else{
            return null;
        }

    }
    public List<Map<String, Object>> consultaPoliza (String pCi, String pExtension,String pFechaNac,String pComplemento){
        ResponseDto res = new ResponseDto();
        try{

            String beneficiario1= "Alicia Arancibia";
            String beneficiario2= "Antonio Arancibia";
            List<String> lstBeneficiarios = new ArrayList<>();
            lstBeneficiarios.add(beneficiario1);
            lstBeneficiarios.add(beneficiario2);


            Map<String, Object> data1 = new HashMap<>();
            data1.put("numero_poliza", "0000567");
            data1.put("producto", "Desgravamen Hipotecario");
            data1.put("nombre_asegurado", "Alvaro Arancibia");
            data1.put("nombre_tomador", "Abel Rolando Arancibia");
            data1.put("beneficiarios", lstBeneficiarios); // array
            data1.put("numero_operacion", "O-0000001395");
            data1.put("nombre_poliza", "DES-NL-00732");
            data1.put("fecha_inicio", "01/09/2022");
            data1.put("fecha_fin", "01/09/2023");
            data1.put("tipo_producto", "Vida");
            data1.put("frecuenia", "Mensual");
            data1.put("monto_prima", "410");
            data1.put("precio", "410");

            Map<String, Object> data2 = new HashMap<>();
            data2.put("numero_poliza", "0000567");
            data2.put("producto", "Desgravamen Hipotecario");
            data2.put("nombre_asegurado", "Alvaro Arancibia");
            data2.put("nombre_tomador", "Abel Rolando Arancibia");
            data2.put("beneficiarios", lstBeneficiarios); // array
            data2.put("numero_operacion", "O-0000001395");
            data2.put("nombre_poliza", "DES-NL-00732");
            data2.put("fecha_inicio", "01/09/2022");
            data2.put("fecha_fin", "01/09/2023");
            data2.put("tipo_producto", "Vida");
            data2.put("frecuenia", "Mensual");
            data2.put("monto_prima", "410");
            data2.put("precio", "410");

            Map<String, Object> data3 = new HashMap<>();
            data3.put("numero_poliza", "0000567");
            data3.put("producto", "Desgravamen Hipotecario");
            data3.put("nombre_asegurado", "Alvaro Arancibia");
            data3.put("nombre_tomador", "Abel Rolando Arancibia");
            data3.put("beneficiarios", lstBeneficiarios); // array
            data3.put("numero_operacion", "O-0000001395");
            data3.put("nombre_poliza", "DES-NL-00732");
            data3.put("fecha_inicio", "01/09/2022");
            data3.put("fecha_fin", "01/09/2023");
            data3.put("tipo_producto", "Vida");
            data3.put("frecuenia", "Mensual");
            data3.put("monto_prima", "410");
            data3.put("precio", "410");

            List<Map<String, Object>> lstPolizas = new ArrayList<>();
            lstPolizas.add(data1);
            lstPolizas.add(data2);
            lstPolizas.add(data3);

            return lstPolizas;

        }catch (Exception ex){

            return  new ArrayList<>();

        }
    }
}
