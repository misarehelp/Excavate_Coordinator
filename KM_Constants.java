package ru.volganap.nikolay.excavate_coordinator;

public interface KM_Constants  {
    String LOG_TAG = "myLogs";
    final String[] FROM = {"0", "1", "2", "3", "4"};
    String DEPARTMENT_USER = "department_user";
    String DEP_LINE_DATA = "dep_line_data";
    String DATA_TYPE = "data_type";

    //Info for state window
    String DATA_IS_READY ="Данные получены. ";
    String DATA_REQUEST_PROCESSING =" Идет обработка запроса:...";
    String DATA_WAS_NOT_CHANGED ="Данные не изменились";
    String DATA_WAS_DELETED ="Текущий наряд был удален";
    String URL_WAS_NOT_FOUND = "The requested URL was not found on this server";
    String NET_ERROR_STATE = "Произошла ошибка подключения к серверу";

    String SERVER_GET_ALL ="server_get_all";
    String SERVER_PUT_ALL ="server_put_all";
    String EMPTY_STORAGE_STATE = "Нет данных на сервере";
    String REQUEST_IS_EMPTY = "request_is_empty";
    String DATA_WAS_NOT_SAVED ="data_was_not_saved";
    String DATA_WAS_SAVED ="data_saved";
    String SERVER_CHANGE_CONFIG = "server_change_config";
    String NET_ERROR_GOT_LOCATION_STATE = "2";

    String NEW_PERMIT_CODE ="new_permit_code";
    String EDIT_PERMIT_CODE ="Режим редактирования";
    String SHOW_PERMIT_CODE ="Режим просмотра";
    String ADD_PERMIT_CODE ="add_permit_code";
    int MAPS_ACTIVITY_REQUEST_CODE = 10;
    int PERMIT_ACTIVITY_REQUEST_CODE = 11;

    String PREF_ACTIVITY = "pref_activity";
    String BROWSER_MODE = "browser_mode";
    String ADMIN = "admin";
    String MASTER = "master";
    String SLAVE = "slave";
    String SERVER_DELAY_TITLE ="server_delay";
    String PUT_DATA = "put_data";
    String SENDER = "sender";
    String MESSAGE = "message";
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
    String NO_LOCATION_FOUND_STATE = "Местоположение не удалось определить";
    String CONFIG_SERVER_STATE = "Изменение настроек на сервере: ";
    String CONFIRM_CONNECTION = "Обратная связь получена!: ";
    String LOCATION_IS_TURNED_OFF ="The Kid must turn the location on!";
}

enum Approvement {

    UNKNOWN ("нет данных"),
    YES ("есть"),
    NO ("нет");

  private final String value;

  Approvement(String value){
        this.value = value;
  }

  String getValue () {
      return value;
  }
}

