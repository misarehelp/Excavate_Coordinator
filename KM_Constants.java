package ru.volganap.nikolay.excavate_coordinator;

interface KM_Constants  {
    String LOG_TAG = "myLogs";
    String[] FROM = {"0", "1", "2", "3", "4"};
    String DEPARTMENT_USER = "department_user";
    String DEP_LINE_DATA = "dep_line_data";
    String DATA_TYPE = "data_type";
    String SENDER = "sender";
    String MESSAGE = "message";
    String PRESENTER = "presenter";
    String PARAMS_DATA = "params_data";

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

    String SAVE_BUTTON_WAS_CLICKED = "save button";
    String DELETE_BUTTON__WAS_CLICKED = "delete button";

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
    String NET_ERROR_GOT_LOCATION_STATE = "2";

    int MAPS_ACTIVITY_REQUEST_CODE = 10;
    int PERMIT_ACTIVITY_REQUEST_CODE = 11;

    String PREF_ACTIVITY = "pref_activity";
    String BROWSER_MODE = "browser_mode";
    String ADMIN = "admin";
    String MASTER = "master";
    String SLAVE = "slave";
    String SERVER_DELAY_TITLE ="server_delay";
    String MAP_TYPE = "map_type";
    String MARKER_DELAY = "marker_delay";
    String MARKER_SCALE = "marker_scale";
    String MARKER_MAX_NUMBER = "marker_max_number";
    String CHOSEN_KID_MARKERS = "chosen_kid_markers";
    String MAX_LOCATION_TIME = "max_location_time";
    String CHANGE_CONFIG_SERVER = "config";
    String ACTION_FROM_OKHTTP = "action_from_okhttp";
    String ACTION_FROM_BR = "ru.volganap.nikolay.excavate_coordinator";
    String URL_ADDR = "https://volganap.ru/excavate_coordinator/index_ec.php";
    String CONFIG_SERVER_STATE = "Изменение настроек на сервере: ";
    String CONFIRM_CONNECTION = "Обратная связь получена!: ";
    String LOCATION_IS_TURNED_OFF ="The Kid must turn the location on!";
}
