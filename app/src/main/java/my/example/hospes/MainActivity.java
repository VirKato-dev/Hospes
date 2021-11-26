package my.example.hospes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/***
 * оставим стартовую Активность для заставки
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Repository.initialize(getApplicationContext());

        // запустим основную программу
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}