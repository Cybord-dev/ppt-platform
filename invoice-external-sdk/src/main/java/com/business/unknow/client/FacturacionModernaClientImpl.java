package com.business.unknow.client;

import java.io.IOException;
import java.io.StringReader;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.business.unknow.client.common.AbstractClient;
import com.business.unknow.client.endpoints.FacturacionModernaEndpoints;
import com.business.unknow.client.interfaces.RestFacturacionModernaClient;
import com.business.unknow.client.model.facturacionmoderna.FacturaModernaClientException;
import com.business.unknow.client.model.facturacionmoderna.FacturaModernaRequestModel;
import com.business.unknow.client.model.facturacionmoderna.SoapRequest;
import com.business.unknow.client.model.swsapiens.SwSapiensClientException;
import com.business.unknow.client.model.swsapiens.SwSapiensConstans;
import com.business.unknow.client.model.swsapiens.SwSapiensErrorMessage;
import com.business.unknow.model.error.InvoiceCommonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FacturacionModernaClientImpl extends AbstractClient implements RestFacturacionModernaClient {

	private static final Logger log = LoggerFactory.getLogger(FacturacionModernaClientImpl.class);

	public FacturacionModernaClientImpl(String url, String contextPath) {
		super(url, contextPath);
	}

	@Override
	protected <T> T parseResponse(Response response, TypeReference<T> entityType) throws SwSapiensClientException {
		T result = null;
		int status = response.getStatus();
		log.info("Status {}", status);
		System.out.println();
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
	public void stamp(FacturaModernaRequestModel request) throws FacturaModernaClientException{
		try {
			log.info("Stamping the invoice.");
			SoapRequest soapRequest = new SoapRequest();
			String endpoint = FacturacionModernaEndpoints.getTimbradoEndpoint();
			Response response = post(endpoint, MediaType.TEXT_PLAIN, soapRequest.createSoapEnvelope(request));
			System.out.println(response.readEntity(String.class));
		} catch (InvoiceCommonException e) {
			throw new FacturaModernaClientException(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		FacturacionModernaClientImpl client= new FacturacionModernaClientImpl("https://t1demo.facturacionmoderna.com", "");
		FacturaModernaRequestModel requestModel = new FacturaModernaRequestModel();
		requestModel.setUser("UsuarioPruebasWS");
		requestModel.setUserPass("b9ec2afa3361a59af4b4d102d3f704eabdf097d4");
		requestModel.setXml(
				"PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48Y2ZkaTpDb21wcm9iYW50ZSB4c2k6c2NoZW1hTG9jYXRpb249Imh0dHA6Ly93d3cuc2F0LmdvYi5teC9jZmQvMyBodHRwOi8vd3d3LnNhdC5nb2IubXgvc2l0aW9faW50ZXJuZXQvY2ZkLzMvY2ZkdjMzLnhzZCIgVmVyc2lvbj0iMy4zIiBTZXJpZT0iQSIgRm9saW89IjAxIiBGZWNoYT0iMjAxOS0wOS0yNFQxNDo0NzowOCIgU2VsbG89IkRtZmJBeEtQc0JqSFI0d21GU0g4RHV4WW05VU9sSEVlc2UvTEFrNXdGb1RVWmNBTnV0R2dBTzhqNldBTlZnUFB0ckh0c3JQOHJLYlZ1aWVoSFRVTFNCNWMzSWMwbDNTd3BubGl2dk83aTVzclZlYXJyU1QrWisyaFZ6ejUwYm96UEJsaHFaWHYvVEo1TE8xcTZQZXZ4ZkszdnQzelZNbUlZMDZxdnBDN1E4RlVVdHhUQVJpenpRdFdOMDJaeDA1cmdZenFHc0NKZGVKZUFpRHVhV1MxTVVaYnU4ekdvTnBpMGxacklxYUlmQWhrRXVlWVA2d0JqRVhkdXEyKzdXOUczeVFNaDFoMjFZUkV6cjNWK3JzVXZqQUt2UE9RR2JVQ2xGVFJDeHhKUWpDanRMSzE0dDdEKy9hRFhHMytTSkFFNVhwVU1STm02akJjUWRQbUlHalBzdz09IiBGb3JtYVBhZ289IjAzIiBOb0NlcnRpZmljYWRvPSIyMDAwMTAwMDAwMDMwMDAyMjgxNSIgQ2VydGlmaWNhZG89Ik1JSUZ4VENDQTYyZ0F3SUJBZ0lVTWpBd01ERXdNREF3TURBek1EQXdNakk0TVRVd0RRWUpLb1pJaHZjTkFRRUxCUUF3Z2dGbU1TQXdIZ1lEVlFRRERCZEJMa011SURJZ1pHVWdjSEoxWldKaGN5ZzBNRGsyS1RFdk1DMEdBMVVFQ2d3bVUyVnlkbWxqYVc4Z1pHVWdRV1J0YVc1cGMzUnlZV05wdzdOdUlGUnlhV0oxZEdGeWFXRXhPREEyQmdOVkJBc01MMEZrYldsdWFYTjBjbUZqYWNPemJpQmtaU0JUWldkMWNtbGtZV1FnWkdVZ2JHRWdTVzVtYjNKdFlXTnB3N051TVNrd0p3WUpLb1pJaHZjTkFRa0JGaHBoYzJsemJtVjBRSEJ5ZFdWaVlYTXVjMkYwTG1kdllpNXRlREVtTUNRR0ExVUVDUXdkUVhZdUlFaHBaR0ZzWjI4Z056Y3NJRU52YkM0Z1IzVmxjbkpsY204eERqQU1CZ05WQkJFTUJUQTJNekF3TVFzd0NRWURWUVFHRXdKTldERVpNQmNHQTFVRUNBd1FSR2x6ZEhKcGRHOGdSbVZrWlhKaGJERVNNQkFHQTFVRUJ3d0pRMjk1YjJGanc2RnVNUlV3RXdZRFZRUXRFd3hUUVZRNU56QTNNREZPVGpNeElUQWZCZ2txaGtpRzl3MEJDUUlNRWxKbGMzQnZibk5oWW14bE9pQkJRMFJOUVRBZUZ3MHhOakV3TWpVeU1UVXlNVEZhRncweU1ERXdNalV5TVRVeU1URmFNSUd4TVJvd0dBWURWUVFERXhGRFNVNUVSVTFGV0NCVFFTQkVSU0JEVmpFYU1CZ0dBMVVFS1JNUlEwbE9SRVZOUlZnZ1UwRWdSRVVnUTFZeEdqQVlCZ05WQkFvVEVVTkpUa1JGVFVWWUlGTkJJRVJGSUVOV01TVXdJd1lEVlFRdEV4eE1RVTQzTURBNE1UY3pValVnTHlCR1ZVRkNOemN3TVRFM1FsaEJNUjR3SEFZRFZRUUZFeFVnTHlCR1ZVRkNOemN3TVRFM1RVUkdVazVPTURreEZEQVNCZ05WQkFzVUMxQnlkV1ZpWVY5RFJrUkpNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQWd2dkNpQ0ZERlZhWVg3eGRWUmhwLzM4VUxXdG8vTEtEU1p5MXlyWEtwYXFGWHFFUkpXRjc4WUhLZjNONUdCb1hnendGUHVEWCs1a3ZZNXd0WU54eC9Pd3Uyc2hOWnFGRmg2RUtzeXNRTWVQNXJ6NmtFMWdGWWVuYVBFVVA5emoraDBiTDN4UjVhcW9Uc3FHRjI0bUtCTG9pYUs0NHBYQnpHemdzeFppc2hWSlZNNlhiek5KVm9uRVVOYkkyNURoZ1dBZDg2ZjJhVTNCbU9IMksxUlp4NDFkdFRUNTZVc3N6SmxzNHRQRk9Eci9jYVd1WkV1VXZMcDFNM25qN0R5dTg4bWhEMmYrMWZBL2c3a3pjVS8xdGNwRlhGL3JJeTkzQVB2a1U3Mmp3dmtybnByenMrU25HODErL0YxNmFodUdzYjJFWjg4ZEtId3F4RWt3emhNeVRiUUlEQVFBQm94MHdHekFNQmdOVkhSTUJBZjhFQWpBQU1Bc0dBMVVkRHdRRUF3SUd3REFOQmdrcWhraUc5dzBCQVFzRkFBT0NBZ0VBSi94a0w4SStmcGlsWlArOWFPOG45MysyMFh4Vm9tTEpqZVNMK05nMkVyTDJHZ2F0cEx1TjVKa25GQmtaQWh4VklnTWFUUzIzenprMVJMdFJhWXZIODNsQkg1RStNK2tFakZHcDE0Rm5lMWlWMlBtM3ZMNGplTG16SGdZMUtmNUhtZVZycnA0UFU3V1FnMTZWcHlIYUovZW9uUE5pRUJVamN5UTFpRmZrekptblNKdkRHdGZRSzJUaUVvbERKQXBZdjBPV2RtNGlzOUJzZmk5ajZsSTkvVDZNTlorL0xNMkwvdDcyVmF1NHI3bTk0SkRFemFPM0Ewd0hBdFE5N2ZqQmZCaU81TThBRUlTQVY3ZVppZElsM2lhSkpIa1FiQllpaVcyZ2lrcmVVWktQVVgwSG1sbklxcVFjQkpoV0tSdTZOcWs2YVpCVEVUTExwR3J2RjlPQXJWMUpTc2Jkdy9aSCtQODhSQXQ1ZW01L2dqd3d0RmxOSHlpS0c1dytVRnBhWk9LM2daUDBzdTBzYTZkbFBlUTlFTDRKbEZrR3FRQ2dTUStOT3NYcWFPYXZnb1A1Vkx5a0x3dUdud0lVbnVoQlRWZURienBncmc5THVGNWRZcC96cytZOVNjSnFlNVZNQWFnTFNZVFNoTnROOGx1VjdMdnhGOXBnV3daZGNNN2xVd3FKbVVkZENpWnFkbmdnM3Z6VGFjdE1Ub0cxNmdaQTRDV25NZ2JVNEUrcjU0MStGTk1wZ0FaTnZzMkNpVy9lQXBmYWFRb2pzWkVBSERzRHY0TDVuM00xQ0M3ZllqRS9kNjFhU25nMUxhTzZUMW1oK2RFZlB2THpwN3p5enorVWdXTWhpNUNzNHBjWHgxZWljNXI3dXhQb0J3Y0NUdDNZSTFqS1ZWblY3L3c9IiBDb25kaWNpb25lc0RlUGFnbz0iQ09OVEFETyIgU3ViVG90YWw9IjE4NTAiIERlc2N1ZW50bz0iMTc1LjAwIiBNb25lZGE9Ik1YTiIgVG90YWw9IjE5NDMuMDAiIFRpcG9EZUNvbXByb2JhbnRlPSJJIiBNZXRvZG9QYWdvPSJQVUUiIEx1Z2FyRXhwZWRpY2lvbj0iNjgwNTAiIHhtbG5zOmNmZGk9Imh0dHA6Ly93d3cuc2F0LmdvYi5teC9jZmQvMyIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSI+PGNmZGk6RW1pc29yIFJmYz0iTEFONzAwODE3M1I1IiBOb21icmU9IkNJTkRFTUVYIFNBIERFIENWIiBSZWdpbWVuRmlzY2FsPSI2MDEiIC8+PGNmZGk6UmVjZXB0b3IgUmZjPSJTQUYwODA4MDRSSDciIE5vbWJyZT0iU0lTVEVNQVMgQURNSU5JU1RSQVRJVk9TIEZVTkNJT05BTEVTIEVDTywgUy5BLiBERSBDLlYuIiBVc29DRkRJPSJHMDEiIC8+PGNmZGk6Q29uY2VwdG9zPjxjZmRpOkNvbmNlcHRvIENsYXZlUHJvZFNlcnY9IjAxMDEwMTAxIiBOb0lkZW50aWZpY2FjaW9uPSJBVUxPRzAwMSIgQ2FudGlkYWQ9IjUiIENsYXZlVW5pZGFkPSJIODciIFVuaWRhZD0iUGllemEiIERlc2NyaXBjaW9uPSJBdXJyaWN1bGFyZXMgVVNCIExvZ2l0ZWNoIiBWYWxvclVuaXRhcmlvPSIzNTAuMDAiIEltcG9ydGU9IjE3NTAuMDAiIERlc2N1ZW50bz0iMTc1LjAwIj48Y2ZkaTpJbXB1ZXN0b3M+PGNmZGk6VHJhc2xhZG9zPjxjZmRpOlRyYXNsYWRvIEJhc2U9IjE1NzUuMDAiIElåtcHVlc3RvPSIwMDIiIFRpcG9GYWN0b3I9IlRhc2EiIFRhc2FPQ3VvdGE9IjAuMTYwMDAwIiBJbXBvcnRlPSIyNTIuMDAiIC8+PC9jZmRpOlRyYXNsYWRvcz48L2NmZGk6SW1wdWVzdG9zPjwvY2ZkaTpDb25jZXB0bz48Y2ZkaTpDb25jZXB0byBDbGF2ZVByb2RTZXJ2PSI0MzIwMTgwMCIgTm9JZGVudGlmaWNhY2lvbj0iVVNCIiBDYW50aWRhZD0iMSIgQ2xhdmVVbmlkYWQ9Ikg4NyIgVW5pZGFkPSJQaWV6YSIgRGVzY3JpcGNpb249Ik1lbW9yaWEgVVNCIDMyZ2IgbWFyY2EgS2luZ3N0b24iIFZhbG9yVW5pdGFyaW89IjEwMC4wMCIgSW1wb3J0ZT0iMTAwLjAwIj48Y2ZkaTpJbXB1ZXN0b3M+PGNmZGk6VHJhc2xhZG9zPjxjZmRpOlRyYXNsYWRvIEJhc2U9IjEwMC4wMCIgSW1wdWVzdG89IjAwMiIgVGlwb0ZhY3Rvcj0iVGFzYSIgVGFzYU9DdW90YT0iMC4xNjAwMDAiIEltcG9ydGU9IjE2LjAwIiAvPjwvY2ZkaTpUcmFzbGFkb3M+PC9jZmRpOkltcHVlc3Rvcz48L2NmZGk6Q29uY2VwdG8+PC9jZmRpOkNvbmNlcHRvcz48Y2ZkaTpJbXB1ZXN0b3MgVG90YWxJbXB1ZXN0b3NUcmFzbGFkYWRvcz0iMjY4LjAwIj48Y2ZkaTpUcmFzbGFkb3M+PGNmZGk6VHJhc2xhZG8gSW1wdWVzdG89IjAwMiIgVGlwb0ZhY3Rvcj0iVGFzYSIgVGFzYU9DdW90YT0iMC4xNjAwMDAiIEltcG9ydGU9IjI2OC4wMCIgLz48L2NmZGk6VHJhc2xhZG9zPjwvY2ZkaTpJbXB1ZXN0b3M+PC9jZmRpOkNvbXByb2JhbnRlPg==");
		requestModel.setRfc("LAN7008173R5");
		requestModel.setGenerarTxt(true);
		requestModel.setGenerarPdf(true);
		requestModel.setGenerarCbb(true);
		try {
			client.stamp(requestModel);
		} catch (FacturaModernaClientException e) {
			e.printStackTrace();
		}
	}


}
