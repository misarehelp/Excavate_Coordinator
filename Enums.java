package ru.volganap.nikolay.excavate_coordinator;

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

    enum PermitBlock {
        VISIBLE,
        INVISIBLE;
    }

    enum PermitState {
        OP ("откр."),
        AP ("согл."),
        CL ("закр.");

        private final String value;

        PermitState(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }

    }
}