package ru.volganap.nikolay.haircut_schedule;

interface KM_Constants  {
    String LOG_TAG = "myLogs";
    String[] FROM = {"0", "1", "2", "3", "4"};
    String[] WEEKDAYS = {"Вск", "Пон", "Втр", "Срд", "Чтв", "Птн", "Сбт"};
    int PERIOD = 7;

    String MODE_USER = "mode_user";
    String DEPARTMENT_USER = "dep_user";
    String SENDER = "sender";
    String MESSAGE = "message";

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
    String EMPTY_STORAGE_STATE = "Нет данных на сервере";
    String SERVER_BASE_HAS_BEEN_RELEASED_BY = "База данных сервера освобождена пользователем: ";
//******************************************************************************************************
    String ADD_CODE = "add_code";
    String DELETE_CODE = "delete_code";
    String CHANGE_CODE = "change_code";
//******************************************************************************************************

    String SERVER_ADD_RECORD = "server_add_record";
    String SERVER_CHANGE_RECORD = "server_change_record";
    String SERVER_DELETE_RECORD = "server_delete_record";

    String SERVER_GET_ALL = "server_get_all";
    String SERVER_GET_ARCHIVE = "server_get_archive";
    String SERVER_GET_BY_DATE = "server_get_by_date";
    String SERVER_DELETE_ALL = "server_delete_all";
    String SERVER_GET_NEXT_ID  = "server_get_next_id";
    String SERVER_CLEAR_START_ID = "server_clear_start_id";

    String SERVER_CHANGE_CONFIG = "server_change_config";
    String SERVER_ANSWER_CONFIG = "config";
//******************************************************************************************************
    String PREF_ACTIVITY = "pref_activity";
    String RECORDS_MAX_NUMBER = "records_max_number";
    String ADMIN_PASS = "admin_pass";
    //String URL_ADDR = "https://volganap.ru/excavate_coordinator/index_ec.php";
    String URL_ADDR = "https://volganap.ru/hair_cut/index_hc.php";
    String CONFIRM_CONNECTION = "Обратная связь получена!: ";
}
