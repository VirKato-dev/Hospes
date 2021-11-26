package my.example.hospes;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/***
 * Набор функций для удобной работы с файлом.
 * Это позволит легко заменить файл в будущем, например базой данных.
 * Конечно, придётся многое переписать или создать новый класс.
 */
public class Repository {

    /***
     * Первичная настройка папки для хранения файла
     */
    private static File dir = new File("");

    /***
     * Инициализация папки для хранения файла
     * @param context контекст приложения/активности
     */
    public static void initialize(Context context) {
        dir = context.getExternalCacheDir();
    }


    /***
     * Поиск человека в файле по логину/идентификатору
     * @param login логин/идентификатор уникален для каждого человека
     * @return данные о человеке в виде собственного типа данных Human
     */
    public static Human findHumanInDB(String login) {
        // данные главного Админа системы
        Human man = new Human();
        man.isAdmin = true;
        man.login = "admin";
        man.password = "admin";
        man.name = "мой Хозяин";

        // если это не главный админ системы (его данные есть только в приложении)
        if (!login.equals(man.login)) {
            man = new Human();
            // искать человека в файле
            File file = new File(dir, "users.db");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                InputStream fis = new FileInputStream(file);
                Reader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                Scanner scanner = new Scanner(reader);
                scanner.useDelimiter("\n");
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    Log.e("find", line);
                    String[] values = line.split("\\|");
                    man.name = values[0];
                    man.login = values[1];
                    man.password = values[2];
                    man.isAdmin = Boolean.parseBoolean(values[3]);
                    man.isGuest = Boolean.parseBoolean(values[4]);
                    if (man.login.equals(login)) {
                        break;
                    }
                    man = new Human();
                }
                scanner.close();
                reader.close();
                fis.close();
            } catch (IOException e) {
                Log.e("REPOSITORY find", e.getMessage());
            }
        }
        return man;
    }


    /***
     * получить список зарегистрированных операторов
     * @param isGuest гость(true) или сотрудник(false)
     * @return список людей из файла
     */
    public static ArrayList<Human> getPeople(boolean isGuest) {
        ArrayList<Human> people = new ArrayList<>();

        File file = new File(dir, "users.db");
        try {
            if (file.exists()) {
                InputStream fis = new FileInputStream(file);
                Reader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                Scanner scanner = new Scanner(reader);
                scanner.useDelimiter("\n"); // разделитель строк
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    Log.e("get", line);
                    String[] values = line.split("\\|");
                    Human man = new Human();
                    man.name = values[0];
                    man.login = values[1];
                    man.password = values[2];
                    man.isAdmin = Boolean.parseBoolean(values[3]);
                    man.isGuest = Boolean.parseBoolean(values[4]);
                    if (man.isGuest == isGuest) {
                        // добавляем в список только выбранную категорию людей
                        people.add(man);
                    }
                }
                fis.close();
            }
        } catch (IOException e) {
            Log.e("REPOSITORY get", e.getMessage());
        }

        return people;
    }


    /***
     * Удалить человека из файла
     * @param login логин/идентификатор человека
     */
    public static void removeOperatorByLogin(String login) {
        // исключить случай
        File file_from = new File(dir, "users.db");
        if (file_from.exists()) file_from.renameTo(new File(dir, "users.bak"));
        File file_to = new File(dir, "users.db");

        try {
            file_from = new File(dir, "users.bak");
            if (!file_from.exists()) {
                file_from.createNewFile();
            }
            if (!file_to.exists()) {
                file_to.createNewFile();
            }
            InputStream fis = new FileInputStream(file_from);

            Reader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            FileWriter writer = new FileWriter(file_to, true);

            Scanner scanner = new Scanner(reader);
            scanner.useDelimiter("\n"); // разделитель строк
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split("\\|");
                Human man = new Human();
                man.name = values[0];
                man.login = values[1];
                man.password = values[2];
                man.isAdmin = Boolean.parseBoolean(values[3]);
                man.isGuest = Boolean.parseBoolean(values[4]);
                if (!man.login.equals(login)) {
                    // записать в новый файл
                    line = man.name + "|" + man.login + "|" + man.password + "|" + man.isAdmin + "|" + man.isGuest +"\n";
                    writer.write(line);
                    writer.flush();
                }
            }
            writer.close();
            fis.close();
        } catch (IOException e) {
            Log.e("REPOSITORY remove", e.getMessage());
        }

        file_from.delete();
    }


    /***
     * добавить человека в файл
     * @param man человек
     */
    public static void addOperator(Human man) {
        File file = new File(dir, "users.db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            // открыть файл в режиме добавления текста
            Writer fw = new FileWriter(file, true);
            String line = man.name + "|" + man.login + "|" + man.password + "|" + man.isAdmin + "|" + man.isGuest +"\n";
            fw.write(line);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            Log.e("REPOSITORY add", e.getMessage());
        }
    }
}
