CREATE DATABASE passkeyStorage;

USE passkeyStorage;

DROP TABLE
    IF EXISTS hibernate_sequence,
    attestation_requests,
    assertion_requests,
    credential_registrations,
    account_transaction,
    account,
    user;

CREATE TABLE
    account (
        id BIGINT NOT NULL AUTO_INCREMENT,
        user_handle NVARCHAR(256) NOT NULL UNIQUE,
        advanced_protection BOOL DEFAULT FALSE,
        balance DECIMAL(13, 2) NOT NULL DEFAULT 0,
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (id)
    );

CREATE TABLE
    account_transaction (
        id BIGINT NOT NULL AUTO_INCREMENT,
        account_id BIGINT NOT NULL,
        type NVARCHAR(50),
        amount DECIMAL(13, 2),
        status BOOL,
        transaction_create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
        description TEXT,
        PRIMARY KEY (id),
        FOREIGN KEY (account_id) REFERENCES account(id)
    );

CREATE TABLE
    attestation_requests (
        id BIGINT NOT NULL AUTO_INCREMENT,
        attestation_request TEXT,
        is_active BOOL DEFAULT TRUE,
        request_id TEXT,
        PRIMARY KEY (id)
    );

CREATE TABLE
    assertion_requests (
        id BIGINT NOT NULL AUTO_INCREMENT,
        assertion_request TEXT,
        is_active BOOL,
        request_id TEXT,
        PRIMARY KEY (id)
    );

CREATE TABLE
    credential_registrations (
        id BIGINT NOT NULL AUTO_INCREMENT,
        credential TEXT,
        credentialid TEXT,
        credential_nickname TEXT,
        iconuri TEXT,
        last_update_time BIGINT,
        last_used_time BIGINT,
        registration_time BIGINT,
        user_handle NVARCHAR(256) NOT NULL,
        user_identity TEXT,
        state NVARCHAR(50) DEFAULT 'ENABLED',
        is_high_assurance BOOL DEFAULT FALSE,
        PRIMARY KEY (id)
    );

CREATE TABLE
    hibernate_sequence (next_val BIGINT NOT NULL);

INSERT INTO hibernate_sequence (next_val) VALUES (0);