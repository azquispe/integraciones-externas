package com.example.apiintegracioneexternas.controller;


import com.example.apiintegracioneexternas.service.AppMovilService;
import com.example.apiintegracioneexternas.service.GanaTechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/app-movil")

public class AppMovilController {

    @Autowired
    private AppMovilService appMovilService;

    @PostMapping("/v1/consulta-poliza")
    public ResponseEntity<?> consultaSegip(@RequestBody Map objRequest) {


        String vCi=objRequest.get("ci")!=null?objRequest.get("ci").toString():"";
        String vExtension=objRequest.get("extension")!=null?objRequest.get("extension").toString():"";
        String vFechaNac=objRequest.get("fecha_nac")!=null?objRequest.get("fecha_nac").toString():"";
        String vComplemento=objRequest.get("complemento")!=null?objRequest.get("complemento").toString():"";

        List<Map<String, Object>> lstPolizas = appMovilService.consultaPoliza(vCi, vExtension,vFechaNac,vComplemento);

        return new ResponseEntity<List<Map<String, Object>>>(lstPolizas, HttpStatus.OK);
    }


}
