package org.max.home.accu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.max.home.accuweather.location.Country;
import org.max.home.accuweather.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetLocationAPITest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(GetLocationAPITest.class);

    @Test
    void getIPAddressSearch200() throws IOException, URISyntaxException {
        logger.info("Описание теста: Информация о местоположении по IP-адресу (код ответа 200)");
        logger.info("Тест запущен");
        //given
        ObjectMapper mapper = new ObjectMapper();

        Country country = new Country();
        country.setId("1");
        country.setEnglishName("Russia");
        country.setLocalizedName("Russia");

        Location location = new Location();
        location.setCountry(country);

        logger.debug("Формирование мока для Get /locations/v1/cities/ipaddress");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/ipaddress"))
                .withQueryParam("apikey", notMatching("HBAoYTHBeloWennir9bjrNSNYgoGaCqz"))
                .willReturn(aResponse()
                        .withStatus(200).withBody(mapper.writeValueAsString(location))));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/cities/ipaddress");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apikey", "12345qwerty")
                .build();
        request.setURI(uri);
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/ipaddress")));
        assertEquals(200, response.getStatusLine().getStatusCode());

        Location responseBody = mapper.readValue(response.getEntity().getContent(), Location.class);
        assertEquals("1", responseBody.getCountry().getId());
        assertEquals("Russia", responseBody.getCountry().getEnglishName());
        assertEquals("Russia", responseBody.getCountry().getLocalizedName());
        logger.info("Тест успешно пройден");
    }

    @Test
    void getLocationByIPServiceUnavailable() throws URISyntaxException, IOException {
        logger.info("Описание теста: Информация о местоположении по IP-адресу (код ответа 503 - сервис недоступен)");
        logger.info("Тест запущен");
        //given
        logger.debug("Формирование мока для Get /locations/v1/cities/ipaddress");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/ipaddress"))
                .withQueryParam("apikey", notMatching("HBAoYTHBeloWennir9bjrNSNYgoGaCqz"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withBody("Сервис временно недоступен")));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/cities/ipaddress");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apikey", "12345qwerty")
                .build();
        request.setURI(uri);
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/ipaddress")));
        assertEquals(503, response.getStatusLine().getStatusCode());
        assertEquals("Сервис временно недоступен",
                EntityUtils.toString(response.getEntity(), "UTF-8"));
        logger.info("Тест успешно пройден");
    }
}
