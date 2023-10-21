package test.clevertec.bank.controller.listener;

import by.clevertec.bank.config.AppConfiguration;
import by.clevertec.bank.controller.listener.ServletContextListenerImpl;
import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.exception.ServiceException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ServletContextListenerTest {
    private Tomcat tomcat;

    private final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("dd-MM-yyyy hh:mm"))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Spy
    private ServletContextListenerImpl listener;

    @BeforeEach
    void setUp() throws LifecycleException {
        tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        Connector connector = tomcat.getConnector();
        connector.setPort(8082);

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);
        ((StandardContext)context).addServletContainerInitializer(new ServletContainerInitializer() {
            @Override
            public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
                ctx.addListener(listener);
            }
        }, null);

        String servletName = "test";
        String urlPattern = "/*";


        tomcat.addServlet(contextPath, servletName, new HttpServlet() {

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.getWriter().write(mapper.writeValueAsString(AppConfiguration.getInstance()));
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
    void shouldRequestAndGetAppConfig() throws IOException, ServiceException, URISyntaxException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(getUriBuilder().build());

            var actual = httpClient.execute(request, response -> {
                {
                    final HttpEntity entity = response.getEntity();
                    AppConfiguration res = mapper.readValue(EntityUtils.toString(entity), AppConfiguration.class);
                    EntityUtils.consume(entity);
                    return res;
                }
            });
            AppConfiguration.DatabaseConfig databaseConfig =
                    new AppConfiguration.DatabaseConfig("bank", "5237",
                            "localhost", "root", "root",
                            "org.postgresql.ds.PGSimpleDataSource");

            AppConfiguration.BusinessConfig businessConfig = new AppConfiguration.BusinessConfig(5);

            Assertions.assertThat(actual.getDatabase()).isEqualTo(databaseConfig);
            Assertions.assertThat(actual.getBusiness()).isEqualTo(businessConfig);
            Mockito.verify(listener, Mockito.times(1)).contextInitialized(Mockito.any());

        }

    }

    @Test
    void shouldDestroyContext() {
        ServletContextListenerImpl servletContextListener = Mockito.spy(new ServletContextListenerImpl());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {

            pool.when(ConnectionPool::close).thenCallRealMethod();
            servletContextListener.contextDestroyed(null);

            pool.verify(ConnectionPool::close, Mockito.times(1));
            Assertions.assertThat(servletContextListener.getScheduledExecutorService()).isNull();
        }

    }
}
