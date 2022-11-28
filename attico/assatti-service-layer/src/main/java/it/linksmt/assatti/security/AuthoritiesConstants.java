package it.linksmt.assatti.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";
    
    public static final String  AMMINISTRATORE_RP = "ROLE_AMMINISTRATORE_RP";

    public static final String NESSUNO = "NESSUNO";
    
    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
}
