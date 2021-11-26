package my.example.hospes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    // список виджетов для формы авторизации, к которым необходим доступ из кода приложения
    private EditText edit_login;
    private EditText edit_password;
    private Button button_auth;

    private String login = "";
    private String password = "";

    private Human man;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // виджеты макета экрана
        edit_login = findViewById(R.id.edit_login);
        edit_password = findViewById(R.id.edit_password);
        button_auth = findViewById(R.id.button_auth);

        button_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRole();
            }
        });
    }


    /***
     * Проверить роль пользователя и отправить на соответствующую Активность
     */
    private void checkRole() {
        // получаем введённый текст без крайних пробелов
        login = edit_login.getText().toString().trim();
        password = edit_password.getText().toString().trim();

        // ищем пользователя
        man = Repository.findHumanInDB(login);

        if (man.login.equals(login) && man.password.equals(password)) {
            // отправить найденного пользователя на соответствующий экран, согласно его роли
            if (man.isAdmin) {
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                startActivity(new Intent(this, OperatorActivity.class));
            }
            Toast.makeText(this, "Добро пожаловать, " + man.name + "!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Такого пользователя не существует.", Toast.LENGTH_LONG).show();
        }

        // очистить поля ввода
        edit_login.setText("");
        edit_password.setText("");
    }


}