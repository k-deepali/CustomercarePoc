package com.agsft.customer.Care.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author Pranjal
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public enum HttpStatusCodes {

    OK(200, "OK"), NO_CONTENT(204, "No Content"), BAD_REQUEST(400, "Bad Request"), UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"), NOT_FOUND(404, "Request object not found"), INTERNAL_SERVER_ERROR(500, "Internal Server Error"), LOGIN_SUCCESSFUL(200, "Logged in successfully"),LOGOUT_SUCCESSFUL(200, "Logout successfully"),USER_LIST_FETCH_SUCCESSFULLY(200, "Users list fetch successfully");

    int value;
    String reasonPhrase;

    public static HttpStatusCodes value(int code) {
        return HttpStatusCodes.value(code);
    }
}
