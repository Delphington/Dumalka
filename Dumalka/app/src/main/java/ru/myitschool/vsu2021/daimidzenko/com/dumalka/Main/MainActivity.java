package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;

public class MainActivity extends AppCompatActivity {

    private Button Btn_Cont;
    private DbGlbSvc dbGlbSvc;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Скрываем верхнюю строку состояния
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //-------------------------------------------
        setContentView(R.layout.activity_main);
        Btn_Cont = findViewById(R.id.MA_BTN_Start);
        image = findViewById(R.id.ImageLogo);

        dbGlbSvc = new DbGlbSvc(MainActivity.this);

        //== Для темы
        String f = dbGlbSvc.getSystemValue("CFG", "LightThema");
        if (f.equals("Y")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // светлая
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // темная
            image.setImageResource(R.drawable.ic_d_start_logo_night);
        }
        //================================================

        Btn_Cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("mode", "cont");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Анимация
                DbGlbSvc.setSystemValue("SYS1", "Имитация", "N");
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("mode", "cont");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Анимация
                DbGlbSvc.setSystemValue("SYS1", "Имитация", "N"); //Обнуление иммитации
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}