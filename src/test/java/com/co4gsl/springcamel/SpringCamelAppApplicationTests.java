package com.co4gsl.springcamel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringCamelAppApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void happyOrderPathTest() throws IOException {
		File resource = ResourceUtils.getFile("classpath:valid-order.json");
		String requestJsonAsString = new String(Files.readAllBytes(resource.toPath()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(requestJsonAsString, headers);

		ResponseEntity<String> responseAsStr = restTemplate.postForEntity("/api/order", request, String.class);
		assertThat(responseAsStr.getStatusCodeValue(), is(200));
		String body = responseAsStr.getBody();

		assertThat(body, is(notNullValue()));
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> jsonMap = objectMapper.readValue(body,
				new TypeReference<List<Map<String,Object>>>(){});

		assertThat(jsonMap.get(0).get("productName"), is("Plum"));
		assertThat(jsonMap.get(0).get("quantity"), is(10));
	}

	@Test
	public void unhappyOrderPathTest() throws IOException {
		File resource = ResourceUtils.getFile("classpath:invalid-order.json");
		String requestJsonAsString = new String(Files.readAllBytes(resource.toPath()));
		ObjectMapper objectMapper = new ObjectMapper();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(requestJsonAsString, headers);

		ResponseEntity<String> responseAsStr = restTemplate.postForEntity("/api/order", request, String.class);
		assertThat(responseAsStr.getStatusCodeValue(), is(400));
		String body = responseAsStr.getBody();

		assertThat(body, is(notNullValue()));
		JsonNode root = objectMapper.readTree(body);
		assertThat(root.path("message").asText(), is("Not a valid order"));
	}
}
