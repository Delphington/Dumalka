package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterGame.MyHistoryAdapter;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterGame.MyHistoryClass;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;


public class History extends AppCompatActivity implements Dumalka {

    private DbGlbSvc dbGlbSvc;
    private Dialog dialog;
    private MyHistoryAdapter adapter;
    private String AllFQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Скрываем верхнюю строку состояния
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //-------------------------------------------
        setContentView(R.layout.activity_history);
        fillString();

        //скрываем системные кнокпи
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        dbGlbSvc = new DbGlbSvc(History.this); //перадача context в бд
        //-------------------------------------------
        // Создание Title бара
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView allQFavorite = findViewById(R.id.allQuestionsFavorite);
        LinearLayout LayoutFavorites = findViewById(R.id.HLayoutFavorites);
        LinearLayout LayoutLV = findViewById(R.id.LayoutLV);

//        /** Меняем фон когда иммитация включенна  */
//        boolean flagImitation = DbGlbSvc.getSystemValue("SYS1", "Имитация").equals("Y");
//        if (flagImitation) {
//            MainHistoryLayout.setBackgroundColor(Color.parseColor("#D779EE"));
//        }

        //Переход на активность с Favorite вопросами
        LayoutFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this, Favorite.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Анимация
            }
        });

        // Количетво фаворитов;
        allQFavorite.setText(AllFQ + " " + DbGlbSvc.getCountFavorite());

        /// Ширина между элементами listview
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setDivider(new ColorDrawable(getApplication().getResources().getColor(R.color.AH_BackgroundBetweenItem)));
        lv.setDividerHeight(1);

        //Передаем adapter
        ArrayList<MyHistoryClass> ssd = new ArrayList<>();
        adapter = new MyHistoryAdapter(this, ssd);
        lv.setAdapter(adapter);


        // Список всех игр
        Cursor q = DbGlbSvc.getGameList();
        q.moveToFirst();
        while (!q.isAfterLast()) {
            MyHistoryClass myHistoryItem = new MyHistoryClass();
            myHistoryItem.gameId = q.getInt(0);
            myHistoryItem.allQuestion = q.getInt(2);
            myHistoryItem.trueQuestion = q.getInt(3); // best
            myHistoryItem.falseQuestion = myHistoryItem.allQuestion - myHistoryItem.trueQuestion; // остальные
            myHistoryItem.gameName = q.getString(1);
            ssd.add(myHistoryItem);
            adapter.notifyDataSetInvalidated(); // обновление
            q.moveToNext();
        }
        q.close();

/** ###################################### */
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Установка значка об информации
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dialog_del_game_m, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //НА action Bar стрелочка назад
        if (id == android.R.id.home) {
            Intent intent = new Intent(History.this, MainActivity2.class);
            intent.putExtra("mode", "back");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
            // переход в MAin2

        }
        //Dialog информация об удалении
        if (id == R.id.dialog_del_game_m) {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// скрывваем заголовок
            dialog.setContentView(R.layout.dialog_del_game);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //Прозрачный фон
            dialog.setCancelable(false); // Диалог нельзя закрыть кнопкой назад
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    //  Закрытия диалога об удалении
    public void onCloseDialogDelGame(View view) {
        dialog.dismiss();
    }

    @Override
    public void fillString() {
        AllFQ = getString(R.string.J_HA_TextFavoriteQ);
    }
}