package ait.cohort34.accounting.dao;

import ait.cohort34.accounting.dto.UserDto;
import ait.cohort34.accounting.model.UserAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAccountRepository extends CrudRepository<UserAccount,String> {
    Optional<UserAccount> getUserAccountByLoginExistsOrLoginNull(String login);



}
