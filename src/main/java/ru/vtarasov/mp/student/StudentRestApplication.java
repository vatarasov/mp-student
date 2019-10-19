package ru.vtarasov.mp.student;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/")
@ApplicationScoped
@BasicAuthenticationMechanismDefinition(realmName = "student")
public class StudentRestApplication extends Application {
}
