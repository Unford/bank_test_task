package test.clevertec.bank.controller.impl;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.ServiceName;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.controller.impl.AccountServlet;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.*;
import by.clevertec.bank.service.impl.AccountServiceImpl;
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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;
import test.clevertec.bank.common.DataGenerator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class AccountServletTest {
    private static Tomcat tomcat;
    @Mock
    private AccountServiceImpl service;

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
        context.getServletContext().setAttribute(ServiceName.ACCOUNT_SERVICE, service);

        String servletName = "AccountServlet";
        String urlPattern = "/accounts";
        AccountServlet accountServlet = new AccountServlet();

        tomcat.addServlet(contextPath, servletName, accountServlet);
        context.addServletMappingDecoded(urlPattern, servletName);

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
                .setPath("/accounts");
    }

    @Test
    @DisplayName("Should request a list of accounts and retrieve them")
    void shouldRequestListAndGetListOfAccounts() throws IOException, ServiceException, URISyntaxException {
        List<AccountDto> expected = List.of(DataGenerator.generateAccountDto(), DataGenerator.generateAccountDto());
        Mockito.when(service.findAll()).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder().build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity1 = response.getEntity();
                List<AccountDto> accountDto = mapper.readValue(EntityUtils.toString(entity1),
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
    @DisplayName("Should request a list and receive an error message")
    void shouldRequestListAndGetErrorMessage() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .message("hello")
                .code(CustomError.NOT_FOUND)
                .build();
        Mockito.when(service.findAll()).thenThrow(new ServiceException(expected.getMessage(), expected.getCode()));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder().build());
            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError error = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                assertThat(response.getCode()).isEqualTo(expected.getCode());

                EntityUtils.consume(entity);
                return error;
            });
            assertThat(actual.getMessage()).matches(s -> s.contains(expected.getMessage()));
            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            Mockito.verify(service, Mockito.times(1)).findAll();

        }


    }

    @Test
    @DisplayName("Should request servlet and retrieve account by ID")
    void shouldRequestServletAndGetAccountById() throws IOException, ServiceException, URISyntaxException {
        AccountDto expected = DataGenerator.generateAccountDto();
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                AccountDto accountDto = mapper.readValue(EntityUtils.toString(entity), AccountDto.class);
                EntityUtils.consume(entity);
                return accountDto;
            });
            assertThat(actual).isEqualTo(expected);
            Mockito.verify(service, Mockito.times(1)).findById(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request with negative ID and receive a bad request code")
    void shouldRequestWithNegativeIdAndGetError() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .message("id should be positive number")
                .build();


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "-1")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError error = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return error;
            });

            assertThat(actual.getMessage()).matches(s -> s.contains(expected.getMessage()));
            assertThat(actual.getCode()).isEqualTo(expected.getCode());

            Mockito.verify(service, Mockito.times(0)).findById(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request with invalid ID format and receive a bad request code")
    void shouldRequestWithInvalidFormatIdAndGetError() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "one")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError error = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return error;
            });

            assertThat(actual.getCode()).isEqualTo(expected.getCode());

            Mockito.verify(service, Mockito.times(0)).findById(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request account balance by ID and receive the balance")
    void shouldRequestAccountBalanceById() throws IOException, ServiceException, URISyntaxException {
        AccountExtractDto expected = AccountExtractDto.builder()
                .balance(BigDecimal.ONE)
                .account(AccountDto.builder()
                        .id(1L).build())
                .build();
        Mockito.when(service.getAccountBalance(Mockito.anyLong())).thenReturn(expected.getBalance());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_BALANCE.name())
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                AccountExtractDto error = mapper.readValue(EntityUtils.toString(entity), AccountExtractDto.class);
                EntityUtils.consume(entity);
                return error;
            });

            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).getAccountBalance(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request balance with negative ID and receive a bad request code")
    void shouldRequestBalanceWithNegativeIdAndGetError() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .message("id should be positive number")
                .build();


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "-1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_BALANCE.name())
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError error = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return error;
            });

            assertThat(actual.getMessage()).matches(s -> s.contains(expected.getMessage()));
            assertThat(actual.getCode()).isEqualTo(expected.getCode());

            Mockito.verify(service, Mockito.times(0)).getAccountBalance(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request balance with invalid ID format and receive a bad request error")
    void shouldRequestBalanceWithInvalidIdFormatAndGetError() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "number")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_BALANCE.name())
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError error = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return error;
            });

            assertThat(actual.getCode()).isEqualTo(expected.getCode());

            Mockito.verify(service, Mockito.times(0)).getAccountBalance(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request account extract by ID between dates and receive the extract")
    void shouldRequestAccountExtractByIdBetweenDates() throws IOException, ServiceException, URISyntaxException {
        AccountExtractDto expected = DataGenerator.generateAccountExtractDto();
        Mockito.when(service.getAccountExtract(Mockito.any())).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_EXTRACT.name())
                    .setParameter(RequestParameter.DATE_FROM, "18-08-2023")
                    .setParameter(RequestParameter.DATE_TO, "18-09-2023")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                AccountExtractDto mapped = mapper.readValue(EntityUtils.toString(entity), AccountExtractDto.class);
                EntityUtils.consume(entity);
                return mapped;
            });

            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).getAccountExtract(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request account extract with an invalid ID and receive a bad request error")
    void shouldRequestAccountExtractByInvalidId() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .message("id should be positive number")
                .build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "-1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_EXTRACT.name())
                    .setParameter(RequestParameter.DATE_FROM, "18-08-2023")
                    .setParameter(RequestParameter.DATE_TO, "18-09-2023")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return mapped;
            });


            assertThat(actual.getMessage()).matches(s -> s.contains(expected.getMessage()));
            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            Mockito.verify(service, Mockito.times(0)).getAccountExtract(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request account extract and receive a service error")
    void shouldRequestAccountExtractAndGetServiceError() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .message("some text")
                .build();
        Mockito.when(service.getAccountExtract(Mockito.any()))
                .thenThrow(new ServiceException(expected.getMessage(), expected.getCode()));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_EXTRACT.name())
                    .setParameter(RequestParameter.DATE_FROM, "18-08-2023")
                    .setParameter(RequestParameter.DATE_TO, "18-09-2023")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            assertThat(actual.getMessage()).matches(s -> s.contains(expected.getMessage()));

            Mockito.verify(service, Mockito.times(1)).getAccountExtract(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request account extract with an invalid account number format and receive a bad request error")
    void shouldRequestAccountExtractByInvalidNumberFormat() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "number")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_EXTRACT.name())
                    .setParameter(RequestParameter.DATE_FROM, "18-08-2023")
                    .setParameter(RequestParameter.DATE_TO, "18-09-2023")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            Mockito.verify(service, Mockito.times(0)).getAccountExtract(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request account extract with an invalid 'from' date format and receive a bad request error")
    void shouldRequestAccountExtractByInvalidFromDateFormat() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_EXTRACT.name())
                    .setParameter(RequestParameter.DATE_FROM, "2023-08-18")
                    .setParameter(RequestParameter.DATE_TO, "18-09-2023")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            Mockito.verify(service, Mockito.times(0)).getAccountExtract(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request account extract with an invalid 'to' date format and receive a bad request error")
    void shouldRequestAccountExtractByInvalidToDateFormat() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_EXTRACT.name())
                    .setParameter(RequestParameter.DATE_TO, "2023-08-18")
                    .setParameter(RequestParameter.DATE_FROM, "18-09-2023")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            Mockito.verify(service, Mockito.times(0)).getAccountExtract(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request account statement by ID between dates and receive the statement")
    void shouldRequestAccountStatementByIdBetweenDates() throws IOException, ServiceException, URISyntaxException {
        AccountStatementDto expected = DataGenerator.generateAccountStatementDto();
        Mockito.when(service.getAccountStatement(Mockito.any())).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ACCOUNT_STATEMENT.name())
                    .setParameter(RequestParameter.DATE_FROM, "18-08-2023")
                    .setParameter(RequestParameter.DATE_TO, "18-09-2023")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                AccountStatementDto mapped = mapper.readValue(EntityUtils.toString(entity), AccountStatementDto.class);
                EntityUtils.consume(entity);
                return mapped;
            });

            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).getAccountStatement(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request to delete account by ID and verify the deletion")
    void shouldRequestDeleteAccountById() throws IOException, ServiceException, URISyntaxException {
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

    @Test
    @DisplayName("Should request to delete account with an invalid ID and receive a bad request error")
    void shouldRequestDeleteAccountByInvalidId() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(getUriBuilder()
                    .setParameter(RequestParameter.ID, "-1")
                    .build());
            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return mapped;
            });

            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            assertThat(actual.getMessage()).isNotBlank();
            Mockito.verify(service, Mockito.times(0)).deleteById(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request to delete account with an invalid ID format and receive a bad request error")
    void shouldRequestDeleteAccountByInvalidIdFormat() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(getUriBuilder()
                    .setParameter(RequestParameter.ID, "number")
                    .build());
            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return mapped;
            });

            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            assertThat(actual.getMessage()).isNotBlank();
            Mockito.verify(service, Mockito.times(0)).deleteById(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request to delete account by ID and receive a service error")
    void shouldRequestDeleteAccountByIdServiceThrowsException() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.CONFLICT)
                .message("some text")
                .build();

        Mockito.when(service.deleteById(Mockito.anyLong()))
                .thenThrow(new ServiceException(expected.getMessage(), expected.getCode()));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(getUriBuilder()
                    .setParameter(RequestParameter.ID, "1")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                return mapped;
            });

            assertThat(actual.getMessage()).matches(s -> s.contains(expected.getMessage()));
            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            Mockito.verify(service, Mockito.times(1)).deleteById(Mockito.anyLong());
        }


    }


    @Test
    @DisplayName("Should request to update account and receive the updated account")
    void shouldRequestPutAccountAndGetUpdated() throws IOException, ServiceException, URISyntaxException {
        AccountDto expected = DataGenerator.generateAccountDto();
        Mockito.when(service.update(Mockito.any())).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(getUriBuilder().build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(expected));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);
            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                AccountDto mapped = mapper.readValue(EntityUtils.toString(entity), AccountDto.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).update(Mockito.any());
        }


    }

    @Test
    void shouldRequestPutIncorrectJsonAndGetError() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(getUriBuilder().build());
            HttpEntity body = new StringEntity("{Wrong json object}");
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);
            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            assertThat(actual.getMessage()).isNotBlank();

            Mockito.verify(service, Mockito.times(0)).update(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request to update account with incorrect JSON and receive a bad request error")
    void shouldRequestPutInvalidAccountAndGetError() throws IOException, ServiceException, URISyntaxException {
        AccountDto invalidAccount = DataGenerator.generateAccountDto();
        invalidAccount.setId(-2L);
        invalidAccount.setAccount("");
        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(getUriBuilder().build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(invalidAccount));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            assertThat(actual.getMessage()).contains("id");
            assertThat(actual.getMessage()).contains("account");

            Mockito.verify(service, Mockito.times(0)).update(Mockito.any());
        }


    }

    @Test
    void shouldRequestPutAccountAndGetServiceError() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.NOT_FOUND)
                .message("text")
                .build();
        Mockito.when(service.update(Mockito.any())).thenThrow(new ServiceException(expected.getMessage(), expected.getCode()));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(getUriBuilder().build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(DataGenerator.generateAccountDto()));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            assertThat(actual.getMessage()).matches(s -> s.contains(expected.getMessage()));


            Mockito.verify(service, Mockito.times(1)).update(Mockito.any());
        }


    }


    @Test
    @DisplayName("Should request to update account and receive a service error")
    void shouldRequestPostAccountAndGetCreated() throws IOException, ServiceException, URISyntaxException {
        AccountDto expected = DataGenerator.generateAccountDto();
        Mockito.when(service.create(Mockito.any())).thenReturn(expected);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(getUriBuilder().build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(expected));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                AccountDto mapped = mapper.readValue(EntityUtils.toString(entity), AccountDto.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).create(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request to create an account with invalid data and receive a bad request error")
    void shouldRequestPostInvalidAccountAndError() throws IOException, ServiceException, URISyntaxException {
        AccountDto invalid = DataGenerator.generateAccountDto();
        invalid.setAccount(null);
        invalid.setBank(BankDto.builder().id(null).build());
        invalid.setUser(UserDto.builder().id(null).build());

        CustomError expected = CustomError.builder()
                .code(CustomError.BAD_REQUEST)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(getUriBuilder().build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(invalid));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            assertThat(actual.getMessage()).contains("account", "bank.id", "user.id");
            Mockito.verify(service, Mockito.times(0)).create(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request to create an account and receive a service error")
    void shouldRequestPostAccountAndGetServiceError() throws IOException, ServiceException, URISyntaxException {
        CustomError expected = CustomError.builder()
                .code(CustomError.NOT_FOUND)
                .message("text")
                .build();
        Mockito.when(service.create(Mockito.any())).thenThrow(new ServiceException(expected.getMessage(), expected.getCode()));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(getUriBuilder().build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(DataGenerator.generateAccountDto()));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                CustomError mapped = mapper.readValue(EntityUtils.toString(entity), CustomError.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual.getCode()).isEqualTo(expected.getCode());
            assertThat(actual.getMessage()).matches(s -> s.contains(expected.getMessage()));


            Mockito.verify(service, Mockito.times(1)).create(Mockito.any());
        }
    }
}
