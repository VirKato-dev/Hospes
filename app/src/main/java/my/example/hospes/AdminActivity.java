package my.example.hospes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class AdminActivity extends AppCompatActivity {

    // виджеты
    /***
     * Плавающая кнопка добавления сотрудника
     */
    private FloatingActionButton fab_add_operator;

    /***
     * Виджет отображения списка
     */
    private RecyclerView rv_operators;

    /***
     * Передатчик данных из списка в виджет списка
     */
    private RvAdapter adapter;

    private LinearLayout l_edit_human;
    private EditText edit_name;
    private EditText edit_login;
    private EditText edit_password;
    private SwitchCompat s_oper_sex;
    private CheckBox check_admin;
    private Button b_save;
    private Button b_change_mode;

    // переменные
    private ArrayList<Human> operators;
    private Human man = new Human();

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

        l_edit_human = findViewById(R.id.l_edit_user);
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

        b_change_mode = findViewById(R.id.b_change_mode);
        b_change_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listMode = !listMode;
                showScreen();
            }
        });

        fab_add_operator = findViewById(R.id.fab_add_operator);
        fab_add_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewOperator();
            }
        });

        // получить список людей. В данном случае сотрудников.
        operators = Repository.getPeople(false);
        rv_operators = findViewById(R.id.rv_operators);
        // задать способ взаимного расположения элементов списка на экране (ветикальный скролл)
        rv_operators.setLayoutManager(new LinearLayoutManager(this));
        // привязать список сотрудников и задать тип Сотрудники
        adapter = new RvAdapter(operators, "o");
        // задать обработчик короткого нажатия на элемент списка
        adapter.setClickListener(new RvAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editOperatorData(adapter.getItem(position));
            }
        });
        // задать обработчик долгого нажатия на элемент списка
        adapter.setLongClickListener(new RvAdapter.ItemLongClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                removeOperatorData(adapter.getItem(position));
            }
        });
        // связываем виджет списка с адаптером данных списка
        rv_operators.setAdapter(adapter);
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
        fab_add_operator.setVisibility(View.GONE);
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
        fab_add_operator.setVisibility(View.GONE);
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
                adapter.setNewList(people);
                // Спрятать форму редактирования данных
                l_edit_human.setVisibility(View.GONE);
                // Это необходимо, чтобы не включился режим редактирования при создании нового оператора
                fab_add_operator.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Оператор с таким логином уже существует", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showScreen() {
        if (listMode) {
            b_change_mode.setText("к работе с сотрудниками");

        } else {
            b_change_mode.setText("к работе с комнатами");

        }
    }
}