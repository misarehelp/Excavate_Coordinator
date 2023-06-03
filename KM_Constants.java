package ru.volganap.nikolay.excavate_coordinator;

interface KM_Constants  {
    String LOG_TAG = "myLogs";
    String[] FROM = {"0", "1", "2", "3", "4"};

    String DEPARTMENT_USER = "department_user";
    String MODE_USER = "mode_user";

    String SENDER = "sender";
    String MESSAGE = "message";

    String DATA_IS_READY = "Данные получены. ";
    String DATA_IS_NOT_READY = "Данные не получены. ";
    String DATA_REQUEST_PROCESSING = " Идет обработка запроса:...";
    String DATA_WAS_NOT_CHANGED = "Данные не изменились";
    String DATA_WAS_DELETED = "Текущий наряд был удален";
    String URL_WAS_NOT_FOUND = "The requested URL was not found on this server";
    String NET_ERROR_STATE = "Произошла ошибка подключения к серверу";
    String EMPTY_STORAGE_STATE = "Нет данных на сервере";
    String FILLED_PERMIT_CODE = "the fields of a new permit are filled before communications are put";
    String DATA_WAS_SAVED = "Данные сохранены";
    String SERVER_BASE_HAS_BEEN_RELEASED_BY = "База данных сервера освобождена пользователем: ";

    String NEW_PERMIT_CODE = "new_permit_code";
    String EDIT_MASTER_PERMIT_CODE = "Режим редактирования";
    String SHOW_PERMIT_CODE = "Режим просмотра";
    String CHANGE_PERMIT_CODE = "change_permit_code";

    String START_LINE_CODE = "Тапом на карте отметьте  стартовую точку начала новой линии";
    String ADD_LINE_CODE = "Укажите тапом на карте следующую точку";
    String ADD_LINE_OR_FINISH_CODE = "Укажите тапом  на карте следующую точку или нажмите *Закончить*";
    String END_LINE_CODE = "Тапом на карте начните новую группу линий или завершите построение линий";
    String CONTOUR_LINE_CODE = "Для обозначения контура необходимо установить не менее 3 точек";
    String CLEAR_LINE_CODE = "Последняя введеная линия (группа линий) удалена";

    String SERVER_GET_ALL = "server_get_all";
    String SERVER_GET_ARCHIVE = "server_get_archive";
    String SERVER_GET_BY_DEP = "server_get_by_dep";
    String SERVER_DELETE_ALL = "server_delete_all";
    String SERVER_GET_NEXT_ID  = "server_get_next_id";
    String SERVER_CLEAR_BUSY = "server_clear_busy";
    String SERVER_CLEAR_START_ID = "server_clear_start_id";

    String SERVER_CHANGE_CONFIG = "server_change_config";
    String SERVER_ANSWER_CONFIG = "config";

    String PREF_ACTIVITY = "pref_activity";

    String MAP_TYPE = "map_type";
    String MAP_SCALE = "map_scale";
    String RECORDS_MAX_NUMBER = "records_max_number";
    String ADMIN_PASS = "admin_pass";
    String URL_ADDR = "https://volganap.ru/excavate_coordinator/index_ec.php";
    String CONFIRM_CONNECTION = "Обратная связь получена!: ";
}
