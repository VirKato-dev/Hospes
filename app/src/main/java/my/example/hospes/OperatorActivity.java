package my.example.hospes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;


public class OperatorActivity extends AppCompatActivity {
    private LinearLayout l_edit_guest;
    private SwitchCompat s_sex;
    private EditText edit_guest_name;
    private EditText edit_guest_surname;
    private EditText edit_guest_family;
    private EditText edit_guest_note;
    private Spinner spin_room_type;
    private Spinner spin_room_num;
    private TextView t_room_info_cost;
    private LinearLayout l_date_arrive;
    private LinearLayout l_date_depart;
    private TextView t_date_arrive;
    private TextView t_date_depart;
    private Button b_guest_save;
    private RecyclerView rv_guests;
    private FloatingActionButton fab_add_guest;

    private ArrayList<Human> guests;
    private ArrayList<String> freeRooms;
    private RvPeopleAdapter adapter;
    private Human man = new Human();
    private boolean hasFree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);

        setTitle("Режим оператора");

        l_edit_guest = findViewById(R.id.l_edit_guest);
        s_sex = findViewById(R.id.s_sex);
        edit_guest_name = findViewById(R.id.edit_guest_name);
        edit_guest_surname = findViewById(R.id.edit_guest_surname);
        edit_guest_family = findViewById(R.id.edit_guest_family);
        edit_guest_note = findViewById(R.id.edit_guest_note);
        spin_room_type = findViewById(R.id.spin_room_type);
        spin_room_num = findViewById(R.id.spin_room_num);
        t_room_info_cost = findViewById(R.id.t_room_info_cost);
        l_date_arrive = findViewById(R.id.l_date_arrive);
        l_date_depart = findViewById(R.id.l_date_depart);
        t_date_arrive = findViewById(R.id.t_date_arrive);
        t_date_depart = findViewById(R.id.t_date_depart);
        b_guest_save = findViewById(R.id.b_guest_save);
        rv_guests = findViewById(R.id.rv_guests);
        fab_add_guest = findViewById(R.id.fab_add_guest);

        b_guest_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGuestData();
            }
        });

        fab_add_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewGuest();
            }
        });

        bindDatePickerTo(l_date_arrive, t_date_arrive);
        bindDatePickerTo(l_date_depart, t_date_depart);

        // получить список людей. В данном случае посетителей.
        guests = Repository.getPeople(true);
        // задать способ взаимного расположения элементов списка на экране (Вертикальный скролл)
        rv_guests.setLayoutManager(new LinearLayoutManager(this));
        // привязать список сотрудников и задать тип Посетители
        adapter = new RvPeopleAdapter(guests, "g");
        // задать обработчик короткого нажатия на элемент списка
        adapter.setClickListener(new RvPeopleAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editGuestData(adapter.getItem(position));
            }
        });
        // задать обработчик долгого нажатия на элемент списка
        adapter.setLongClickListener(new RvPeopleAdapter.ItemLongClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                removeGuestData(adapter.getItem(position));
            }
        });
        // связываем виджет списка с адаптером данных списка
        rv_guests.setAdapter(adapter);

        // настроим список возможных типов комнат
        spin_room_type.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Room.types));

        // действия при выборе типа комфортности выбираемой комнаты
        spin_room_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFreeRooms(man.login);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateFreeRooms(man.login);
            }
        });

        // действия при выборе номера комнаты
        spin_room_num.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCostOfRoom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateCostOfRoom();
            }
        });

        l_edit_guest.setVisibility(View.GONE);
    }

    /***
     * Сменить класс комфортности выбираемой комнаты
     * @param type комфортность
     */
    private void setRoomType(int type) {
        spin_room_type.setSelection(type);
    }

    /***
     * Обновить список свободных комнат при смене типа комфортности
     */
    private void updateFreeRooms(String guestId) {
        String s1 = t_date_arrive.getText().toString();
        String s2 = t_date_depart.getText().toString();
        if (s1.equals("") || s2.equals("")) {
            Toast.makeText(this, "Даты заселения и выселения обязательны", Toast.LENGTH_LONG).show();
            return;
        }
        long d1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .parse(s1, new ParsePosition(0)).getTime();
        long d2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .parse(s2, new ParsePosition(0)).getTime();

        freeRooms = Repository.getFreeRooms(spin_room_type.getSelectedItemPosition(), d1, d2, guestId);
        spin_room_num.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, freeRooms));

        // пустой список = нет свободных комнат для выбранного типа комфортности
        hasFree = freeRooms.size() > 0;
        if (!hasFree) {
            Toast.makeText(this, "Нет свободных комнат этого класса комфортности", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * Показать стоимость заселения на одну ночь в указанный номер
     */
    private void updateCostOfRoom() {
        if (freeRooms.size() > 0) {
            double cost = Repository.findRoomByNum(freeRooms.get(spin_room_num.getSelectedItemPosition())).cost;
            t_room_info_cost.setText(String.format(Locale.ENGLISH, "%.2f", cost));
        }
    }

    /***
     * Показать форму для создания нового посетителя
     */
    private void addNewGuest() {
        // не режим редактирования
        man = new Human();
        fab_add_guest.setVisibility(View.GONE);
        // Показать пустую форму для создания нового посетителя
        l_edit_guest.setVisibility(View.VISIBLE);
        edit_guest_name.setText("");
        edit_guest_surname.setText("");
        edit_guest_family.setText("");
        edit_guest_note.setText("");
        s_sex.setChecked(false);
        setRoomType(2);
    }

    /***
     * Показать данные посетителя для редактирования
     * @param man редактируемый посетитель
     */
    private void editGuestData(Human man) {
        // режим редактирования
        this.man = man;
        fab_add_guest.setVisibility(View.GONE);
        // заполняем форму соответствующими данными
        // распаковываем name для получения данных об Имени, Отчестве и Фамилии
        String[] val = man.name.split("֍");
        String name = val[0]; // имя
        String surname = val[1]; // отчество
        String family = val[2]; // фамилия
        // распаковываем password для получения данных о заселении
        String[] mRoom = man.password.split("֍");
        String num = mRoom[0]; // номер комнаты
        long arriveDate = Long.parseLong(mRoom[1]); // дата заселения
        long departDate = Long.parseLong(mRoom[2]); // дата выселения
        String notes = "";
        if (mRoom.length >3) notes = mRoom[3]; // примечания может отсутствовать
        // потому что при парсинге, для последнего пустого элемента не создаётся элемент массива

        l_edit_guest.setVisibility(View.VISIBLE);
        edit_guest_name.setText(name);
        edit_guest_surname.setText(surname);
        edit_guest_family.setText(family);
        edit_guest_note.setText(notes);
        s_sex.setChecked(man.sex);
        Room room = Repository.findRoomByNum(num);
        t_room_info_cost.setText(String.format(Locale.ENGLISH, "%.2f", room.cost));
        t_date_arrive.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(arriveDate));
        t_date_depart.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(departDate));
        setRoomType(room.type);
        updateFreeRooms(man.login);
        spin_room_num.setSelection(freeRooms.indexOf(num));
    }

    /***
     * Удалить данные гостя
     * @param man удаляемый гость
     */
    private void removeGuestData(Human man) {
        // поставим фамилию перед именем
        String[] val = man.name.split("֍");
        String name = val[2] + " " + val[0] + " " + val[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление посетителя");
        builder.setMessage("Вы действительно желаете удалить этого посетителя '" + name + "' из базы?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Удалить человека из базы (посетитель в данном случае)
                Repository.removeHumanByLogin(man.login);
                // Получить обновлённый список гостей
                ArrayList<Human> people = Repository.getPeople(true);
                // Показать обновлённый список гостей
                adapter.setNewList(people);
                l_edit_guest.setVisibility(View.GONE);
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
     * Сохранить данные посетителя (переменная Human:man)
     */
    private void saveGuestData() {
        if (!hasFree) {
            Toast.makeText(this, "Нет свободный комнат на указанные даты", Toast.LENGTH_LONG).show();
            return;
        }
        String n1 = edit_guest_name.getText().toString().trim();
        String n2 = edit_guest_surname.getText().toString().trim();
        String n3 = edit_guest_family.getText().toString().trim();
        String name = n1 + "֍" + n2 + "֍" + n3;
        String p1 = "" + freeRooms.get(spin_room_num.getSelectedItemPosition());
        String p21 = t_date_arrive.getText().toString();
        String p31 = t_date_depart.getText().toString();
        if (p21.equals("") || p31.equals("")) {
            Toast.makeText(this, "Даты заселения и выселения обязательны", Toast.LENGTH_LONG).show();
            return;
        }
        String p2 = String.valueOf(
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .parse(p21, new ParsePosition(0)).getTime());
        String p3 = String.valueOf(
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .parse(p31, new ParsePosition(0)).getTime());
        String p4 = edit_guest_note.getText().toString().trim();
        String password = p1 + "֍" + p2 + "֍" + p3 + "֍" + p4;

        if ("".equals(n1) || "".equals(n3) || "".equals(p1) || !hasFree) {
            Toast.makeText(this, "Поля 'Имя', 'Фамилия', 'Комната' должны быть заполнены", Toast.LENGTH_LONG).show();
        } else {
            String login;
            // Когда все обязательные поля заполнены.
            if (!man.login.equals("")) {
                // man здесь хранит данные о человеке выбранном из списка или ничего.
                // Логин не пустой - режим редактирования.
                login = man.login;
                // Перед внесением изменений в базу нужно удалить старые данные из базы.
                Repository.removeHumanByLogin(man.login);
                // Потому что наша база не умеет обновлять имеющиеся данные.
            } else {
                // Человек без данных - режим создания.
                // Выдать новый идентификатор
                login = generateUID();
            }
            // Создаём человека и указываем его данные
            man = new Human();
            man.login = login;
            man.name = name;
            man.password = password;
            man.sex = s_sex.isChecked();
            man.isGuest = true;
            // Проверяем уникальность логина
            Human h = Repository.findHumanById(man.login);
            if (h.login.equals("")) {
                // Когда не найден человек с таким логином - можно добавить человека с этим логином
                Repository.addOperator(man);
                // Получить обновлённый список гостей
                ArrayList<Human> people = Repository.getPeople(true);
                // Показать обновлённый список гостей
                adapter.setNewList(people);
                // Спрятать форму редактирования данных
                l_edit_guest.setVisibility(View.GONE);
                // Это необходимо, чтобы не включился режим редактирования при создании нового оператора
                fab_add_guest.setVisibility(View.VISIBLE);
            } else {
                // Скопировано из кода AdminActivity.java
                // Здесь этот блок кода является бессмысленным так как проверка идентификатора уже сделана выше
                Toast.makeText(this, "Посетитель с таким идентификатором уже существует", Toast.LENGTH_LONG).show();
            }
        }
    }


    /***
     * Сгенерировать новый уникальный идетификатор
     * @return идентификатор гостя
     */
    private String generateUID() {
        Human man;
        String newUID;
        do {
            newUID = "id" + new Random().nextInt();
            man = Repository.findHumanById(newUID);
        } while (!man.login.equals(""));
        // когда не нашёлся человек с таким идентификатором
        return newUID;
    }


    /***
     * Показать диалог выбора даты
     * @param vg виджет вызова диалога
     * @param tv виджет отображения даты
     */
    private void bindDatePickerTo(View vg, TextView tv) {
        vg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                Dialog dialog = new Dialog(v.getContext());
                DatePicker datePicker = new DatePicker(v.getContext());
                datePicker.setCalendarViewShown(true);
                datePicker.setSpinnersShown(false);
                datePicker.init(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                tv.setText(
                                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTimeInMillis()));
                                dialog.dismiss();
                                updateFreeRooms(man.login);
                            }
                        });
                dialog.setContentView(datePicker);
                dialog.show();
            }
        });
    }
}