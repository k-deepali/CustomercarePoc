package com.agsft.customer.Care.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;



@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum HttpSuccessCodes {

    OK(200, "ok"),
    FILE_UPLOADED_SUCCESS(200,"upload.success"),
    BILL_SUCCESS(200,"bill.success"),
    BILL_DETAIL(200,"bill.detail"),
    ROLE_SUCCESS(200,"role.success"),
    BATCH_FILE_PROCESSING(200,"batch.file.process"),
    LIST_FETCHED(200,"list.fetched"),
    TOKEN_SUCCESS(200,"token.success"),
    COMPANY_SUCCESS(200,"company.success"),
    LOGIN_SUCCESS(200,"login.success"),
    LOGOUT_SUCCESS(200,"logout.success"),
    FILE_PARSE_SUCCESS(200,"file.parse.success"),
    FILE_DOWNLOADED_SUCCESS(200,"file.downloaded.success"),
    USER_SUCCESS(200,"user.success");



    Integer value;
    String reasonPhrase;

    public static HttpSuccessCodes value(int code) {
        return HttpSuccessCodes.value(code);
    }
}
