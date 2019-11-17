package com.business.unknow.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.business.unknow.client.common.AbstractClient;
import com.business.unknow.client.endpoints.SwSapiensEndpoints;
import com.business.unknow.client.interfaces.RestSwSapiensClient;
import com.business.unknow.client.model.swsapiens.SwSapiensClientException;
import com.business.unknow.client.model.swsapiens.SwSapiensConfig;
import com.business.unknow.client.model.swsapiens.SwSapiensConstans;
import com.business.unknow.client.model.swsapiens.SwSapiensErrorMessage;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unused")
public class RestSwSapiensClientImpl extends AbstractClient implements RestSwSapiensClient {

	private String token;
	private String user;
	private String pw;

	public RestSwSapiensClientImpl(String url, String contextPath, String user, String pw) {
		super(url, contextPath);
		this.pw = pw;
		this.user = user;
	}

	private static final Logger log = LoggerFactory.getLogger(RestSwSapiensClientImpl.class);

	@Override
	protected <T> T parseResponse(Response response, TypeReference<T> entityType) throws SwSapiensClientException {
		T result = null;
		int status = response.getStatus();
		log.info("Status {}", status);
		try {
			String content = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			if (response.getStatusInfo().getFamily() == Status.Family.SUCCESSFUL) {
				if (!content.isEmpty()) {
					try (StringReader reader = new StringReader(content)) {
						result = mapper.readValue(reader, entityType);
					}
				}
			} else {
				log.error("Error response: {}", content);
				if (content.contains("message")) {
					try (StringReader reader = new StringReader(content)) {
						SwSapiensErrorMessage error = mapper.readValue(reader,
								new TypeReference<SwSapiensErrorMessage>() {
								});
						log.info(error.toString());
						throw new SwSapiensClientException(error, status);
					}
				} else {
					SwSapiensErrorMessage error = new SwSapiensErrorMessage("Unexpected services response",
							String.format("Error response: %s", content));
					throw new SwSapiensClientException(error, status);
				}
			}
		} catch (IOException e) {
			SwSapiensErrorMessage error = new SwSapiensErrorMessage();
			error.setMessage("Client Error, the deserialization of entity " + entityType.getClass().getName()
					+ " can't be done.");
			error.setMessageDetail(e.getMessage());
			throw new SwSapiensClientException(error, SwSapiensConstans.UNPROCESSABLE_ENTITY);
		} catch (SwSapiensClientException e) {
			throw e;
		} catch (Exception e) {
			SwSapiensErrorMessage error = new SwSapiensErrorMessage();
			error.setMessage("Unhandled Error in client implementation");
			error.setMessageDetail(e.getMessage());
			throw new SwSapiensClientException(error, SwSapiensConstans.UNPROCESSABLE_ENTITY);
		}
		return result;
	}

	@Override
	public SwSapiensConfig authenticate(String usr, String pw) throws SwSapiensClientException {
		log.info("authenticate with Sw Sapiens.");
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add(SwSapiensConstans.USER, usr);
		headers.add(SwSapiensConstans.PW, pw);
		String endpoint = SwSapiensEndpoints.getAuthenticateEndpoint();
		Response response = post(endpoint, MediaType.APPLICATION_JSON, null, headers);
		return parseResponse(response, new TypeReference<SwSapiensConfig>() {
		});
	}

	@Override
	public SwSapiensConfig validateRfc(String rfc) throws SwSapiensClientException {
		log.info("Validating rfc.");
		authenticate();
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add(SwSapiensConstans.TOKEN_PARAMETER, token);
		String endpoint = SwSapiensEndpoints.getValidateRfcEndpoint(rfc);
		Response response = get(endpoint, MediaType.APPLICATION_JSON, headers);
		return parseResponse(response, new TypeReference<SwSapiensConfig>() {
		});
	}

	private void authenticate() throws SwSapiensClientException {
		if (token == null) {
			SwSapiensConfig config = authenticate(this.user, this.pw);
			token = config.getData().getToken();
		}
	}

	@Override
	public SwSapiensConfig stamp(String xml, String version) throws SwSapiensClientException {
		log.info("Stamping the invoice.");
		authenticate();
		String boundary = UUID.randomUUID().toString();
		String raw = SwSapiensConstans.TOKEN_SEPARATOR + boundary + SwSapiensConstans.TOKEN_SUFIX + xml
				+ SwSapiensConstans.TOKEN_ENTER + boundary + SwSapiensConstans.TOKEN_SEPARATOR;
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add(SwSapiensConstans.TOKEN_PARAMETER, token);
		headers.add(SwSapiensConstans.CONTENT_TYPE_PARAMETER, SwSapiensConstans.CONTENT_TYPE_VALUE.concat(boundary));
		headers.add(SwSapiensConstans.CONTENT_DISPOSITION_PARAMETER, SwSapiensConstans.CONTENT_DISPOSITION_VALUE);
		String endpoint = SwSapiensEndpoints.getStampByVersionEndpoint(version);
		Response response = post(endpoint, SwSapiensConstans.CONTENT_TYPE_VALUE.concat(boundary),
				raw, headers);
		return parseResponse(response, new TypeReference<SwSapiensConfig>() {
		});
	}

}
