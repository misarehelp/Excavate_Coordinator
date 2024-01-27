package ru.volganap.nikolay.haircut_schedule;

public interface Enums {

    enum ClientComand {

        SERVER_GET_CLIENTS ("server_get_clients"),
        SERVER_ADD_CLIENT ("server_add_client"),
        SERVER_DELETE_CLIENT ("server_delete_client"),
        SERVER_CHANGE_CLIENT ("server_change_client"),
        SERVER_GET_CLIENT_ID ("server_get_client_id"),
        GET_CLIENT_DATA_FROM_BASE ("get_client_data_from_base"),
        SHOW_CLIENT_JOB ("show_client_job");

        private final String value;
        ClientComand(String value) {
            this.value = value;
        }
        String getValue() {
            return value;
        }
    }

    enum PhotoType {
        CAMERA,
        REPOSITORY,
        GALLERY;
    }

    enum RecordVisibility {
        ARCHIVE,
        SHOW,
        HIDE;
    }
}