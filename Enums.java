package ru.volganap.nikolay.haircut_schedule;

public interface Enums {
    enum Approvement {

        UN("н/д"),
        ND("---"),
        YES("да"),
        EX("есть"),
        NO("нет");

        private final String value;

        Approvement(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

}