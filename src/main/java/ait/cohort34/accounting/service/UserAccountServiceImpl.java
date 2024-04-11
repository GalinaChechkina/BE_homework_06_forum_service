package ait.cohort34.accounting.service;

import ait.cohort34.accounting.dao.UserAccountRepository;
import ait.cohort34.accounting.dto.RolesDto;
import ait.cohort34.accounting.dto.UserDto;
import ait.cohort34.accounting.dto.UserEditDto;
import ait.cohort34.accounting.dto.UserRegisterDto;
import ait.cohort34.accounting.dto.exceptions.IncorrectRoleException;
import ait.cohort34.accounting.dto.exceptions.UserNotFoundException;
import ait.cohort34.accounting.dto.exceptions.UserExistsException;
import ait.cohort34.accounting.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
        if (userAccountRepository.existsById(userRegisterDto.getLogin())) {
            throw new UserExistsException();//ошибка, если юзер с этим логином уже сущ-ет
        }

        UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
        // роль добавлять не нужно, modelMapper создаст юзера по дефолтному конструктору
        // и уже добавит роль юзер
        String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
        userAccount.setPassword(password);
        userAccount = userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto getUser(String login) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto removeUser(String login) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
        userAccountRepository.delete(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto updateUser(String login, UserEditDto userEditDto) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
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
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
//если роль непустая строка и получили запрос удалить
        role = role.toUpperCase();
        boolean res;
        try {
            if (isAddRole) {
                res = userAccount.addRole(role);
            } else {
                res = userAccount.removeRole(role);
            }
        } catch (Exception e){
            throw new IncorrectRoleException();
        }
        if (res) {
            userAccountRepository.save(userAccount);
        }
        return modelMapper.map(userAccount, RolesDto.class);
    }
//пароль в бд хранится в хэшированном виде
    @Override
    public void changePassword(String login, String newPassword) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);

        String password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userAccount.setPassword(password);

        userAccountRepository.save(userAccount);

    }
}
