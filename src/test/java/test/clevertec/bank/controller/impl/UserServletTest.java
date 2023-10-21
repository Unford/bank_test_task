package test.clevertec.bank.controller.impl;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.ServiceName;
import by.clevertec.bank.controller.ServletPath;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.controller.impl.UserServlet;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.UserDto;
import by.clevertec.bank.service.impl.UserServiceImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.gen.DataGenerator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServletTest {
    private Tomcat tomcat;
    @Mock
    private UserServiceImpl service;

    private final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("dd-MM-yyyy hh:mm"))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @BeforeEach
    void setUp() throws LifecycleException {
        tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        Connector connector = tomcat.getConnector();
        connector.setPort(8082);

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);
        context.getServletContext().setAttribute(ServiceName.USER_SERVICE, service);

        String servletName = "UserServlet";
        UserServlet servlet = new UserServlet();

        tomcat.addServlet(contextPath, servletName, servlet);
        context.addServletMappingDecoded(ServletPath.USER, servletName);

        tomcat.start();
        tomcat.getService().addConnector(connector);
    }


    @AfterEach
    void tearDown() throws LifecycleException {
        tomcat.stop();
        tomcat.destroy();
    }

    private URIBuilder getUriBuilder() throws UnknownHostException {
        return URIBuilder
                .localhost()
                .setScheme(URIScheme.HTTP.getId())
                .setPort(8082)
                .setPath(ServletPath.USER);
    }

    @Test
    void shouldRequestListAndGetListOfUsers() throws IOException, ServiceException, URISyntaxException {
        List<UserDto> expected = List.of(DataGenerator.generateUserDto(), DataGenerator.generateUserDto());
        Mockito.when(service.findAll()).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder().build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity1 = response.getEntity();
                List<UserDto> accountDto = mapper.readValue(EntityUtils.toString(entity1),
                        new TypeReference<>() {
                        });

                EntityUtils.consume(entity1);
                return accountDto;
            });
            assertThat(actual).containsAll(expected);
            Mockito.verify(service, Mockito.times(1)).findAll();

        }
    }

    @Test
    void shouldRequestServletAndGetUserById() throws IOException, ServiceException, URISyntaxException {
        UserDto expected = DataGenerator.generateUserDto();
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                UserDto mapped = mapper.readValue(EntityUtils.toString(entity), UserDto.class);
                EntityUtils.consume(entity);
                return mapped;
            });
            assertThat(actual).isEqualTo(expected);
            Mockito.verify(service, Mockito.times(1)).findById(Mockito.anyLong());
        }


    }

    @Test
    void shouldRequestUserByIdWithCommand() throws IOException, ServiceException, URISyntaxException {
        UserDto expected = DataGenerator.generateUserDto();
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(expected);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_USER_BY_ID.name())
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                UserDto mapped = mapper.readValue(EntityUtils.toString(entity), UserDto.class);
                EntityUtils.consume(entity);
                return mapped;
            });


            assertThat(actual).isEqualTo(expected);
            Mockito.verify(service, Mockito.times(1)).findById(Mockito.anyLong());
        }


    }


    @Test
    void shouldRequestPutUserAndGetUpdated() throws IOException, ServiceException, URISyntaxException {
        UserDto expected = DataGenerator.generateUserDto();
        Mockito.when(service.update(Mockito.any())).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(getUriBuilder().build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(expected));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);
            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                UserDto mapped = mapper.readValue(EntityUtils.toString(entity), UserDto.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).update(Mockito.any());
        }


    }

    @Test
    void shouldRequestPostUserAndGetCreated() throws IOException, ServiceException, URISyntaxException {
        UserDto expected = DataGenerator.generateUserDto();
        Mockito.when(service.create(Mockito.any())).thenReturn(expected);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(getUriBuilder().build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(expected));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                UserDto mapped = mapper.readValue(EntityUtils.toString(entity), UserDto.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).create(Mockito.any());
        }


    }

    @Test
    void shouldRequestDeleteById() throws IOException, ServiceException, URISyntaxException {
        Boolean expected = true;
        Mockito.when(service.deleteById(Mockito.anyLong())).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(getUriBuilder()
                    .setParameter(RequestParameter.ID, "1")
                    .build());
            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                Boolean mapped = mapper.readValue(EntityUtils.toString(entity), Boolean.class);
                EntityUtils.consume(entity);
                return mapped;
            });

            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).deleteById(Mockito.anyLong());
        }


    }

}


