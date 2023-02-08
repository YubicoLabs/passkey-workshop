package com.yubicolabs.passkey_rp.services.storage.mysql;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yubicolabs.passkey_rp.models.dbo.mysql.AssertionOptionsDBO;

@Repository
public interface AssertionRequestRepositoryMysql extends CrudRepository<AssertionOptionsDBO, Long> {

}
