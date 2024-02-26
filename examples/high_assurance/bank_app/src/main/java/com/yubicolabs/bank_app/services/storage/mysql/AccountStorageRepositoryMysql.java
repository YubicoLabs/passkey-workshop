package com.yubicolabs.bank_app.services.storage.mysql;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yubicolabs.bank_app.models.dbo.mysql.AccountDBO;

@Repository
@ConditionalOnExpression("#{'${datasource.type}'.contains('mysql')}")
public interface AccountStorageRepositoryMysql extends CrudRepository<AccountDBO, Long> {

  List<AccountDBO> findByUserHandle(String accountId);

  @Override
  List<AccountDBO> findAll();

}
