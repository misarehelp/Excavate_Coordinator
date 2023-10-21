package ru.volganap.nikolay.haircut_schedule;

interface Constants {
    String LOG_TAG = "myLogs";
    String[] WEEKDAYS = {"Вск", "Пон", "Втр", "Срд", "Чтв", "Птн", "Сбт"};
    int PERIOD = 7;
    //****************  Themes  ***********************************************************************
    public int THEME_LIGHT_SMALL = R.style.Theme_LightGreyStyle_1;
    public int THEME_LIGHT_MEDIUM = R.style.Theme_LightGreyStyle_2;
    public int THEME_LIGHT_BIG = R.style.Theme_LightGreyStyle_3;
    public int THEME_DARK_SMALL = R.style.Theme_DarkStyle_1;
    public int THEME_DARK_MEDIUM = R.style.Theme_DarkStyle_2;
    public int THEME_DARK_BIG = R.style.Theme_DarkStyle_3;
    public int THEME_NEUTRAL_SMALL = R.style.Theme_DarkStyle_1;
    public int THEME_NEUTRAL_MEDIUM = R.style.Theme_DarkStyle_2;
    public int THEME_NEUTRAL_BIG = R.style.Theme_DarkStyle_3;

    //******************************************************************************************************
    String ROTATE = "pic_rotate";
    String ASPECT = "pic_aspect";
    String COMPRESS = "pic_compress";
    String THEME = "theme";

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
    String INDEX_FREE_RECORD = "-1";
    String INDEX_NOTE = "-2";
    String INDEX_PAST = "-3";
    String INDEX_QUESTION = "-4";
//******************************************************************************************************
    int RECORD_HOST = 0;
    int CLIENT_LIST_HOST = 2;
    int HISTORY_LIST_HOST = 3;
//******************************************************************************************************

    String SERVER_ADD_RECORD = "server_add_record";
    String SERVER_CHANGE_RECORD = "server_change_record";
    String SERVER_SHOW_RECORD = "server_show_record";
    String SERVER_DELETE_RECORD = "server_delete_record";

    String SERVER_GET_ALL = "server_get_all";
    String SERVER_GET_ARCHIVE_ALL = "server_get_archive_all";
    String SERVER_GET_ARCHIVE_BY_PHONE = "server_get_archive_by_phone";
    String SERVER_GET_BY_DATE = "server_get_by_date";
    String SERVER_DELETE_ALL = "server_delete_all";
    String SERVER_GET_NEXT_ID  = "server_get_next_id";
    String SERVER_CHANGE_CONFIG = "server_change_config";
    String SERVER_ANSWER_CONFIG_CHANGED = "The config is changed,";
//******************************************************************************************************
    String SERVER_ADD_CLIENT = "server_add_client";
    String SERVER_GET_CLIENTS = "server_get_clients";
    String GET_CLIENT_DATA_FROM_BASE = "get_client_data_from_base";
    String SHOW_CLIENT_JOB = "show_client_job";
    String SERVER_DELETE_CLIENT = "server_delete_client";
    String SERVER_CHANGE_CLIENT = "server_change_client";

    //******************************************************************************************************
    String PREF_ACTIVITY = "pref_activity";
    String RECORDS_MAX_NUMBER = "records_max_number";
    String DAYS_BEFORE_NOW = "days_before_now";
    String URL_ADDR = "https://volganap.ru/hair_cut/index_hc.php";
}