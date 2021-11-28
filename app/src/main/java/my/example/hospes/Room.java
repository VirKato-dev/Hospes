package my.example.hospes;

import java.util.ArrayList;
import java.util.Arrays;

public class Room {
    public static final ArrayList<String> types = new ArrayList<>(Arrays.asList("Эконом", "Комфорт", "Люкс"));

    /***
     * Номер комнаты (уникальный)
     */
    public String number = "";

    /***
     * Тип комфортности (соответствует списку types)
     */
    public int type = 0;

    /***
     * Описание комнаты
     */
    public String description = "";

    /***
     * Стоимость за 1 ночь проживания
     */
    public double cost = 0.0;
}
