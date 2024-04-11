package ait.cohort34.accounting.controller;

import ait.cohort34.accounting.dto.RolesDto;
import ait.cohort34.accounting.dto.UserDto;
import ait.cohort34.accounting.dto.UserEditDto;
import ait.cohort34.accounting.dto.UserRegisterDto;
import ait.cohort34.accounting.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController//каждый метод этого класса по умолчанию возвращает объекты,
// которые будут автоматически преобразованы в формат JSON и
// отправлены обратно клиенту в теле HTTP-ответа

@RequestMapping("/account")//позволяет определить URL-адреса и
// методы HTTP-запросов, которые должен обрабатывать данный метод или класс

@RequiredArgsConstructor//позволяет избежать явного написания конструктора
// инициализации для всех final полей

public class UserAccountController{
    final UserAccountService userAccountService;

    @PostMapping("/register")
    public UserDto register(@RequestBody UserRegisterDto userRegisterDto) {
        return userAccountService.register(userRegisterDto);
    }

    @PostMapping("/login")
    public UserDto login(Principal principal) {
//надо достать данные из заголовка, сделать проверки и вернуть найденного по логину пользователя
//Principal principal - объект, кот. д. появиться после аутентификации
//TODO method
        return userAccountService.getUser(principal.getName());
    }

    @GetMapping("/user/{login}")
    public UserDto getUser(@PathVariable String login) {
        return userAccountService.getUser(login);
    }

    @DeleteMapping("/user/{login}")
    public UserDto removeUser(@PathVariable String login) {
        return userAccountService.removeUser(login);
    }

    @PutMapping("/user/{login}")
    public UserDto updateUser(@PathVariable String login, @RequestBody UserEditDto userEditDto) {
        return userAccountService.updateUser(login, userEditDto);
    }

    @PutMapping("/user/{login}/role/{role}")
    public RolesDto addRole(@PathVariable String login, @PathVariable String role) {
        return userAccountService.changeRolesList(login, role, true);
    }

    @DeleteMapping("/user/{login}/role/{role}")
    public RolesDto deleteRole(@PathVariable String login, @PathVariable String role) {
        return userAccountService.changeRolesList(login, role, false);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//при смене пароля н. передавать заголовок "X-Password"
    public void changePassword(Principal principal, @RequestHeader("X-Password") String newPassword) {
      userAccountService.changePassword(principal.getName(), newPassword);
    }
}
