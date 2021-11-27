package my.example.hospes;

import java.util.ArrayList;
import java.util.Arrays;

public class Room {
    public static final ArrayList<String> types = new ArrayList<>(Arrays.asList("Эконом", "Комфорт", "Люкс"));

    public String number = "";
    public int type = 0;
    public String description = "";
    public double cost = 0.0;
}
