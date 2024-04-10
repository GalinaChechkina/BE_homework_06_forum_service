package ait.cohort34.accounting.service;

import ait.cohort34.accounting.dao.UserAccountRepository;
import ait.cohort34.accounting.dto.RolesDto;
import ait.cohort34.accounting.dto.UserDto;
import ait.cohort34.accounting.dto.UserEditDto;
import ait.cohort34.accounting.dto.UserRegisterDto;
import ait.cohort34.accounting.dto.exceptions.PostNotFoundException;
import ait.cohort34.accounting.model.Role;
import ait.cohort34.accounting.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

//сочетание аннотаций @Service и @RequiredArgsConstructor используется
// для создания и автоматической инициализации бинов (компонентов) сервиса,
// а также для инъекции зависимостей через конструктор

@Service//Spring сканирует приложение, обнаруживает классы, отмеченные @Service,
// создает их экземпляры и внедряет их в другие компоненты приложения,
// которые зависят от этих сервисов

@RequiredArgsConstructor//генерирует конструктор с аргументами для всех полей,
// объявленных с аннотациями @NonNull или final, устраняет необходимость
// явного написания конструктора и автоматически внедряет
// все необходимые зависимости через конструктор

public class UserAccountServiceImpl implements UserAccountService{

    final UserAccountRepository userAccountRepository;
    final ModelMapper modelMapper;

    @Override
    public UserDto register(UserRegisterDto userRegisterDto) {
        UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
        userAccount.addRole(Role.USER.toString());//у нас enam, а нужна строка
        userAccount = userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto getUser(String login) {
        UserAccount userAccount = userAccountRepository.getUserAccountByLoginExistsOrLoginNull(login).orElseThrow(PostNotFoundException::new);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto removeUser(String login) {
        UserAccount userAccount = userAccountRepository.getUserAccountByLoginExistsOrLoginNull(login).orElseThrow(PostNotFoundException::new);
        userAccountRepository.delete(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto updateUser(String login, UserEditDto userEditDto) {
        UserAccount userAccount = userAccountRepository.getUserAccountByLoginExistsOrLoginNull(login).orElseThrow(PostNotFoundException::new);
        String newFirstName = userEditDto.getFirstName();
        if(newFirstName !=null){
            userAccount.setFirstName(newFirstName);
        }
        String newLastName = userEditDto.getLastName();
        if(newLastName !=null){
            userAccount.setLastName(newLastName);
        }
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
        UserAccount userAccount = userAccountRepository.getUserAccountByLoginExistsOrLoginNull(login).orElseThrow(PostNotFoundException::new);
//если роль непустая строка и получили запрос удалить
        if(role != null && isAddRole) {
            userAccount.addRole(role);
                    }
        if(role != null && !isAddRole) {
            userAccount.removeRole(role);
        }
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, RolesDto.class);
    }

    @Override
    public void changePassword(String login, String newPassword) {

    }
}
