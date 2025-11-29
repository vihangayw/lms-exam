package lk.mc.core.enums;

/**
 * AppUserRole contains user roles used within application (mostly used in jwt token)
 *
 * @author vihangawicks
 * @since 11/10/21
 * MC-lms
 */
public enum AppUserRole {
    ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_CLIENT, ROLE_DEVICE;

    public String getAuthority() {
        return name();
    }

}
