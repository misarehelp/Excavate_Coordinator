package ru.volganap.nikolay.excavate_coordinator;

public interface Enums {
    enum Approvement {

        UNKNOWN("нет данных"),
        YES("есть"),
        NO("нет");

        private final String value;

        Approvement(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    enum PermitBlock {
        VISIBLE("visible"),
        INVISIBLE("invisible");

        private final String value;

        PermitBlock(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }
}