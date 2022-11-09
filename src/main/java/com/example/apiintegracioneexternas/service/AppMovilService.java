package com.example.apiintegracioneexternas.service;

import com.example.apiintegracioneexternas.dto.PolizasDto;
import com.example.apiintegracioneexternas.dto.ResponseDto;
import com.example.apiintegracioneexternas.dto.SolicitudPolizaDto;
import com.example.apiintegracioneexternas.utils.constantes.ConstDiccionarioMensaje;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppMovilService {
    private final RestTemplate restTemplate;
    final String baseUrl = "https://sociedadganadero--devtemp8.sandbox.my.salesforce.com";
    ObjectMapper oMapper = new ObjectMapper();

    @Autowired
    private EmailService emailService;


    public String obtenerToken() throws URISyntaxException {

        /*Map<String, Object> data = new HashMap<>();
        data.put("client_key", "$2a$10$Ft6UCTo6ovMcGD/d5uJF.eq3uTqeiU0V.VRtHpWRjceNKHw9o01CO");
        data.put("access_code", "Ganaseguros");
        Map<String, Object> risk = new HashMap<>();
        Map<String, Object> request = new HashMap<>();

        request.put("data",data);
        request.put("risk",risk);*/

        ResponseEntity<Map> resultMap = restTemplate.postForEntity(new URI(baseUrl + "/services/oauth2/token?grant_type=password&" +
                "client_id=3MVG99VEEJ_Bj3.7I7xGG.Bq_J8F3a4MeRx2mQ_YyzMsOkrBBLD_brf0BO42IQUn2rtDSdv5ryAoCQLuDZcwP&" +
                "client_secret=77ADEFF8025EEBE56BC9DBA7AC5A45685293C40B2A553F1A4B35DCC0CE454E68" +
                "&username=ararancibia@bg.com.bo.devtemp8&password=fa3BO2aa77cPSdT836OF12yYKHE2fj7Y"), new HashMap<>(), Map.class);
        if (resultMap != null && resultMap.getStatusCode().value() == 200) {

            //Map<String, Object> dataToken = oMapper.convertValue(resultMap.getBody().get("access_token"), Map.class);
            return resultMap.getBody().get("access_token").toString();

        } else {
            return null;
        }

    }

    public List<PolizasDto> consultaPoliza(String pNroDocumento, String pExtension, String pFechaNac, String pComplemento) {

        Map<String, Object> mapDatosPersona = null;
        Map<String, Object> mapDatosPoliza = null;
        Map<String, Object> mapDatosPolizaDetalle = null;

        List<PolizasDto> lstPolizas = new ArrayList<>();

        try {
            String token = this.obtenerToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            // BUSCA DATOS PERSONA
            //  Consulta Datos Persona por Documento de Identidad
            // https://ap1.salesforce.com/services/apexrest/vlocity_ins/v1/integrationprocedure/bg_ti_g_getPersonInformation

            try {
                String urlDatosPersona = baseUrl + "/services/apexrest/vlocity_ins/v1/integrationprocedure/" + "bg_ti_g_getPersonInformation?searchCriteria=1&identificationType=CI&identificationNumber=" + pNroDocumento + "&identificationPlugin=" + pComplemento;
                HttpEntity request = new HttpEntity(headers);
                ResponseEntity<String> resultMapDatosPersona = restTemplate.exchange(
                        urlDatosPersona,
                        HttpMethod.GET,
                        request,
                        String.class,
                        1
                );
                if (resultMapDatosPersona != null && resultMapDatosPersona.getStatusCode().value() == 200) {
                    mapDatosPersona = new ObjectMapper().readValue(resultMapDatosPersona.getBody(), Map.class);
                    if (mapDatosPersona.get("codigoMensaje").equals("CODSF1000")) {

                        Map<String, Object> objCustomer = oMapper.convertValue(mapDatosPersona.get("Customers"), Map.class);
                        List<Map<String, Object>> lstPersonas = oMapper.convertValue(objCustomer.get("objetos"), ArrayList.class);

                        //BUSCA PÓLIZAS
                        //  Consulta Póliza(s) de una Persona por “AccountId”
                        //  https://ap1.salesforce.com/services/apexrest/vlocity_ins/v1/integrationprocedure/bg_ti_g_getProductInformation
                        for (Map<String, Object> vAccountIdMap : lstPersonas) {

                            String urlPolizas = baseUrl + "/services/apexrest/vlocity_ins/v1/integrationprocedure/bg_ti_g_getProductInformation";
                            Map<String, Object> requestGetPoliza = new HashMap<>();
                            requestGetPoliza.put("AccountId",vAccountIdMap.get("AccountId").toString());
                            HttpEntity<Map> bodyGetPoliza = new HttpEntity<>(requestGetPoliza, headers);
                            ResponseEntity<String> resultMapDatosPoliza = restTemplate.postForEntity(urlPolizas, bodyGetPoliza, String.class);
                            if (resultMapDatosPoliza != null && resultMapDatosPoliza.getStatusCode().value() == 200) {
                                mapDatosPoliza = new ObjectMapper().readValue(resultMapDatosPoliza.getBody(), Map.class);
                                if (mapDatosPoliza.get("codigoMensaje").equals("CODSF1002")) {


                                    PolizasDto objPoliza = null;
                                    Map<String, Object> objMapProductos = oMapper.convertValue(mapDatosPoliza.get("Products"), Map.class);
                                    List<Map<String, Object>> lstMapPolizas = oMapper.convertValue(objMapProductos.get("objetos"), ArrayList.class);
                                    for (Map<String, Object> objMapPoliza : lstMapPolizas) {
                                        objPoliza = new PolizasDto();
                                        objPoliza.setPolizaId(objMapPoliza.get("PolicyId") != null ? objMapPoliza.get("PolicyId").toString() : "-");
                                        objPoliza.setNombreProducto(objMapPoliza.get("productName") != null ? objMapPoliza.get("productName").toString() : " - ");
                                        objPoliza.setNumeroProducto("no-identificado");
                                        objPoliza.setTipoProducto("no-identificado");

                                        //objPoliza.setNombreAsegurado(objMapPoliza.get("NameInsured") != null ? objMapPoliza.get("NameInsured").toString() : " - ");

                                        objPoliza.setNombrePoliza(objMapPoliza.get("PolicyName") != null ? objMapPoliza.get("PolicyName").toString() : " - ");
                                        objPoliza.setFechaInicio(objMapPoliza.get("EffectiveDate") != null ? objMapPoliza.get("EffectiveDate").toString() : " - ");
                                        objPoliza.setFechaFin(objMapPoliza.get("ExpirationDate") != null ? objMapPoliza.get("ExpirationDate").toString() : " - ");


                                        objPoliza.setFrecuencia(objMapPoliza.get("PremiumFrecuency") != null ? objMapPoliza.get("PremiumFrecuency").toString() : " - ");
                                        objPoliza.setPrecio(objMapPoliza.get("Price") != null ? objMapPoliza.get("Price").toString() : " - ");


                                        // Consulta Detalle de Póliza y Pagos Pendientes por "PolicyId"
                                        // https://ap1.salesforce.com/services/apexrest/vlocity_ins/v1/integrationprocedure/bg_ti_g_getPolicyInformation
                                        String urlDetallePolizas = baseUrl + "/services/apexrest/vlocity_ins/v1/integrationprocedure/bg_ti_g_getPolicyInformation?PolicyId=" + objPoliza.getPolizaId() + "&Status=Pending";
                                        HttpEntity requestDetPoliza = new HttpEntity(headers);
                                        ResponseEntity<String> resultMapDatosPolizaDetalle = restTemplate.exchange(
                                                urlDetallePolizas,
                                                HttpMethod.GET,
                                                requestDetPoliza,
                                                String.class,
                                                1
                                        );
                                        if (resultMapDatosPolizaDetalle != null && resultMapDatosPolizaDetalle.getStatusCode().value() == 200) {
                                            mapDatosPolizaDetalle = new ObjectMapper().readValue(resultMapDatosPolizaDetalle.getBody(), Map.class);
                                            if (mapDatosPolizaDetalle.get("codigoMensaje").equals("CODSF1002")) {

                                                List<Map<String, Object>> lstMapPolizaDetalle = oMapper.convertValue(mapDatosPolizaDetalle.get("Poliza"), ArrayList.class);
                                                Map<String, Object> objMapPolizaDetalle = oMapper.convertValue(lstMapPolizaDetalle.get(0), Map.class);

                                                objPoliza.setNombreTomador(objMapPolizaDetalle.get("NombreTomador") != null ? objMapPolizaDetalle.get("NombreTomador").toString() : "-");
                                                objPoliza.setNombreAsegurado(objMapPolizaDetalle.get("NombreAsegurado") != null ? objMapPolizaDetalle.get("NombreAsegurado").toString() : "-"  );
                                                List<String> lstBeneficiarios = new ArrayList();
                                                if (objMapPolizaDetalle.get("Beneficiarios") != null) {
                                                    List<Map<String, Object>> lstMapBeneficiarios = oMapper.convertValue(objMapPolizaDetalle.get("Beneficiarios"), ArrayList.class);
                                                    for (Map<String, Object> objMapBeneficiarios : lstMapBeneficiarios) {
                                                        lstBeneficiarios.add(
                                                                (objMapBeneficiarios.get("Nombre") != null ? objMapBeneficiarios.get("Nombre").toString() : " - ") +
                                                                        (objMapBeneficiarios.get("Porcentaje") != null ? "(" + objMapBeneficiarios.get("Porcentaje").toString() + "%)" : " - ")
                                                        );
                                                    }
                                                }
                                                objPoliza.setLstBeneficiarios(lstBeneficiarios);
                                                objPoliza.setNumeroOperacion(objMapPolizaDetalle.get("NroOperacion") != null ? objMapPolizaDetalle.get("NroOperacion").toString() : "-");
                                                objPoliza.setEstado(objMapPolizaDetalle.get("Status") != null ? objMapPolizaDetalle.get("Status").toString() : "-");
                                                objPoliza.setMontoPrima(objMapPolizaDetalle.get("PremiumAmount") != null ? objMapPolizaDetalle.get("PremiumAmount").toString() : "-");

                                            }
                                        }
                                        lstPolizas.add(objPoliza);
                                    }
                                }
                            }
                        }

                    }
                }
            } catch (Exception ex) {
                System.out.println("error: " + ex.toString());
            }


            /*String beneficiario1 = "Alicia Arancibia";
            String beneficiario2 = "Antonio Arancibia";
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
            lstPolizas.add(data3);*/

            return lstPolizas;

        } catch (Exception ex) {

            return new ArrayList<>();

        }
    }
    public ResponseDto enviarSolicitudSeguro(Map objRequest){
        ResponseDto res = new ResponseDto();
        try{
            // Crear el asunto
            String vAsunto = "SOLICITUD DE SEGURO";

            // Crear el cuerpo del correo
            StringBuilder vBody = new StringBuilder();

            vBody.append("Mediante la Presente se informa de una solicitud de seguro <br>");
            vBody.append("con el siguiente detalle: <br><br>");
            vBody.append("Nombre : "+objRequest.get("nombreCompleto").toString()+" <br>");
            vBody.append("Correo :"+objRequest.get("correoElectronico").toString()+" <br />");
            vBody.append("Ciudad: "+ objRequest.get("ciudad").toString()+" <br />");
            vBody.append("CI: "+objRequest.get("ci").toString()+" <br />" );
            vBody.append("Celular: "+objRequest.get("celular").toString()+" <br />");
            vBody.append("Tipo de seguro: "+objRequest.get("tipoSeguro").toString()+" <br />");
            vBody.append("Mensaje: "+objRequest.get("mensaje").toString()+" <br />");

            vBody.append("<br />");
            vBody.append("GANASEGUROS <br>");
            vBody.append("Correo generado desde la Aplicación Móvil. <br>");

            // Determinar el destinatario copia
            String vDestino = "azquispe@bg.com.bo";
            //String vDestino = "alvaro20092004@hotmail.com";


            emailService.enviarCorreoHtml(vDestino,vAsunto,vBody.toString());

            res.setCodigo("1000");
            res.setMensaje("Envio de correo exitoso");
            return res;


        }catch (Exception ex){
            res.setCodigo("1001");
            res.setMensaje("Error al enviar correo");
            return res;
        }
    }

    public ResponseDto enviarSolicitudSeguroV2(SolicitudPolizaDto pSolicitudPolizaDto){
        ResponseDto res = new ResponseDto();
        try{
            // Crear el asunto
            String vAsunto = "SOLICITUD DE SEGURO";

            // Crear el cuerpo del correo
            StringBuilder vBody = new StringBuilder();

            /*vBody.append("Mediante la Presente se informa de una solicitud de seguro <br>");
            vBody.append("con el siguiente detalle: <br><br>");
            vBody.append("Nombre : "+objRequest.get("nombreCompleto").toString()+" <br>");
            vBody.append("Correo :"+objRequest.get("correoElectronico").toString()+" <br />");
            vBody.append("Ciudad: "+ objRequest.get("ciudad").toString()+" <br />");
            vBody.append("CI: "+objRequest.get("ci").toString()+" <br />" );
            vBody.append("Celular: "+objRequest.get("celular").toString()+" <br />");
            vBody.append("Tipo de seguro: "+objRequest.get("tipoSeguro").toString()+" <br />");
            vBody.append("Mensaje: "+objRequest.get("mensaje").toString()+" <br />");
            vBody.append("<br />");
            vBody.append("GANASEGUROS <br>");
            vBody.append("Correo generado desde la Aplicación Móvil. <br>");*/




            vBody.append("<div style='background-color: #6fbf31; color: #fff; width: 100%; height: 50px; font-size: 40px; text-align: center;'>SOLICITUD DE SEGURO</div>");
            vBody.append("<p>Mediante la presente se informa de una solicitud de Seguro desde la Aplicaci&oacute;n M&oacute;vil con el siguiente detalle:</p>");
            vBody.append("<table style='border-collapse: collapse; width: 100%; height: 126px;' border='1'>");
            vBody.append("<tbody>");
            vBody.append("<tr style='height: 18px;'>");
            vBody.append("<td style='width: 50%; height: 18px; '><strong>Nombre(s)</strong></td>");
            vBody.append("<td style='width: 50%; height: 18px;'>"+pSolicitudPolizaDto.getNombres()!=null?pSolicitudPolizaDto.getNombres():" - "+"</td>");
            vBody.append("</tr>");
            vBody.append("<tr style='height: 18px;'>");
            vBody.append("<td style='width: 50%; height: 18px; '><strong>Apellido(s)</strong></td>");
            vBody.append("<td style='width: 50%; height: 18px;'>"+pSolicitudPolizaDto.getApellidos()!=null?pSolicitudPolizaDto.getApellidos():" - "+"</td>");
            vBody.append("</tr>");
            vBody.append("<tr style='height: 18px;'>");
            vBody.append("<td style='width: 50%; height: 18px; '><strong>Teléfono o Celular</strong></td>");
            vBody.append("<td style='width: 50%; height: 18px;'>"+pSolicitudPolizaDto.getTelefonoCelular()!=null?pSolicitudPolizaDto.getTelefonoCelular():""+"</td>");
            vBody.append("</tr>");
            vBody.append("<tr style='height: 18px;'>");
            vBody.append("<td style='width: 50%; height: 18px; '><strong>Correo</strong></td>");
            vBody.append("<td style='width: 50%; height: 18px;'>"+pSolicitudPolizaDto.getCorreo()!=null?pSolicitudPolizaDto.getCorreo():""+"</td>");
            vBody.append("</tr>");
            vBody.append("<tr style='height: 18px;'>");
            vBody.append("<td style='width: 50%; height: 18px; '><strong>Ciudad</strong></td>");
            vBody.append("<td style='width: 50%; height: 18px;'>"+pSolicitudPolizaDto.getCiudad()!=null?pSolicitudPolizaDto.getCiudad():""+"</td>");
            vBody.append("</tr>");
            vBody.append("<tr style='height: 18px;'>");
            vBody.append("<td style='width: 50%; height: 18px; '><strong>Tiene algun seguro contratado con nosotros?</strong></td>");
            if(pSolicitudPolizaDto.getTieneSeguroConNosotros()){
                vBody.append("<td style='width: 50%; height: 18px;'>SI</td>");
            }else{
                vBody.append("<td style='width: 50%; height: 18px;'>NO</td>");
            }
            vBody.append("</tr>");
            vBody.append("<tr style='height: 18px;'>");
            vBody.append("<td style='width: 50%; height: 18px; '><strong>Tiene algun seguro contratado con otra compañia?</strong></td>");
            if(pSolicitudPolizaDto.getTieneSeguroConOtros()){
                vBody.append("<td style='width: 50%; height: 18px;'>SI</td>");
            }else{
                vBody.append("<td style='width: 50%; height: 18px;'>NO</td>");
            }
            vBody.append("</tr>");

            vBody.append("<tr style='height: 18px;'>");
            vBody.append("<td style='width: 50%; height: 18px; '><strong>Tipo de seguro de interés</strong></td>");
            vBody.append("<td style='width: 50%; height: 18px;'>"+pSolicitudPolizaDto.getTipoSeguroInteresado()!=null?pSolicitudPolizaDto.getTipoSeguroInteresado():""+"</td>");
            vBody.append("</tr>");

            vBody.append("</tbody>");
            vBody.append("</table>");
            vBody.append("<p></p>");
            vBody.append("<hr />");
            vBody.append("<p><strong>GANASEGUROS</strong><br />Correo generado desde la Aplicaci&oacute;n M&oacute;vil.</p>");
            vBody.append(" <img src='https://front-funcionales.azurewebsites.net/img/logo_ganaseguros3.c585e0d6.jpg' > ");


            // Determinar el destinatario copia
            //String vDestino = "azquispe@bg.com.bo";
            String vDestino = "alvaroquispesegales@gmail.com";
            //String vDestino = "alvaro20092004@hotmail.com";


            emailService.enviarCorreoHtml(vDestino,vAsunto,vBody.toString());

            res.setCodigo("1000");
            res.setMensaje("Envio de correo exitoso");
            return res;


        }catch (Exception ex){
            res.setCodigo("1001");
            res.setMensaje("Error al enviar correo");
            return res;
        }
    }

    public ResponseDto descargarPoliza(String pPolicyId){
        ResponseDto res = new ResponseDto();
        Map<String, Object> mapDatosPolizaDetalle = null;

        try{
            String token = this.obtenerToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            // Consulta Detalle de Póliza y Pagos Pendientes por "PolicyId"
            // https://ap1.salesforce.com/services/apexrest/vlocity_ins/v1/integrationprocedure/bg_ti_g_getPolicyInformation
            String urlDetallePolizas = baseUrl + "/services/apexrest/vlocity_ins/v1/integrationprocedure/bg_ti_g_getPolicyInformation?PolicyId=" + pPolicyId + "&Status=Pending";
            HttpEntity requestDetPoliza = new HttpEntity(headers);
            ResponseEntity<String> resultMapDatosPolizaDetalle = restTemplate.exchange(
                    urlDetallePolizas,
                    HttpMethod.GET,
                    requestDetPoliza,
                    String.class,
                    1
            );
            if (resultMapDatosPolizaDetalle != null && resultMapDatosPolizaDetalle.getStatusCode().value() == 200) {
                mapDatosPolizaDetalle = new ObjectMapper().readValue(resultMapDatosPolizaDetalle.getBody(), Map.class);
                if (mapDatosPolizaDetalle.get("codigoMensaje").equals("CODSF1002")) {
                    List<Map<String, Object>> lstMapPolizaDetalle = oMapper.convertValue(mapDatosPolizaDetalle.get("Poliza"), ArrayList.class);
                    Map<String, Object> objMapPolizaDetalle = oMapper.convertValue(lstMapPolizaDetalle.get(0), Map.class);

                    res.setCodigo("COD-SAT-1000");
                    res.setMensaje("Poliza Obtenida Exitosamente");
                    res.setElementoGenerico(objMapPolizaDetalle.get("ArchivoBase64"));


                }}
        }catch (Exception ex){
            res.setCodigo("COD-SAT-1001");
            res.setMensaje("Error :"+ex.toString());
        }
        return res;
    }
}
