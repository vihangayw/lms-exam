package lk.mc.core.constants;

/**
 * This class contains Application related constants
 *
 * @author vihanga
 * @since 27/10/2021
 * MC-lms
 */
public class ApplicationConstants {

    //PreFIx for the ejb
    public static final String BO_JNDI_PREFIX = "java:app/ts_bo/";
    public static final String PERSISTENCE_UNIT_NAME = "lms_unit";
    //default record count for a page
    public static final int RECORDS_PER_PAGE = 1000;
    //Default output param name of a Stored Procedure
    public static final String DEFAULT_SP_OUT_PARA_NAME = "pkey";
    public static final String DEFAULT_SP_OUT_PARA_CODE = "out_code";
    public static final String DEFAULT_SP_OUT_PARA_MSG = "out_message";

    public static final int SP_OUT_SUCCESS_PARA_CODE = 1;
    public static final int SP_OUT_EXCEPTION_PARA_CODE = -99;

    public static final int ERROR_JPA_SP_EXECUTE = -1;
    public static final int DEFAULT_SUCCESS_RESPONSE_FOR_SP_EXECUTE = 1;
    public static final String DEFAULT_SUCCESS_CODE_FOR_ADD_EDIT = "1|Success";
    public static final String DEFAULT_FAILED_CODE_FOR_ADD_EDIT = "-1|Failed";
    public static final String DEFAULT_SUCCESS_RESPONSE_FOR_JPA_EXECUTE = "1";
    //Use this com.ts.user id for execute unit tests
    public static final int DEFAULT_TEST_USER_EMPLOYEE_ID = 1;
    public static final String DEFAULT_DATE_FORMAT = "YYYY-MM-DD"; //eg: "2010-10-2"
    public static final int FIRST_INDEX = 0;

    //security
    public static final String AUTH_HEADER_KEY = "X-AUTH-TOKEN";
    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    public static final int PW_CHAR_LIMIT = 8;
    public static final String JWT_COMMON = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJHRU4iLCJuYW1lIjoiTE1TLU1" +
            "DIiwiaWF0IjoxNjcxNzc5NDY4LCJhdXRob3IiOiJ2aWhhbmdhd2lja3MiLCJleHAiOjI1MDAwMDAwMDB9.EFuKrCNC_6HfUIVB65g7W0" +
            "j0eKPkmG-n_8kZhkeOxzU";
    //jwt keys
    public static final String JWT_CUSTOMER_ID = "cus";
    public static final String JWT_BRANCH_ID = "bra";
    public static final String JWT_USER_ID = "user";
    public static final String JWT_SP_ID = "spid";
    public static final String JWT_SP_REG = "reg";
    public static final String JWT_BRANCH_COUNT = "nfb";//no of branches
    public static final String JWT_PW = "EDv+UY+Yp1Ccp533SPtFew==";

    public static final long JWT_CUSTOMER_EXP = 20 * 365 * 24 * 60 * 60 * 1000L; // 20 years -> y * 365 * h * min * sec * millis

    //locale
    public static final String LOCALE_HEADER = "Accept-Language";

    //http
    public static final String RESPONSE_OK = "success";
    public static final String RESPONSE_FAIL = "fail";


    private ApplicationConstants() {
        // added a private constructor to hide the implicit public one.
    }

}
