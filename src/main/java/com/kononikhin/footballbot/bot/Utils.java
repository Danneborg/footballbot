package com.kononikhin.footballbot.bot;

public class Utils {

    public static final int ELEMENTS_IN_A_ROW = 4;
    //Возможно когда-то пригодится
    private static final int MAX_ELEMENTS = 100;

    //TODO придумать что делать если послали число меньше 1. Скорее всего надо будет смотреть что было на предыдущем шаге и отправлять предыдущую информацию
    public static int defineNumberOfRows(int numberOfButtons) {

        if (numberOfButtons <= ELEMENTS_IN_A_ROW) {
            return 1;
        }

        if (numberOfButtons % ELEMENTS_IN_A_ROW == 0) {
            return numberOfButtons / ELEMENTS_IN_A_ROW;
        }

        return (numberOfButtons / ELEMENTS_IN_A_ROW) + 1;

    }

}
