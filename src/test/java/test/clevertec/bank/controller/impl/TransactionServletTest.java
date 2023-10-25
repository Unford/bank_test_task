package test.clevertec.bank.controller.impl;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.ServiceName;
import by.clevertec.bank.controller.ServletPath;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.controller.impl.AccountTransactionServlet;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
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
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class TransactionServletTest {
    private Tomcat tomcat;
    @Mock
    private AccountTransactionServiceImpl service;

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
        context.getServletContext().setAttribute(ServiceName.TRANSACTION_SERVICE, service);

        String servletName = "TransactionServlet";
        AccountTransactionServlet servlet = new AccountTransactionServlet();

        tomcat.addServlet(contextPath, servletName, servlet);
        context.addServletMappingDecoded(ServletPath.TRANSACTION, servletName);

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
                .setPath(ServletPath.TRANSACTION);
    }

    @Test
    @DisplayName("Should request to get a list of transactions and receive the expected list")
    void shouldRequestGetListAndGetListOfTransaction() throws IOException, ServiceException, URISyntaxException {
        List<TransactionDto> expected =
                List.of(DataGenerator.generateTransactionDto(), DataGenerator.generateTransactionDto());
        Mockito.when(service.findAll()).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder().build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity1 = response.getEntity();
                List<TransactionDto> mapped = mapper.readValue(EntityUtils.toString(entity1),
                        new TypeReference<>() {
                        });

                EntityUtils.consume(entity1);
                return mapped;
            });
            assertThat(actual).containsAll(expected);
            Mockito.verify(service, Mockito.times(1)).findAll();

        }
    }

    @Test
    @DisplayName("Should request to get a transaction by ID and receive the expected transaction")
    void shouldRequestServletAndGetTransactionById() throws IOException, ServiceException, URISyntaxException {
        TransactionDto expected = DataGenerator.generateTransactionDto();
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(expected);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter("id", "1")
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                TransactionDto mapped = mapper.readValue(EntityUtils.toString(entity), TransactionDto.class);
                EntityUtils.consume(entity);
                return mapped;
            });
            assertThat(actual).isEqualTo(expected);
            Mockito.verify(service, Mockito.times(1)).findById(Mockito.anyLong());
        }


    }

    @Test
    @DisplayName("Should request to get a list of transactions by account number and receive the expected list")
    void shouldRequestListOfTransactionsByAccount() throws IOException, ServiceException, URISyntaxException {
        List<TransactionDto> expected =
                List.of(DataGenerator.generateTransactionDto(), DataGenerator.generateTransactionDto());
        Mockito.when(service.findAllByAccount(Mockito.any())).thenReturn(expected);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter(RequestParameter.ACCOUNT, "123")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ALL_TRANSACTIONS_BY_ACCOUNT.name())
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                List<TransactionDto> mapped = mapper.readValue(EntityUtils.toString(entity),
                        new TypeReference<>() {
                        });
                EntityUtils.consume(entity);
                return mapped;
            });


            assertThat(actual).containsAll(expected);
            Mockito.verify(service, Mockito.times(1)).findAllByAccount(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request to get a list of transactions by account ID and receive the expected list")
    void shouldRequestListOfTransactionsByAccountId() throws IOException, ServiceException, URISyntaxException {
        List<TransactionDto> expected =
                List.of(DataGenerator.generateTransactionDto(), DataGenerator.generateTransactionDto());
        Mockito.when(service.findAllByAccountId(Mockito.anyLong())).thenReturn(expected);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder()
                    .setParameter(RequestParameter.ID, "123")
                    .setParameter(RequestParameter.COMMAND, CommandType.GET_ALL_TRANSACTIONS_BY_ACCOUNT_ID.name())
                    .build());

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                List<TransactionDto> mapped = mapper.readValue(EntityUtils.toString(entity),
                        new TypeReference<>() {
                        });
                EntityUtils.consume(entity);
                return mapped;
            });


            assertThat(actual).containsAll(expected);
            Mockito.verify(service, Mockito.times(1)).findAllByAccountId(Mockito.anyLong());
        }


    }


    @Test
    @DisplayName("Should request to create a deposit and receive the expected transaction")
    void shouldRequestPostDepositAndGetCreated() throws IOException, ServiceException, URISyntaxException {
        TransactionDto expected = DataGenerator.generateTransactionDto();
        Mockito.when(service.deposit(Mockito.any())).thenReturn(expected);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(getUriBuilder()
                    .setParameter(RequestParameter.COMMAND, CommandType.DEPOSIT.name())
                    .build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(expected));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                TransactionDto mapped = mapper.readValue(EntityUtils.toString(entity), TransactionDto.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).deposit(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request to withdraw funds and receive the expected transaction")
    void shouldRequestPostWithdrawalAndGetCreated() throws IOException, ServiceException, URISyntaxException {
        TransactionDto expected = DataGenerator.generateTransactionDto();
        Mockito.when(service.withdrawal(Mockito.any())).thenReturn(expected);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(getUriBuilder()
                    .setParameter(RequestParameter.COMMAND, CommandType.WITHDRAWAL.name())
                    .build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(expected));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                TransactionDto mapped = mapper.readValue(EntityUtils.toString(entity), TransactionDto.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).withdrawal(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request to transfer funds and receive the expected transaction")
    void shouldRequestPostTransferAndGetCreated() throws IOException, ServiceException, URISyntaxException {
        TransactionDto expected = DataGenerator.generateTransactionDto();
        Mockito.when(service.transferMoney(Mockito.any())).thenReturn(expected);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(getUriBuilder()
                    .setParameter(RequestParameter.COMMAND, CommandType.TRANSFER.name())
                    .build());
            HttpEntity body = new StringEntity(mapper.writeValueAsString(expected));
            request.setHeader("Content-type", "application/json");
            request.setEntity(body);

            var actual = httpClient.execute(request, response -> {
                final HttpEntity entity = response.getEntity();
                TransactionDto mapped = mapper.readValue(EntityUtils.toString(entity), TransactionDto.class);
                EntityUtils.consume(entity);
                EntityUtils.consume(body);
                return mapped;
            });


            assertThat(actual).isEqualTo(expected);

            Mockito.verify(service, Mockito.times(1)).transferMoney(Mockito.any());
        }


    }

    @Test
    @DisplayName("Should request to delete by ID and receive the expected result")
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
