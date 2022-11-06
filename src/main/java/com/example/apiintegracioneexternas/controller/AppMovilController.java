package com.example.apiintegracioneexternas.controller;


import com.example.apiintegracioneexternas.dto.PolizasDto;
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

        String vNroDocumento=objRequest.get("nroDocumento")!=null?objRequest.get("nroDocumento").toString():"";
        String vExtension=objRequest.get("extension")!=null?objRequest.get("extension").toString():"";
        String vFechaNac=objRequest.get("fechaNacimiento")!=null?objRequest.get("fechaNacimiento").toString():"";
        String vComplemento=objRequest.get("complemento")!=null?objRequest.get("complemento").toString():"";

        //List<Map<String, Object>> lstPolizas = appMovilService.consultaPoliza(vCi, vExtension,vFechaNac,vComplemento);
        List<PolizasDto> lstPolizas = appMovilService.consultaPoliza(vNroDocumento, vExtension,vFechaNac,vComplemento);

        ///return new ResponseEntity<List<Map<String, Object>>>(lstPolizas, HttpStatus.OK);
        return new ResponseEntity<List<PolizasDto>>(lstPolizas, HttpStatus.OK);
    }


}
