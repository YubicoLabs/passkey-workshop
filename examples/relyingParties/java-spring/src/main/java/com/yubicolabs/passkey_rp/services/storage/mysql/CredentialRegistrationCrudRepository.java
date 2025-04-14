package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yubicolabs.passkey_rp.models.dbo.mysql.CredentialRegistrationDBO;

@Repository
@ConditionalOnExpression("#{'${datasource.type}'.contains('mysql')}")
public interface CredentialRegistrationCrudRepository extends CrudRepository<CredentialRegistrationDBO, Long> {

  List<CredentialRegistrationDBO> findByUserHandle(String stringID);

  List<CredentialRegistrationDBO> findByCredentialID(String credentialID);

  @Override
  List<CredentialRegistrationDBO> findAll();

  Long deleteByCredentialID(String credentialID);

}
