package ait.cohort34.security.fiter;

import ait.cohort34.accounting.model.Role;
import ait.cohort34.security.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@Order(50)
public class DeleteUserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request =(HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        if (checkEndpoint(request.getMethod(), request.getServletPath())) {
            User principal = (User) request.getUserPrincipal();
            String[] parts = request.getServletPath().split("/");
            String owner = parts[parts.length-1];
//удалить себя м. только сам пользователь или админ
            if(!(principal.getRoles().contains(Role.ADMINISTRATOR.name()) || principal.getName().equalsIgnoreCase(owner))){
                response.sendError(403, "Not Authorized");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return HttpMethod.DELETE.matches(method) && servletPath.matches("/account/user/\\w+");
    }
}
