package ait.cohort34.security.fiter;

import ait.cohort34.accounting.dao.UserAccountRepository;
import ait.cohort34.accounting.model.Role;
import ait.cohort34.accounting.model.UserAccount;
import ait.cohort34.security.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component // теперь наш класс попал в апликационный компонент
@RequiredArgsConstructor // создаем конструктор с аргументами для всех полей с аннотациями final
@Order(10) // организуем порядок выполнения компонентов в Spring(нам важны фильтры)
// Классы с меньшим значением атрибута value в аннотации @Order выполняются раньше

// делаем фильтр сами, чтобы разобраться
// выбираем implements Filter.jakarta.servlet
public class AuthenticationFilter implements Filter {

    final UserAccountRepository userAccountRepository; // нам нужен доступ к репо

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

// преобразуем объекты ServletRequest и ServletResponse в объекты HttpServletRequest и HttpServletResponse
// получаем доступ к различным атрибутам HTTP-запроса и HTTP-ответа
        HttpServletRequest request =(HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

// для наглядности распечатала:
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

//в фильтрах не надо вычитывать body
//body request- это поток ввода, не надо вычитывать поток ввода, иначе не вернешь его назад
//System.out.println(request.getInputStream());

        if (checkEndpoint(request.getMethod(), request.getServletPath())) {
            try { // обработаем возможные ошибки: неправильный логин, пароль, заголовок авторизации
                String[] credentials = getCredentials(request.getHeader("Authorization"));
                UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
                if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())){//если пароль не совпадает, то даем ошибку и прерываем фильтр, т.к. наш метод checkpw boolean
                    throw new RuntimeException();
                }

//взяли роли из enam и положили их в стринговый сет
                Set<String>roles= userAccount.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet());
                request = new WrappedRequest(request, userAccount.getLogin(),roles); //положили в наш request principal
            } catch (Exception e) {
                response.sendError(401);
                return;
            }
        }

        filterChain.doFilter(request, response);//прервали прохождение фильтра, переходим к следующему
    }

// создаем булеановский метод, кот. проверит пути и метод запроса, используя регулярное выражение
// "/forum/posts/" - строка, которая должна точно совпадать с путем запроса.
// \\w+ - шаблон, который соответствует одному или более символам слова
// (/\\w+)? - группа, соотв-ая следующему шаблону: символ / за которым следует один или более символов слова.
//            Символ ? после всей группы делает ее необязательной.
    private boolean checkEndpoint(String method, String servletPath) {
          return !(
                (HttpMethod.POST.matches(method) && servletPath.matches("/account/register"))
                || servletPath.matches(("/forum/posts/\\w+(/\\w+)?"))
                );
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
        private Set<String>roles;

//добавим конструктор
        public WrappedRequest(HttpServletRequest request, String login, Set<String>roles) {
            super(request);
            this.login = login;
            this.roles = roles;
        }

// создали метод getUserPrincipal, кот. принимает объект Principal и возвращает объект класса User,
// наследника нашего Principal (смотри в папку model)
       @Override
        public Principal getUserPrincipal(){
            return new User(login, roles);
       }
    }
}
