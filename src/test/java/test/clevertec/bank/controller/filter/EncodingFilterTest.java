package test.clevertec.bank.controller.filter;

import by.clevertec.bank.controller.filter.EncodingFilter;
import by.clevertec.bank.exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class EncodingFilterTest {
    private Tomcat tomcat;

    @BeforeEach
    void setUp() throws LifecycleException {
        tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        Connector connector = tomcat.getConnector();
        connector.setPort(8082);

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);


        String servletName = "test";
        String urlPattern = "/*";
        FilterDef filter = new FilterDef();
        filter.setFilterName("filter1");
        filter.setFilterClass(EncodingFilter.class.getName());
        FilterMap map = new FilterMap();
        map.setFilterName("filter1");
        map.addURLPattern("/*");
        context.addFilterDef(filter);
        context.addFilterMap(map);


        tomcat.addServlet(contextPath, servletName, new HttpServlet() {

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.getWriter().write("{\"id\": 1}");
            }
        });
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
                .setPath("/");
    }

    @Test
    @DisplayName("Should include 'Content-Type' header in the response with 'UTF-8' and 'application/json' values")
    void shouldRequestAndAddContentTypeHeader() throws IOException, ServiceException, URISyntaxException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder().build());

            var actual = httpClient.execute(request, response -> {
                return response.getHeader("Content-Type").getValue();
            });

            Assertions.assertThat(actual).contains("UTF-8", "application/json");


        }


    }
}
