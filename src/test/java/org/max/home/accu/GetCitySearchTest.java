package org.max.home.accu;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.max.home.accuweather.location.Country;
import org.max.home.accuweather.location.Location;
import org.max.home.accuweather.location.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class GetCitySearchTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(GetCitySearchTest.class);

    @Test
    void getCitySearchReturn200() throws IOException, URISyntaxException {
        logger.info("Описание теста: Возвращает информацию для массива городов, которые соответствуют тексту поиска (код ответа 200)");
        logger.info("Тест запущен");
        ObjectMapper mapper = new ObjectMapper();
        Location location = new Location();
        location.setCountry(new Country("1", "Russia", "Russia"));
        location.setTimeZone(new TimeZone("MSK", "Europe/Moscow", 3.0, false, null));

        logger.debug("Формирование мока для Get /locations/v1/cities/search");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/search"))
                .withQueryParam("apikey", notMatching("HBAoYTHBeloWennir9bjrNSNYgoGaCqz"))
                .withQueryParam("q", equalTo("Moscow"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mapper.writeValueAsString(location))));

        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/cities/search");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apikey", "13151351")
                .addParameter("q", "Moscow")
                .build();
        request.setURI(uri);

        CloseableHttpResponse execute = client.execute(request);
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/search")));

        Assertions.assertEquals(200, execute.getStatusLine().getStatusCode());

        Location readValue = mapper.readValue(execute.getEntity().getContent(), Location.class);
        Assertions.assertEquals("1", readValue.getCountry().getId());
        Assertions.assertEquals("Russia", readValue.getCountry().getEnglishName());
        Assertions.assertEquals("Russia", readValue.getCountry().getLocalizedName());
        Assertions.assertEquals("MSK", readValue.getTimeZone().getCode());
        Assertions.assertEquals("Europe/Moscow", readValue.getTimeZone().getName());
        Assertions.assertEquals(3.0, readValue.getTimeZone().getGmtOffset());
        Assertions.assertEquals(false, readValue.getTimeZone().getIsDaylightSaving());
        Assertions.assertEquals(null, readValue.getTimeZone().getNextOffsetChange());
    }

    @Test
    void getCitySearchReturn204() throws URISyntaxException, IOException {
        logger.info("Описание теста: Возвращает информацию для массива городов, которые соответствуют тексту поиска " +
                "(код ответа 204 - нет содержимого)");
        logger.info("Тест запущен");
        logger.debug("Формирование мока для Get /locations/v1/cities/search");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/search"))
                .withQueryParam("apikey", notMatching("HBAoYTHBeloWennir9bjrNSNYgoGaCqz"))
                .withQueryParam("q", equalTo("Moscow"))
                .willReturn(aResponse()
                        .withStatus(204)));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/cities/search");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apikey", "15151351")
                .addParameter("q", "Moscow")
                .build();
        request.setURI(uri);

        HttpResponse response = httpClient.execute(request);

        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/search")));
        Assertions.assertEquals(204, response.getStatusLine().getStatusCode());
        Assertions.assertNull(response.getEntity());
    }
}
