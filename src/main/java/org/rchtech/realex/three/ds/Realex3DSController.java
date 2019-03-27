package org.rchtech.realex.three.ds;

import java.io.IOException;

import org.rchtech.realex.three.ds.response.Verify3DResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.api.entities.exceptions.ConfigurationException;

@RestController
public class Realex3DSController {

	@Autowired
	private Realex3DSService request;

	@GetMapping(value = "/verifyEnrolled")
	private String verify() throws ConfigurationException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(request.verifyEnrolled());
	}

	@PostMapping(value = "/process3dSecure")
	private String process3dSecure(@RequestBody String pares)
			throws JsonParseException, JsonMappingException, IOException, ConfigurationException {
		final Verify3DResponse response = getResponse(pares);
		System.out.println("Pares = " + response.getPares());
		System.out.println("MD = " + response.getMd());
		return request.verifyAuthenticationResult(response.getPares());
	}

	private Verify3DResponse getResponse(String pares) {
		final Verify3DResponse response = new Verify3DResponse();
		for (String string : pares.split("&")) {
			final String[] value = string.split("=");
			if (value[0].equalsIgnoreCase("pares")) {
				response.setPares(value[1]);
			} else if (value[0].equalsIgnoreCase("md")) {
				// response.setMd(value[1]);
			}
		}
		return response;
	}

}
