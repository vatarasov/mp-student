package ru.vtarasov.mp.student;

import java.util.Arrays;
import java.util.HashSet;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

/**
 * @author vtarasov
 * @since 20.10.2019
 */
@ApplicationScoped
public class StudentIdentityStore implements IdentityStore {
    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
            if (usernamePasswordCredential.compareTo("user", "user")) {
                return new CredentialValidationResult("user", new HashSet<>(Arrays.asList("default")));
            }
        }
        return CredentialValidationResult.INVALID_RESULT;
    }
}
