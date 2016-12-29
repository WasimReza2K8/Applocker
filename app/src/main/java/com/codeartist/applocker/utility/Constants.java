
package com.codeartist.applocker.utility;

public class Constants {
    public static final String LICENSE_KEY = "3IMDPFN-7c5962ca-a0cc-4bd9-afbb-eb2341e2fd5c";
    public static final String ACTION_ID = "ID";
    public static final String ACTION_TITLE_KEY = "Title";
    public static final String SCAN_STATUS_IN_PROGRESS = "Scan In Progress";
    public static final String SCAN_STATUS_FINISHED = "Scan completed";
    public static final String HELP_URL = "http://rd.snxt.jp/56759";
    public static final String ABOUT_PRODUCT_URL = "http://rd.snxt.jp/13236";
    public static final String DATE_FORMAT_WITH_TIME = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_FORMAT_WITHOUT_TIME = "MMM dd, yyyy";
    public static final String LICENSE_ENTRY_URL = "http://www2.sourcenext.com/snrmcapi/ca/activation";
    public static final String LICENSE_EXTEND_URL = "http://test.www2.sourcenext.com/snrmeapi/ea/getlicenseinfo";
    public static final int SERIAL_KEY_VALIDITY_TIME = 30;
    public static final String APPLICATION_CODE = "{39D73E3F-F1FB-457a-87BA-9248D84C773C}";
    public static final String EMAIL_REGISTRATION_URL = "http://test.www2.sourcenext.com/snrmcapi/ca/activation";

    public static final int FAILED_NO_INTERNET = 0;
    public static final int FAILED_INTERRUPT_INTERNET = 1;
    public static final int FAILED_VIRUS_FOUND = 2;
    public static final int FAILED_USER_STOP = 3;
    public static final int FAILED_NO_SCAN_PERFORMED = 4;

    public static final int AUTO_INSTALLATION_SCAN = 0;
    public static final int MANUAL_SCAN = 1;
    public static final int EXTERNAL_STORAGE_SCAN = 2;
    public static final String FAILED_NO_INSTANCE_FOUND = "No activity instance found";
    public static final String FAILED_NO_INTERNET_TEXT = "No internet connection";

    public static final String KEY_SCAN_TIME = "scan_time";
    public static final String KEY_SCAN_TYPE = "scan_type";
    public static final String KEY_RESULT_LIST = "result list";
    public static final String KEY_PKG_NAME = "pkg Name";
    public static final String KEY_CLOSE_DIALOG = "close_dialog";
    public static final String KEY_LOCKER_ACCURACY = "locker_accuracy";
    public static final String KEY_LOCKER_TYPE = "locker_type";
    public static final String KEY_ERROR_MSG = "error msg";
    public static final String STB_STATUS_RECEIVER_INTENT_FILTER = "stb_status_receiver_intent_filter";

    public static final String INTENT_ON_PKG_UNINSTALL = "android.intent.action.PACKAGE_REMOVED";
    public static final String INTENT_ON_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
    public static final String INTENT_ON_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED";
    public static final String INTENT_ON_MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL";
    public static final String INTENT_ON_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
    public static final int LAST_SCAN_VIRUS_COUNT_DEFAULT_VAL = -1;
    public static final String RESPONSE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String DEFAULT_LICENSE_KEY = "-1";
}
