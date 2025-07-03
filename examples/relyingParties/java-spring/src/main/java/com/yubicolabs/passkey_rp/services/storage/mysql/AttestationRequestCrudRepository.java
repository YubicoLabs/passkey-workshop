package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yubicolabs.passkey_rp.models.dbo.mysql.AttestationOptionsDBO;

@Repository
@ConditionalOnExpression("#{'${datasource.type}'.contains('mysql')}")
public interface AttestationRequestCrudRepository extends CrudRepository<AttestationOptionsDBO, Long> {
  List<AttestationOptionsDBO> findByRequestId(String requestID);
}
