package ait.cohort34.security.fiter;

import ait.cohort34.accounting.model.Role;
import ait.cohort34.security.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(20)
public class AdminRightsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

// преобразую объекты ServletRequest и ServletResponse в объекты HttpServletRequest и HttpServletResponse,
// чтобы получить доступ к различным атрибутам HTTP-запроса и HTTP-ответа
        HttpServletRequest request =(HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

//Если они корректны, и метод
// возвращает истину, то пропустим этот запрос через наш фильтр. Иначе, вернем статус 403- запрещено
// пользователь, кот. только прошел регистрацию пройдет через этот фильтр свободно, т.к. метод checkEndpoint даст ложь

        if(checkEndpoint(request.getMethod(), request.getServletPath())){
            User principal = (User) request.getUserPrincipal();

            if (!principal.getRoles().contains(Role.ADMINISTRATOR.name())){
                response.sendError(403,"You are not allowed to access this resource");
                return; //условие даст истину или ложь-> можно просто return
            }
        }
        filterChain.doFilter(request, response);
    }

// создам булеановский метод, кот. проверит путь, метод запроса и роль пользователя.
    private boolean checkEndpoint(String method, String servletPath) {
        return servletPath.matches("/account/user/\\w+/role/\\w+");
    }
}
