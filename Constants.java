package ru.volganap.nikolay.haircut_schedule;

interface Constants {
    String LOG_TAG = "myLogs";
    String START_HOLIDAY_TIME = "01:00";
    String[] WEEKDAYS = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
    int PERIOD = 7;

    //****************  Type of Activity  ***********************************************************************
    String RECORD_ACTIVITY = "record_activity";
    String GENERAL_SETTINGS = "general_settings";
    String CALENDAR_SETTINGS = "calendar_settings";
    //******************************************************************************************************
    String CHILD_ACTIVITY = "child_activity";

    //****************  Themes  ***********************************************************************
    int THEME_LIGHT_SMALL = R.style.Theme_LightGreyStyle_1;
    int THEME_LIGHT_MEDIUM = R.style.Theme_LightGreyStyle_2;
    int THEME_LIGHT_BIG = R.style.Theme_LightGreyStyle_3;
    int THEME_DARK_SMALL = R.style.Theme_DarkStyle_1;
    int THEME_DARK_MEDIUM = R.style.Theme_DarkStyle_2;
    int THEME_DARK_BIG = R.style.Theme_DarkStyle_3;

    //******************************************************************************************************
    String ROTATE = "pic_rotate";
    String ASPECT = "pic_aspect";
    String COMPRESS = "pic_compress";
    String THEME = "theme";
    String CALENDAR_BACKGROUND_WORKDAY = "calendar_bckgd_workday";
    String CALENDAR_TEXT_WORKDAY = "calendar_text_workday";
    String CALENDAR_BACKGROUND_HOLIDAY = "calendar_bckgd_holiday";
    String CALENDAR_TEXT_HOLIDAY = "calendar_text_holiday";
    String CALENDAR_BACKGROUND_TODAY = "calendar_bckgd_today";
    String CALENDAR_TEXT_TODAY = "calendar_text_today";
    String CALENDAR_BACKGROUND_SELECT_DAY = "calendar_bckgd_select_day";
    String CALENDAR_TEXT_SELECT_DAY = "calendar_text_select_day";

//******************************************************************************************************
    String COMMAND = "command";
    String SENDER = "sender";
    String MESSAGE = "message";
    String FILE_STORAGE = "Haircut_pics";

//******************************************************************************************************
    String DATA_IS_READY = "Данные получены. ";
    String DATA_IS_NOT_READY = "Данные не получены. ";
    String DATA_REQUEST_PROCESSING = " Идет обработка запроса:...";
    String DATA_WAS_NOT_CHANGED = "Данные не изменились";
    String DATA_WAS_SAVED = "Данные сохранены";
    String DATA_WAS_NOT_SAVED = "Данные не сохранены";
    String DATA_WAS_DELETED = "Данные удалены";

//******************************************************************************************************
    String URL_WAS_NOT_FOUND = "The requested URL was not found on this server";
    String NET_ERROR_STATE = "Произошла ошибка подключения к серверу";
//******************************************************************************************************
    String DATE_CODE = "date_code";
    String TIME_CODE = "time_code";
    String INDEX_CODE = "index_code";
    String TYPE_CODE = "type_code";

    //******* Type of Records *******************************************************
    String INDEX_FREE_RECORD = "-1";
    String INDEX_NOTE = "-2";
    String INDEX_SET_ON_HOLIDAY = "-3";
    String INDEX_SET_OFF_HOLIDAY = "-4";

//******************************************************************************************************
    int BIT_NOTE = 0;
    int BIT_HAS_PIC = 1;
    int BIT_REMIND_SENT = 2;
    int BIT_QUESTION = 3;
    int BIT_HOLIDAY = 4;
//******************************************************************************************************
    int NOT_IN_CLIENT_BASE = -1;
//******************************************************************************************************
    int RECORD_HOST = 0;
    int CLIENT_LIST_HOST = 2;
    int HISTORY_LIST_HOST = 3;

//******* Record Commands sent to Server *******************************************************
    String SERVER_ADD_RECORD = "server_add_record";
    String SERVER_CHANGE_RECORD = "server_change_record";
    String SERVER_SHOW_RECORD = "server_show_record";
    String SERVER_DELETE_RECORD = "server_delete_record";
    String SERVER_MARK_HOLIDAY = "server_mark_holiday";
    String SERVER_UNMARK_HOLIDAY = "server_unmark_holiday";
    String SERVER_GET_ALL = "server_get_all";
    String SERVER_GET_ARCHIVE_BY_ID = "server_get_archive_by_id";
    String SERVER_DELETE_ARCHIVE = "server_delete_archive";
    String SERVER_CHANGE_CONFIG = "server_change_config";
    String SERVER_DELETE_ALL = "server_delete_all";

    //******** Client Commands sent to Server *********************************************************
    String SERVER_GET_CLIENTS = "server_get_clients";
    String SERVER_ADD_CLIENT = "server_add_client";
    String SERVER_DELETE_CLIENT = "server_delete_client";
    String SERVER_CHANGE_CLIENT = "server_change_client";
    String SERVER_GET_CLIENT_ID = "server_get_client_id";
    String GET_CLIENT_DATA_FROM_BASE = "get_client_data_from_base";
    String SHOW_CLIENT_JOB = "show_client_job";

    //******* Answer got from Server *******************************************************
    String SERVER_WAIT_FOR_ANSWER = "server_wait_for_answer";
    String SERVER_ANSWER_CONFIG_CHANGED = "The config is changed,";

    //******************************************************************************************************
    String PREF_ACTIVITY = "pref_activity";
    String RECORDS_MAX_NUMBER = "records_max_number";
    String DAYS_BEFORE_NOW = "days_before_now";
    String URL_ADDR = "https://volganap.ru/hair_cut/index_hc.php";
}
