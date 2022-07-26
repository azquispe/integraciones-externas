package com.example.apiintegracioneexternas;

import com.example.apiintegracioneexternas.service.GanaTechService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class ApiIntegracioneExternasApplicationTests {

	@Autowired
	private GanaTechService ganaTechService;

	@Test
	void obtenerToken() {

		String token = ganaTechService.obtenerToken();
		Map<String, Object> datosPersona = ganaTechService.ConsultaSegip(token,"9133040","","22/06/1988");
	}

}
