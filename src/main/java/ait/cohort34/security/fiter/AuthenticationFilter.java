package ait.cohort34.security.fiter;

import ait.cohort34.accounting.dao.UserAccountRepository;
import ait.cohort34.accounting.dto.exceptions.UserNotFoundException;
import ait.cohort34.accounting.model.UserAccount;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
//делаем фильтр сами, чтобы разобраться
// выбираем implements Filter.jakarta.servlet

@Component//теперь наш класс понятен Spring
@RequiredArgsConstructor
@Order(10)//другие фильтры б. идти раньше или позже, чем этот- зависит от номера
public class AuthenticationFilter implements Filter {

    final UserAccountRepository userAccountRepository;//нам нужен доступ к репо

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =(HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

//обработаем возможные ошибки: неправильный логин, пароль, заголовок авторизации

        if (checkEndpoint(request.getMethod(), request.getServletPath())) {
            try {
                String[] credentials = getCredentials(request.getHeader("Authorization"));
                UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
                if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())){//если пароль не совпадает, то даем ошибку и прерываем фильтр, т.к. наш метод checkpw boolean
                    throw new RuntimeException();
                }
                request = new WrappedRequest(request, userAccount.getLogin()); //положили в наш request principal
            } catch (Exception e) {
                response.setStatus(401);
                return;
            }
        }
        filterChain.doFilter(request, response);//прервали дальнейшее прохождение сквозь фильтры
    }

    private boolean checkEndpoint(String method, String servletPath) {
        //TODO HOMEWORK условия, кот. пропускаются без аутентификации: регистрация и все posts
        if(method.equals("GET") && servletPath.startsWith("/posts")) {
            return true;
        }
        if(method.equals("POST") && servletPath.equals("/register")) {
            return true;
        }
        return false;
    }
//позволяет проверить, что пользователь прошел проверку на аутентификацию
    private String[] getCredentials(String authorization) {
        String token = authorization.split(" ")[1];//сначала отбросили basic: рассплитили и взяли только буквы шифрования
        String decode = new String(Base64.getDecoder().decode(token));//переделали в строку, т.к. метод decode вернул byte
        return decode.split(":");//чтобы получить логин и пароль отдельно; у нас ключ:значение, получим массив из двух эл-ов
    }

//хочу передавать по фильтру свой класс class WrappedRequest с principal
    private class WrappedRequest extends HttpServletRequestWrapper{
        private String login;
        //добавим конструктор
        public WrappedRequest(HttpServletRequest request, String login) {
            super(request);
            this.login = login;
        }
       @Override
        public Principal getUserPrincipal(){
            return ()->login;//создали principal при помощи стрелочной ф-ии
       }
    }
}
