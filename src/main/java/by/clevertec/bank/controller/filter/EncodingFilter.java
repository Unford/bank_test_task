package by.clevertec.bank.controller.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/**
 * The EncodingFilter class is a Java filter that sets the character encoding and content type for incoming and outgoing
 * requests.
 */
@WebFilter(urlPatterns = {"/*"})
public class EncodingFilter implements Filter {
    private static final String ENCODING = "UTF-8";
    private static final String APPLICATION_JSON = "application/json";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON);
        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
        chain.doFilter(request, response);

    }
}
