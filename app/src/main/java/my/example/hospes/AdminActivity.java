package my.example.hospes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private FloatingActionButton fab_add_operator;
    private RecyclerView rv_operators;
    private Rv_OperatorsAdapter adapter;
    private ArrayList<Human> operators;
    private Human man = new Human();

    private LinearLayout l_edit_user;
    private EditText edit_name;
    private EditText edit_login;
    private EditText edit_password;
    private CheckBox check_admin;
    private Button b_save;

    private boolean edit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setTitle("Режим администратора");

        l_edit_user = findViewById(R.id.l_edit_user);
        l_edit_user.setVisibility(View.GONE);

        edit_name = findViewById(R.id.edit_oper_name);
        edit_login = findViewById(R.id.edit_oper_login);
        edit_password = findViewById(R.id.edit_oper_password);
        check_admin = findViewById(R.id.check_admin);

        b_save = findViewById(R.id.b_oper_save);
        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        rv_operators = findViewById(R.id.rv_operators);
        fab_add_operator = findViewById(R.id.fab_add_operator);
        fab_add_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewUser();
            }
        });

        operators = Repository.getPeople(false);

        adapter = new Rv_OperatorsAdapter(operators);
        adapter.setClickListener(new Rv_OperatorsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                man = adapter.getItem(position);
                editUserData(man);
            }
        });
        rv_operators.setAdapter(adapter);
        rv_operators.setLayoutManager(new LinearLayoutManager(this));
    }


    /***
     * показать форму для создания нового оператора
     */
    private void addNewUser() {
        l_edit_user.setVisibility(View.VISIBLE);
        edit_name.setText("");
        edit_login.setText("");
        edit_password.setText("");
        check_admin.setChecked(false);
        man = new Human();
        edit = false;
    }


    /***
     * показать данные оператора для редактирования
     * @param man редактируемый оператор
     */
    private void editUserData(Human man) {
        l_edit_user.setVisibility(View.VISIBLE);
        edit_name.setText(man.name);
        edit_login.setText(man.login);
        edit_password.setText(man.password);
        check_admin.setChecked(man.isAdmin);
        edit = true;
    }


    /***
     * сохранить данные оператора (переменная user)
     */
    private void saveUserData() {
        if (!man.login.equals("")) {
            if (edit) {
                Repository.removeOperatorByLogin(man.login);
            }
        }
        man = new Human();
        man.name = edit_name.getText().toString().trim();
        man.login = edit_login.getText().toString().trim();
        man.password = edit_password.getText().toString().trim();
        man.isAdmin = check_admin.isChecked();
        Human man = Repository.findHumanInDB(this.man.login);
        if (man.login.equals("")) {
            Repository.addOperator(this.man);

            // показать обновлённый список операторов
            ArrayList<Human> people = Repository.getPeople(false);
            adapter.setNewList(people);
            l_edit_user.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Оператор с таким логином уже существует", Toast.LENGTH_SHORT).show();
        }
    }
}