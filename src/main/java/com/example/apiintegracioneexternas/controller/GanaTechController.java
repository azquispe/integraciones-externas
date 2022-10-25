package com.example.apiintegracioneexternas.controller;

import com.example.apiintegracioneexternas.service.GanaTechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/ganatech")

public class GanaTechController {
    @Autowired
    private GanaTechService ganaTechService;

    @PostMapping("/v1/consulta-segip")
    public ResponseEntity<?> consultaSegip(@RequestBody Map objRequest) {


        String token = ganaTechService.obtenerToken();
        String ci=objRequest.get("ci")!=null?objRequest.get("ci").toString():"";
        String documentCity=objRequest.get("documentCity")!=null?objRequest.get("documentCity").toString():"";
        String birthdate=objRequest.get("birthdate")!=null?objRequest.get("birthdate").toString():"";

        Map<String, Object> res = ganaTechService.ConsultaSegip(token, ci,documentCity,birthdate);

        return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
    }


}
