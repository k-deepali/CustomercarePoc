package com.agsft.customer.Care.enums;


public enum HttpErrorCode {


    SOMETHING_WENT_WRONG(500, "something.went.wrong"),
    UNAUTHORIZED(401, "unauthorized.access"),
    NO_CONTENT(204, "no.content"),
    BAD_REQUEST(400, "bad.request"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "request.object.not.found"),
    USER_NOT_FOUND(404, "user.not.found"),
    ROLE_NOT_ASSIGN(400,"role.not.assign"),
    FILE_NOT_UPLOAD(404, "file.not.upload"),
    FILE_COMPLETE(404,"file.completed"),
    INVALID_MAIL(404, "invalid.mail"),
    FILE_ALREADY_PRESENT(403, "file.already.exits"),
    FILE_INCORRECT(415,"file.incorrect" ),
    PATH_NOT_EXIIST(404,"path.not.exist"),
    INCORRECT_CSV(404,"incorrect.csv"),
    ACCESS_DENID(400,"access.denied"),
    FILE_PARSE_FAILED(400,"file.failed.parse"),
    COMPANY_NOT_FOUND(404, "company.not.found"),
    PASSWORD_NOT_MATCH(403,"password.not.match"),
    INTERNAL_SERVER_ERROR(500, "internal.server.error"),
    EMAIL_ALREADY_REGISTERED(404,"email.failed"),
    PHONE_NO_ALREADY_REGISTERED(404,"phone.failed"),
    NAME_ALREADY_REGISTERED(404,"name.failed"),
    LOGIN_FAILED(401,"login.failed") ,
    SORT_BY(404,"sort.by"),
    FILE_EMPTY(204,"file.list.empty"),
    TOKEN_EXPIRED(401,"token.expired"),
    CONFLICT(409, "conflict"),
    FILE_NOT_FOUND(404,"file.not.found" );
    private String message;

    private Integer code;

    HttpErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
