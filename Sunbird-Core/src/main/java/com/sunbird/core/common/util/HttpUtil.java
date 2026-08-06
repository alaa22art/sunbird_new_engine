package com.sunbird.core.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;

public class HttpUtil {

	public static final DefaultResponseErrorHandler SPRING_DEFAULT_ERROR_HANDLER = new DefaultResponseErrorHandler();

	/**
	 * Send a POST request with query parameters.
	 * 
	 * @param url
	 * @param uriVariables : the query string parameters
	 * @param uriVariablesEncoding : keys and values of the query string parameters, to know what to encode.
	 * @param clazz : the response type from the client
	 *
	 * @return ResponseEntity
	 */
	public static <T> ResponseEntity<T> queryParamsPost(String url, Map<String, String> uriVariables,
			Map<String, Boolean> uriVariablesEncoding, Class<T> clazz) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.setErrorHandler(SPRING_DEFAULT_ERROR_HANDLER);
			StringBuilder urlParameters = new StringBuilder();
			for (Map.Entry<String, String> entry : uriVariables.entrySet()) {
				if (urlParameters.length() != 0) {//don't add & if it is the first parameter
					urlParameters.append("&");
				}
				String key = uriVariablesEncoding.get(entry.getKey()) != null
						&& uriVariablesEncoding.get(entry.getKey()) == Boolean.TRUE
								? URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name())
								: entry.getKey();
				urlParameters.append(key);
				urlParameters.append("=");
				urlParameters.append("{" + key + "}");
				String value = uriVariablesEncoding.get(entry.getValue()) != null
						&& uriVariablesEncoding.get(entry.getValue()) == Boolean.TRUE
								? URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name())
								: entry.getValue();
				entry.setValue(value);
			}
			url += "?" + urlParameters.toString();//http://example.org + '?'+ foo={foo}&bar={bar}
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.ALL));
			HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
			ResponseEntity<T> re = restTemplate.exchange(url, HttpMethod.POST, requestEntity, clazz, uriVariables);
			return re;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage(), e.getLocalizedMessage(), ErrorSeverity.ERROR);
		} catch (RestClientException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage(), e.getLocalizedMessage(), ErrorSeverity.ERROR);
		}
	}

	public static <T> ResponseEntity<T> formPost(String url, Map<String, Object> data, Map<String, String> customHeaders,
			Class<T> responseType) {

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(SPRING_DEFAULT_ERROR_HANDLER);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		if (!CollectionUtil.isMapEmpty(customHeaders)) {
			for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
				headers.set(entry.getKey(), entry.getValue());
			}
		}

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		if (!CollectionUtil.isMapEmpty(data)) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				map.set(entry.getKey(), entry.getValue());
			}
		}

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

		return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
	}

	/**
	 * Sending a http request.
	 * 
	 * @param url
	 * @param objectToSend
	 * @param responseType
	 * @param customHttpMethod : nullable
	 * @param customHeaders : nullable
	 * @param customResponseErrorHandler : nullable
	 * @param uriVariables : nullable
	 * @return ResponseEntity
	 */
	public static <T, V> ResponseEntity<V> request(String url, HttpMethod customHttpMethod, T objectToSend,
			MediaType mediaType, Map<String, String> customHeaders, Class<V> responseType,
			ResponseErrorHandler customResponseErrorHandler, Object... uriVariables) {
		HttpHeaders headers = new HttpHeaders();
		if (mediaType != null) {
			headers.setContentType(mediaType);
		}
		if (!CollectionUtil.isMapEmpty(customHeaders)) {
			for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
				headers.set(entry.getKey(), entry.getValue());
			}
		}
		ResponseErrorHandler responseErrorHandler = customResponseErrorHandler != null ? customResponseErrorHandler
				: SPRING_DEFAULT_ERROR_HANDLER;
		HttpMethod method = customHttpMethod != null ? customHttpMethod : HttpMethod.GET;
		HttpEntity<T> request = new HttpEntity<T>(objectToSend, headers);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(responseErrorHandler);
		return restTemplate.exchange(url, method, request, responseType, uriVariables);
	}

	/**
	 * Using Firebase Dynamic-Link
	 * https://firebase.google.com/docs/dynamic-links/rest
	 * 
	 * @param url
	 * @return short url
	 */
	public static String shortenUrl(String url) {
		Map<String, Map<String, String>> requestBody = new HashMap<>();
		Map<String, String> details = new HashMap<>();
		details.put("domainUriPrefix", "https://acculab.page.link");
		details.put("link", url);
		requestBody.put("dynamicLinkInfo", details);
		ResponseEntity<String> responseEntity = HttpUtil.request(
				"https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=AIzaSyAWYkwd316NJljeXhdlmrVfamdevMctTAg", HttpMethod.POST,
				requestBody, MediaType.APPLICATION_JSON, null, String.class, null);
		Map<String, Object> map4 = JSONUtil.convertJSONToMap(responseEntity.getBody(), String.class, Object.class);
		return (String) map4.get("shortLink");
	}

	/**
	 * Simple helper method to help you extract the headers from HttpServletRequest object.
	 * 
	 * @param request
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Simple helper method to fetch request data as a string from HttpServletRequest object.
	 * 
	 * @param request
	 * @return Map<String, Object>
	 */
	public static String getBody(HttpServletRequest request) {
		String body = null;
		try {
			body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}

	/**
	 * Generate a list that contains all the variations of the url (http,https,http://www,https://www)
	 * 
	 * @return List
	 */
	public static List<String> generateAllowedOrigins(String serverBaseUrl) {
		List<String> urls = new ArrayList<>();
		urls.add(serverBaseUrl);
		urls.add(serverBaseUrl.replace("//", "//www."));
		String otherUrl = serverBaseUrl.startsWith("https://") ? serverBaseUrl.replace("https://", "http://")
				: serverBaseUrl.replace("http://", "https://");
		urls.add(otherUrl);
		urls.add(otherUrl.replace("//", "//www."));
		return urls;
	}

	/**
	 * Generate an array that contains all the variations of the url (http,https,http://www,https://www)
	 * 
	 * @return String[]
	 */
	public static String[] generateAllowedOriginsAsArray(String serverBaseUrl) {
		List<String> urls = generateAllowedOrigins(serverBaseUrl);
		String[] origins = new String[urls.size()];
		origins = urls.toArray(origins);
		return origins;
	}

	/**
	 * Get the current HttpServletRequest.
	 * 
	 * @return HttpServletRequest: null if no http request
	 */
	public static HttpServletRequest getCurrentHttpRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			return request;
		}
		return null;
	}

	public static int getUserTimezoneOffset() {
		int userTimezoneOffset;
		try {
			userTimezoneOffset = Integer.parseInt(getCurrentHttpRequest().getHeader("TimezoneOffset"));
		} catch (NumberFormatException e) {
			userTimezoneOffset = 0;
		}

		return userTimezoneOffset;
	}

}
