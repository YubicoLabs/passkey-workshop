package com.yubicolabs.bank_app.services.storage.mysql;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yubicolabs.bank_app.models.dbo.mysql.AccountTransactionDBO;

@Repository
@ConditionalOnExpression("#{'${datasource.type}'.contains('mysql')}")
public interface AccountTransactionRepositoryMysql extends CrudRepository<AccountTransactionDBO, Long> {

  List<AccountTransactionDBO> findByAccountId(int accountId);

  @Override
  List<AccountTransactionDBO> findAll();

}
