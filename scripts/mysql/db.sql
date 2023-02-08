CREATE DATABASE passkeyStorage;

USE passkeyStorage;

CREATE TABLE
    credential_registrations (
        userHandle text,
        credential text
    );

CREATE TABLE assertion_requests ( id BIGINT );

CREATE TABLE attestation_requests ( id BIGINT);

SHOW TABLES;