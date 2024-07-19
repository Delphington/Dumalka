package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterFavorite.MyFavoriteAdapter;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterFavorite.MyFavoriteClass;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;


public class Favorite extends AppCompatActivity implements Dumalka {

    private DbGlbSvc dbGlbSvc;
    private DbGlbSvc.QuestionRecord qRec;
    private boolean flag_null = true;
    private MyFavoriteAdapter adapter;
    private LinearLayout MainLinearLayout;
    private ListView lv;
    private int currentNightMode;
    private String SearchHint;
    private boolean flagSearch = true;

    ArrayList<MyFavoriteClass> ssd = new ArrayList<>();
    ArrayList<MyFavoriteClass> ssdCopy = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Скрываем верхнюю строку состояния
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //---------------------------------
        setContentView(R.layout.activity_favorite);

        //Для темы
        currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;


        dbGlbSvc = new DbGlbSvc(Favorite.this); //перадача context в бд

        MainLinearLayout = findViewById(R.id.MainLinearLayout);
        lv = (ListView) findViewById(R.id.listViewFavorite);

        lv.setDivider(new ColorDrawable(getApplication().getResources().getColor(R.color.AF_BetweenItem)));
        lv.setDividerHeight(2);

        //Action Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Передача Adapter

        adapter = new MyFavoriteAdapter(this, ssd);
        lv.setAdapter(adapter);

        //Заполняем Favorite
        Cursor q = DbGlbSvc.getFavoriteList();
        q.moveToFirst();
        int idx = 0;
        while (!q.isAfterLast()) {
            idx++;
            flag_null = false; // если нет Favorite меняем Layout
            MyFavoriteClass myFavoriteItem = new MyFavoriteClass();
            //myFavoriteItem.questionNum = String.valueOf(q.getInt(0));
            myFavoriteItem.questionNum = String.valueOf(idx); // т.к. q.getInt(0) всегда = 0
            myFavoriteItem.questionText = q.getString(1);
            myFavoriteItem.questionId = q.getInt(2);
            myFavoriteItem.gameId = q.getInt(3);
            myFavoriteItem.isCheck = q.getInt(4);
            ssd.add(myFavoriteItem);
            ssdCopy.add(myFavoriteItem);
            adapter.notifyDataSetInvalidated(); //обновление
            q.moveToNext();
        }
        q.close();
        fillString();

        //Установка Background если нет Favorite
        if (flag_null) {
            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    MainLinearLayout.setBackgroundResource(R.drawable.ic_favorite_null_ligth);
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    MainLinearLayout.setBackgroundResource(R.drawable.ic_favorite_null_night);
                    break;
            }
        } else {
            MainLinearLayout.setBackgroundColor(ContextCompat.getColor(Favorite.this, R.color.AF_Background));
        }
    }

    // Метод для Title бара back
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(Favorite.this, History.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); //Анимация
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //Системная кнопка назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    //Меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Если нет вопросов
        if (flag_null) {
            flagSearch = false;
        }

        getMenuInflater().inflate(R.menu.favorite_search, menu);
        //Во время Search назад кнопка
        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        };

        MenuItem menuItem = menu.findItem(R.id.F_search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(SearchHint);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<MyFavoriteClass> results = new ArrayList<>();
                for (MyFavoriteClass x : ssdCopy) {
                    //Регистр
                    if (x.questionText.toLowerCase().contains(newText.toLowerCase())) {
                        results.add(x);
                    }
                }

                ((MyFavoriteAdapter) lv.getAdapter()).update(results);
                return true;
            }

        });

        return flagSearch;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void fillString() {
        SearchHint = getString(R.string.J_FA_SearchHint);

    }
}