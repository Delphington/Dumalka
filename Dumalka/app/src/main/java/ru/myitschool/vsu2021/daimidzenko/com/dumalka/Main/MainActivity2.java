package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.navigation.NavigationView;

import io.github.muddz.styleabletoast.StyleableToast;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity implements Dumalka {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;
    private String MODE = null;
    private String G_GAME_IDS, G_QUESTION_IDS, TitleBar_Q, TitleBar_Main, toast, f;
    private ImageView IVheart, btn_ChangeTheme;
    private LinearLayout LinearLove;
    private boolean flagHeart = true;
    private long backPressTime;
    private DbGlbSvc dbGlbSvc;
    private DbGlbSvc.QuestionRecord qRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получение Intent (Тут, не ниже ошибка с передачей в фрагмент)
        MODE = getIntent().getStringExtra("mode");
        G_GAME_IDS = getIntent().getStringExtra("gameId");
        G_QUESTION_IDS = getIntent().getStringExtra("questionId");

        // Скрываем верхнюю строку состояния
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //######### Дефолт ######################################################
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_searchsystem, R.id.nav_sentauthor, R.id.nav_aboutApp)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //######### Дефолт ######################################################

        fillString(); // Получение String из ресурсов

        //developed with love
        LinearLove = findViewById(R.id.developedLove);
        IVheart = findViewById(R.id.developedLoveImage);

        LinearLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagHeart) {
                    IVheart.setImageResource(R.drawable.ic_heart_press);
                    flagHeart = false;
                } else {
                    IVheart.setImageResource(R.drawable.ic_heart_no_press);
                    flagHeart = true;
                }

            }
        });

        // если нажали back, то восстанавливаем последний статус состояния из БД
        // Статус сохраяняли на homeFragment
        if (MODE.equals("back")) {
            MODE = dbGlbSvc.getSystemValue("MODE", "G_MODE");
            G_GAME_IDS = dbGlbSvc.getSystemValue("MODE", "G_GAME_ID");
            G_QUESTION_IDS = dbGlbSvc.getSystemValue("MODE", "G_QUESTION_ID");
        }

        //Изменение Title bar
        if (MODE.equals("favorite")) {
            getSupportActionBar().setTitle(TitleBar_Q);
        } else if (MODE.equals("question")) {
            String gameName = DbGlbSvc.getGameName(Integer.parseInt(G_GAME_IDS));
            getSupportActionBar().setTitle(gameName);
        } else {
            getSupportActionBar().setTitle(TitleBar_Main);
        }

    }

    @Override
    public void fillString() {
        TitleBar_Q = getString(R.string.J_MA2_TB_F);
        toast = getString(R.string.J_MA2_Toast);
        TitleBar_Main = getString(R.string.J_MA2_TB_M);
    }

    /**
     * Передача данных в Home Fragment
     */
    public Bundle getBundleData() {
        Bundle bundle = new Bundle();
        bundle.putString("mode", MODE);
        bundle.putString("gameId", G_GAME_IDS);
        bundle.putString("questionId", G_QUESTION_IDS);
        return bundle;
    }

    //######### Дефолт ######################################################
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    //######### Дефолт ######################################################

    // Смена темы
    public void OnChangeThemeImageButton(View view) {
        f = DbGlbSvc.getSystemValue("CFG", "LightThema");
        btn_ChangeTheme = findViewById(R.id.btn_ChangeTheme);
        if (f.equals("Y")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // темная
            f = "N";
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // светлая
            f = "Y";
        }
        DbGlbSvc.setSystemValue("CFG", "LightThema", f);
    }

    //Системная кнопка назад по повторному выходу
    @Override
    public void onBackPressed() {
        if (backPressTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
            return;
        } else {
            StyleableToast.makeText(MainActivity2.this, toast,
                    Toast.LENGTH_SHORT, R.style.OneMoreBackToast).show();
        }
        backPressTime = System.currentTimeMillis();
    }

}