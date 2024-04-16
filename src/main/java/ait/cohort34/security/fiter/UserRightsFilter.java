package ait.cohort34.security.fiter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(30)
public class UserRightsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request =(HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        if (checkEndpoint(request.getMethod(), request.getServletPath())) {
            String[] pathParts = request.getServletPath().split("/");

            if (pathParts.length == 4 || pathParts.length == 6) {
                String author = pathParts[pathParts.length - 1];//последний эл-т массива содержит автора
                String currentLogin = request.getUserPrincipal().getName();
                if (!currentLogin.equalsIgnoreCase(author)) {
                    response.sendError(403, "You are not allowed to access this resource");
                    return; //становили работу фильтра
                }
            }
        }

        filterChain.doFilter(request, response);
    }
    private boolean checkEndpoint(String method, String servletPath) {
        return (HttpMethod.POST.matches(method) || HttpMethod.PUT.matches(method)) && servletPath.matches("/forum/post/\\w+(/\\w+/\\w+)?");
    }
}
