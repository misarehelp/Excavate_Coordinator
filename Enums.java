package ru.volganap.nikolay.haircut_schedule;

public interface Enums {
    enum Job {

        MH("мужская стрижка"),
        WH("женская стрижка"),
        WC("окрашивание"),
        S1("услуга 1"),
        S2("услуга 2"),
        S3("услуга 3"),;

        private final String value;
        Job(String value) {
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
}