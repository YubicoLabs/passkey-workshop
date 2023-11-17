package com.yubicolabs.passkey_rp.interfaces;

import java.util.Collection;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.data.ByteArray;
import com.yubicolabs.passkey_rp.models.common.CredentialRegistration;
import com.yubicolabs.passkey_rp.models.common.CredentialRegistration.StateEnum;;

public interface CredentialStorage extends CredentialRepository {
  /**
   * Adds a new credential for a user into the Credential Repository
   * 
   * @param registration credential to be stored for the used
   * @return true indicates the credential was successfully created, false
   *         otherwise
   */
  public Boolean addRegistration(CredentialRegistration registration);

  /**
   * Gets all registrations that belong to a username
   * 
   * @param username denotes the user whose credential will be returned
   * @return collection of registrations belonging to a defined username
   */
  public Collection<CredentialRegistration> getRegistrationsByUsername(String username);

  /**
   * Gets all registrations that belong to a userHandle
   * 
   * @param userHandle denotes the user whose credentials will be returned
   * @return collection of registrations belonging to a defined user handle
   */
  public Collection<CredentialRegistration> getRegistrationsByUserHandle(ByteArray userHandle);

  /**
   * Gets all credentials associated to a credential ID
   * 
   * @param credentialId denotes the credentialId to search for
   * @return collection of all credentials with the credential ID
   */
  public Collection<CredentialRegistration> getByCredentialId(ByteArray credentialId);

  /**
   * Determines if a user has a credential in the credential repository
   * 
   * @param username denotes the user to be searched for
   * @return true if the user has a credential, false otherwise
   */
  public Boolean userExists(String username);

  /**
   * Removes a credential from the credential repository
   * 
   * @param credentialId ID of the credential to be removed
   * @param userHandle   the user handle of the user attempting to make the
   *                     request
   * @return true if the credential was successfully removed, false otherwise
   */
  public Boolean removeRegistration(ByteArray credentialId, ByteArray userHandle);

  /**
   * @TODO - readd this method
   *       Updates the nickname of a specific credential
   * 
   * @param nickname     new nickname to be given to a credential
   * @param credentialId ID of the credential to be renamed
   * @param userHandle   user handle of the user attempting to make the request
   * @return true if the credential was successfully renamed, false otherwise
   */
  public Boolean updateCredentialNickname(ByteArray credentialId, String newNickname);

  // TODO = Add method for getAll()

  /**
   * Updates the state of a credential; Meant to allow for the reactivation of
   * disabled credentials to support advanced protection
   * 
   * Allows for the state to be set to deleted, which is similar to the
   * functionality in removeRegistration
   * 
   * Note, the method will not allow for a credential with the DELETED state to be
   * re-enabled, or deactivated
   * 
   * @param credentialId ID of the credential to be updated
   * @param userHandle   User handle of the user attempting to make the request
   * @param newState     New state of the credential
   * @return true if the operation was successful, false otherwise
   */
  public Boolean updateCredentialStatus(ByteArray credentialId, ByteArray userHandle, StateEnum newState);
}
