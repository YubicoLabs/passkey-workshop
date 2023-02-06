CREATE DATABASE passkeyStorage;

USE passkeyStorage;

CREATE TABLE credential_registrations (
  requestId text,
  credential text
);

CREATE TABLE assertion_requests (
  requestId text,
  request text
);

CREATE TABLE attestation_requests (
  requestId text,
  request text
);

SHOW TABLES;