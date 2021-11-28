package my.example.hospes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class AdminActivity extends AppCompatActivity {

    // виджеты
    /***
     * Плавающая кнопка добавления сотрудника
     */
    private FloatingActionButton fab_add;


    // Форма редактирования данных о человеке
    private LinearLayout l_edit_human;
    private EditText edit_name;
    private EditText edit_login;
    private EditText edit_password;
    private SwitchCompat s_oper_sex;
    private CheckBox check_admin;
    private Button b_save;
    private Button b_change_mode;
    /***
     * Виджет отображения списка людей
     */
    private RecyclerView rv_people;
    /***
     * Передатчик данных из списка в виджет списка людей
     */
    private RvPeopleAdapter adapterPeople;

    // Форма редактирования данных о комнате
    private LinearLayout l_edit_room;
    private Spinner spin_edit_room_type;
    private EditText edit_room_number;
    private EditText edit_room_cost;
    private EditText edit_room_description;
    private Button b_save_room;
    /***
     * Виджет отображения списка
     */
    private RecyclerView rv_rooms;
    /***
     * Передатчик данных из списка в виджет списка
     */
    private RvRoomsAdapter adapterRooms;

    // переменные
    private ArrayList<Human> operators;
    private Human man = new Human();
    private ArrayList<Room> rooms;
    private Room room = new Room();

    /***
     * режим работы: false - сотрудники; true - комнаты
     */
    private boolean listMode = false;


    /***
     * Первоначальная настройка экрана
     * @param savedInstanceState сохранённое состояние экрана
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setTitle("Режим администратора");

        fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listMode) {
                    addNewRoom();
                } else {
                    addNewOperator();
                }
            }
        });

        b_change_mode = findViewById(R.id.b_change_mode);
        b_change_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listMode = !listMode;
                showScreen();
            }
        });

        initFormHuman();
        initFormRoom();
        showScreen();
    }

    /***
     * Инициализация формы для редактирования сотрудников
     */
    private void initFormHuman() {
        l_edit_human = findViewById(R.id.l_edit_human);
        l_edit_human.setVisibility(View.GONE);

        edit_name = findViewById(R.id.edit_oper_name);
        edit_login = findViewById(R.id.edit_oper_login);
        edit_password = findViewById(R.id.edit_guest_password);
        s_oper_sex = findViewById(R.id.s_oper_sex);
        check_admin = findViewById(R.id.check_admin);

        b_save = findViewById(R.id.b_oper_save);
        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOperatorData();
            }
        });
    }

    /***
     * Инициализация формы для редактирования комнат
     */
    private void initFormRoom() {
        l_edit_room = findViewById(R.id.l_edit_room);
        l_edit_room.setVisibility(View.GONE);

        spin_edit_room_type = findViewById(R.id.spin_edit_room_type);
        // настроим список возможных типов комфортности комнат
        spin_edit_room_type.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Room.types));

        edit_room_number = findViewById(R.id.edit_room_number);
        edit_room_cost = findViewById(R.id.edit_room_cost);
        edit_room_description = findViewById(R.id.edit_room_description);

        b_save_room = findViewById(R.id.b_save_room);
        b_save_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRoomData();
            }
        });
    }

    /***
     * Инициализация виджета списка людей.
     */
    private void initRvPeople() {
        rv_people = findViewById(R.id.rv_people);
        // задать способ взаимного расположения элементов списка на экране (ветикальный скролл)
        rv_people.setLayoutManager(new LinearLayoutManager(this));

        // получить список людей. В данном случае сотрудников.
        operators = Repository.getPeople(false);
        // привязать список сотрудников и задать тип Сотрудники
        adapterPeople = new RvPeopleAdapter(operators, "o");
        // задать обработчик короткого нажатия на элемент списка
        adapterPeople.setClickListener(new RvPeopleAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editOperatorData(adapterPeople.getItem(position));
            }
        });
        // задать обработчик долгого нажатия на элемент списка
        adapterPeople.setLongClickListener(new RvPeopleAdapter.ItemLongClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                removeOperatorData(adapterPeople.getItem(position));
            }
        });

        // связываем виджет списка с адаптером данных списка
        rv_people.setAdapter(adapterPeople);
    }

    /***
     * Инициализация виджета списка комнат.
     */
    private void initRvRooms() {
        rv_rooms = findViewById(R.id.rv_rooms);
        // задать способ взаимного расположения элементов списка на экране (ветикальный скролл)
        rv_rooms.setLayoutManager(new LinearLayoutManager(this));

        // получить список всех комнат.
        rooms = Repository.getRooms(-1);
        // привязать список комнат
        adapterRooms = new RvRoomsAdapter(rooms);
        // задать обработчик короткого нажатия на элемент списка
        adapterRooms.setClickListener(new RvRoomsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editRoomData(adapterRooms.getItem(position));
            }
        });
        // задать обработчик долгого нажатия на элемент списка
        adapterRooms.setLongClickListener(new RvRoomsAdapter.ItemLongClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                removeRoomData(adapterRooms.getItem(position));
            }
        });

        // связываем виджет списка с адаптером данных списка
        rv_rooms.setAdapter(adapterRooms);
    }

    /***
     * Показать форму для создания нового сотрудника
     */
    private void addNewOperator() {
        // Показать пустую форму для создания нового оператора
        l_edit_human.setVisibility(View.VISIBLE);
        edit_name.setText("");
        edit_login.setText("");
        edit_password.setText("");
        s_oper_sex.setChecked(false);
        check_admin.setChecked(false);
        // не режим редактирования
        man = new Human();
        fab_add.setVisibility(View.GONE);
    }

    /***
     * Показать форму для создания новой комнаты
     */
    private void addNewRoom() {
        // Показать пустую форму для создания новой комнаты
        l_edit_room.setVisibility(View.VISIBLE);
        spin_edit_room_type.setSelection(0);
        edit_room_number.setText("");
        edit_room_cost.setText("");
        edit_room_description.setText("");
        // не режим редактирования
        room = new Room();
        fab_add.setVisibility(View.GONE);
    }

    /***
     * Показать данные сотрудника для редактирования
     * @param man редактируемый сотрудник
     */
    private void editOperatorData(Human man) {
        // заполняем форму соответствующими данными
        l_edit_human.setVisibility(View.VISIBLE);
        edit_name.setText(man.name);
        edit_login.setText(man.login);
        edit_password.setText(man.password);
        s_oper_sex.setChecked(man.sex);
        check_admin.setChecked(man.isAdmin);
        // режим редактирования
        this.man = man;
        fab_add.setVisibility(View.GONE);
    }

    /***
     * Показать данные о комнате для редактирования
     * @param room редактируемая комната
     */
    private void editRoomData(Room room) {
        // заполняем форму соответствующими данными
        l_edit_room.setVisibility(View.VISIBLE);
        spin_edit_room_type.setSelection(room.type);
        edit_room_number.setText(room.number);
        edit_room_cost.setText(String.format(Locale.ENGLISH, "%.2f", room.cost));
        edit_room_description.setText(room.description);
        // режим редактирования
        this.room = room;
        fab_add.setVisibility(View.GONE);
    }

    /***
     * Удалить данные оператора
     * @param man удаляемый сотрудник
     */
    private void removeOperatorData(Human man) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление сотрудника");
        builder.setMessage("Вы действительно желаете удалить этого сотрудника\n" + man.name + " из базы?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Удалить человека из базы (сотрудник в данном случае)
                Repository.removeHumanByLogin(man.login);
                // Получить обновлённый список сотрудников (админы, операторы)
                ArrayList<Human> people = Repository.getPeople(false);
                // Показать обновлённый список сотрудников
                adapterPeople.setNewList(people);
            }
        });
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Просто закрыть диалог, как и при тапе за пределами окна диалога
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /***
     * Удалить данные о комнате
     * @param room удаляемая комната
     */
    private void removeRoomData(Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление комнаты");
        builder.setMessage("Вы действительно желаете удалить комнату №" + room.number + " из базы?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Удалить комнату из базы
                Repository.removeRoomByNum(room.number);
                // Получить обновлённый список комнат
                ArrayList<Room> rooms = Repository.getRooms(-1);
                // Показать обновлённый список комнат
                adapterRooms.setNewList(rooms);
            }
        });
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Просто закрыть диалог, как и при тапе за пределами окна диалога
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /***
     * Сохранить данные сотрудника (переменная Human:man)
     */
    private void saveOperatorData() {
        String name = edit_name.getText().toString().trim();
        String login = edit_login.getText().toString().trim();
        String password = edit_password.getText().toString().trim();

        if ("".equals(name) || "".equals(login) || "".equals(password)) {
            Toast.makeText(this, "Поля 'Имя', 'Логин', 'Пароль' должны быть заполнены", Toast.LENGTH_LONG).show();
        } else {
            // Когда все обязательные поля заполнены.
            if (!man.login.equals("")) {
                // man здесь хранит данные о человеке выбранном из списка или ничего.
                // Человек без данных - режим создания.
                // Логин не пустой - режим редактирования.
                // Перед внесением изменений в базу нужно удалить старые данные из базы.
                Repository.removeHumanByLogin(man.login);
                // Потому что наша база не умеет обновлять имеющиеся данные.
            }
            // Создаём человека и указываем его данные
            man = new Human();
            man.name = name;
            man.login = login;
            man.password = password;
            man.sex = s_oper_sex.isChecked();
            man.isAdmin = check_admin.isChecked();
            // Проверяем уникальность логина
            Human h = Repository.findHumanById(man.login);
            if (h.login.equals("")) {
                // Когда не найден человек с таким логином - можно добавить человека с этим логином
                Repository.addOperator(man);
                // Получить обновлённый список сотрудников (админы, операторы)
                ArrayList<Human> people = Repository.getPeople(false);
                // Показать обновлённый список сотрудников
                adapterPeople.setNewList(people);
                // Спрятать форму редактирования данных
                l_edit_human.setVisibility(View.GONE);
                // Это необходимо, чтобы не включился режим редактирования при создании нового оператора
                fab_add.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Оператор с таким логином уже существует", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /***
     * Сохранить данные сотрудника (переменная Human:man)
     */
    private void saveRoomData() {
        String num = edit_room_number.getText().toString().trim();
        String cost = edit_room_cost.getText().toString().trim();
        String desc = edit_room_description.getText().toString().trim();

        if ("".equals(num) || "".equals(cost)) {
            Toast.makeText(this, "Поля 'Номер' и 'Стоимость' должны быть заполнены", Toast.LENGTH_LONG).show();
        } else {
            // Когда все обязательные поля заполнены.
            if (!room.number.equals("")) {
                // room здесь хранит данные о комнате выбранной из списка или ничего.
                // Комната без данных - режим создания.
                // Номер не пустой - режим редактирования.
                // Перед внесением изменений в базу нужно удалить старые данные из базы.
                Repository.removeRoomByNum(room.number);
                // Потому что наша база не умеет обновлять имеющиеся данные.
            }
            // Создаём комнату и указываем её данные
            room = new Room();
            room.type = spin_edit_room_type.getSelectedItemPosition();
            room.number = num;
            room.cost = Double.parseDouble(cost);
            room.description = desc;
            // Проверяем уникальность номера
            Room r = Repository.findRoomByNum(room.number);
            if (r.number.equals("")) {
                // Когда не найдена комната с таким номером - можно добавить её с этим номером
                Repository.addRoom(room);
                // Получить обновлённый список комнат
                ArrayList<Room> rooms = Repository.getRooms(-1);
                // Показать обновлённый список комнат
                adapterRooms.setNewList(rooms);
                // Спрятать форму редактирования данных
                l_edit_room.setVisibility(View.GONE);
                // Это необходимо, чтобы не включился режим редактирования при создании новой комнаты
                fab_add.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Комната с таким номером уже существует", Toast.LENGTH_LONG).show();
            }
        }
    }

    /***
     *
     */
    private void showScreen() {
        initRvPeople();
        initRvRooms();
        if (listMode) {
            b_change_mode.setText("к работе с сотрудниками");
            l_edit_human.setVisibility(View.GONE);
            rv_people.setVisibility(View.GONE);
            rv_rooms.setVisibility(View.VISIBLE);
        } else {
            b_change_mode.setText("к работе с комнатами");
            l_edit_room.setVisibility(View.GONE);
            rv_rooms.setVisibility(View.GONE);
            rv_people.setVisibility(View.VISIBLE);
        }
    }
}