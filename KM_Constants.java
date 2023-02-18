package ru.volganap.nikolay.excavate_coordinator;

interface KM_Constants  {
    String LOG_TAG = "myLogs";
    String[] FROM = {"0", "1", "2", "3", "4"};
    String DEPARTMENT_USER = "department_user";
    String PREFERENCE_SERVER_SETUP = "server_setup";
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

    String NEW_PERMIT_CODE = "new_permit_code";
    String EDIT_PERMIT_CODE = "Режим редактирования";
    String SHOW_PERMIT_CODE = "Режим просмотра";
    String ADD_PERMIT_CODE = "add_permit_code";

    String START_LINE_CODE = "Тапом на карте отметьте  стартовую точку начала новой линии";
    String ADD_LINE_CODE = "Укажите тапом на карте следующую точку";
    String ADD_LINE_OR_FINISH_CODE = "Укажите тапом  на карте следующую точку или нажмите *Закончить*";
    String END_LINE_CODE = "Тапом на карте начните новую группу линий или завершите построение линий";
    String CONTOUR_LINE_CODE = "Для обозначения контура необходимо установить не менее 3 точек";
    String CLEAR_LINE_CODE = "Последняя введеная линия (группа линий) удалена";

    String SERVER_GET_ALL ="server_get_all";
    String SERVER_PUT_ALL ="server_put_all";
    String SERVER_CHANGE_CONFIG = "server_change_config";

    String PREF_ACTIVITY = "pref_activity";
    String MODE_USER = "mode_user";
    String ADMIN = "admin";
    String MASTER = "master";
    String SLAVE = "slave";

    String MAP_TYPE = "map_type";
    String MAP_SCALE = "map_scale";
    String RECORDS_MAX_NUMBER = "records_max_number";
    String URL_ADDR = "https://volganap.ru/excavate_coordinator/index_ec.php";
    String CONFIG_SERVER_STATE = "Изменение настроек на сервере: ";
    String CONFIRM_CONNECTION = "Обратная связь получена!: ";
}
