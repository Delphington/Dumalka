package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;

import ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterQuestion.MyQuestionAdapter;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterQuestion.MyQuestionClass;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;


public class Question extends AppCompatActivity implements Dumalka {

    private DbGlbSvc dbGlbSvc;
    private DbGlbSvc.QuestionRecord qRec;
    private String QA_NumberGame;
    private MyQuestionAdapter adapter;
    private int gameId;
    private Dialog dialog;
    private ListView lv;
    private String SearchHint;
    private boolean noQuestionFlag = true;
    private boolean flagSearch =true;


    ArrayList<MyQuestionClass> ssd = new ArrayList<>();
    ArrayList<MyQuestionClass> ssdCopy = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //---- Убераем верхнюю строку состояния
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        //-------------------------------------------
        setContentView(R.layout.activity_question);
        fillString();
        dbGlbSvc = new DbGlbSvc(Question.this);
        // Получение id от прошлого активити
        String gameIds = getIntent().getStringExtra("gameId");
        gameId = Integer.parseInt(gameIds);
        //-------------------------------------------
        // Создание Title бара
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String GameName = DbGlbSvc.getGameName(gameId);
        setTitle(GameName);

////Начинаме -----------=============================================
        /**##################################################################*/
        lv = (ListView) findViewById(R.id.listView1);
        //цвет между элементами listview
        lv.setDivider(new ColorDrawable(getApplication().getResources().getColor(R.color.AQ_Background)));
        lv.setDividerHeight(1);

        //Установка adapter
        adapter = new MyQuestionAdapter(this, ssd);
        lv.setAdapter(adapter);
        /**---------------------------------------------------*/

        //Заполнения массива для ListView
        Cursor q = DbGlbSvc.getQuestionList(gameId);
        q.moveToFirst();
        while (!q.isAfterLast()) {
            noQuestionFlag = false; // Если вопросов нет
            MyQuestionClass myQuestionItem = new MyQuestionClass();
            myQuestionItem.gameId = gameId;
            myQuestionItem.questionId = q.getInt(0);
            myQuestionItem.questionName = q.getString(1);
            myQuestionItem.questionText = q.getString(2);
            myQuestionItem.isFav = q.getInt(3);
            myQuestionItem.isCheck = q.getInt(4);
            ssd.add(myQuestionItem);
            ssdCopy.add(myQuestionItem);
            adapter.notifyDataSetInvalidated();
            q.moveToNext();
        }

        q.close();
        //скрываем системные кнокпи
        //  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //Создания диалога
        dialog = new Dialog(Question.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// скрывваем заголовок
        dialog.setContentView(R.layout.dialog_no_quesion);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //Прозрачный фон
        dialog.setCancelable(false); // Диалог нельзя закрыть кнопкой назад

        // если нет вопросов, то показываем диалог
        if (noQuestionFlag) {
            dialog.show();
        }
    }

    /**
     * ##################################################################
     */
    //Диалог создать вопрос
    public void onNewQuestion(View view) {
        Intent intent = new Intent(Question.this, MainActivity2.class);
        intent.putExtra("mode", "newQuestion");
        intent.putExtra("gameId", String.valueOf(gameId));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    //Диалог создать вопрос
    public void onBackHistoryActivity(View view) {
        Intent intent = new Intent(Question.this, History.class);
        startActivity(intent);
        finish();
    }

    //Системная кнопка назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    //Завершение и анимация
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //Меню Search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (noQuestionFlag){
            flagSearch = false;
        }

        getMenuInflater().inflate(R.menu.question_search, menu);

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

        MenuItem menuItem = menu.findItem(R.id.Q_search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(SearchHint);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<MyQuestionClass> results = new ArrayList<>();
                for (MyQuestionClass x : ssdCopy) {
                    //регистр 0
                    if (x.questionText.toLowerCase().contains(newText.toLowerCase())) {
                        results.add(x);
                    }
                }
                ((MyQuestionAdapter) lv.getAdapter()).update(results);
                return true;
            }
        });

        return flagSearch;
    }

    // Стрелка назад на Action Bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(Question.this, History.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void fillString() {
        QA_NumberGame = getString(R.string.J_QA_NumberGame);
        SearchHint = getString(R.string.J_QA_SearchHint);
    }
}