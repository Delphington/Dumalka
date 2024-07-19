package ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterFavorite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import io.github.muddz.styleabletoast.StyleableToast;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main.Favorite;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main.MainActivity2;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;

public class MyFavoriteAdapter extends ArrayAdapter<MyFavoriteClass> implements Dumalka {

    private Context myContext;
    private DbGlbSvc dbGlbSvc;
    private DbGlbSvc.QuestionRecord qRec;
    private ArrayList<MyFavoriteClass> arr;
    private String ToastCheck;
    private Favorite activity = (Favorite) getContext();

    public MyFavoriteAdapter(Context context, ArrayList<MyFavoriteClass> arr) {
        super(context, R.layout.adapter_favorite_question_item, arr);
        myContext = context;
        this.arr = arr;
    }

    //Поиск
    public void update(ArrayList<MyFavoriteClass> results) {
        arr.clear();
        arr.addAll(results);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyFavoriteClass FavoriteClass = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_favorite_question_item, null);
        }

        fillString();

        dbGlbSvc = new DbGlbSvc(myContext);

        TextView textFavorite = convertView.findViewById(R.id.textFavoriteQuestion);
        textFavorite.setText(FavoriteClass.questionText);

        TextView numFavorite = convertView.findViewById(R.id.Txt_NumberOfQ);
        numFavorite.setText(FavoriteClass.questionNum);

        ImageView Fa_ChoiceFavorite = convertView.findViewById(R.id.Fa_ChoiceFavorite);

        ImageView Favorite_check =convertView.findViewById(R.id.Favorite_check);
        LinearLayout LinearText  = convertView.findViewById(R.id.LinearText);


        if(FavoriteClass.isCheck == 1){
            Favorite_check.setBackgroundResource(R.drawable.ic_favorite_check);
        }


        //Переход на MainActivity через Layout
        LinearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFavoriteClass objQuestion = arr.get(position);
                int gameId = objQuestion.gameId;
                String gameIds = String.valueOf(gameId);
                int questionId = objQuestion.questionId;
                String questionIds = String.valueOf(questionId);
                Intent intent = new Intent(myContext, MainActivity2.class);
                intent.putExtra("mode", "favorite");
                intent.putExtra("gameId", gameIds);
                intent.putExtra("questionId", questionIds);
                myContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //Переход на MainActivity через text
        textFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyFavoriteClass objQuestion = arr.get(position);
                int gameId = objQuestion.gameId;
                String gameIds = String.valueOf(gameId);
                int questionId = objQuestion.questionId;
                String questionIds = String.valueOf(questionId);

                Intent intent = new Intent(myContext, MainActivity2.class);
                intent.putExtra("mode", "favorite");
                intent.putExtra("gameId", gameIds);
                intent.putExtra("questionId", questionIds);
                myContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        //Удаление элемента
        Fa_ChoiceFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///------------------------------------------
                MyFavoriteClass objFavorite = arr.get(position);
                int questionId = objFavorite.questionId;
                DbGlbSvc.setFavorite(questionId);
                ///------------------------------------------
                arr.remove(position); //удаление из listview
                notifyDataSetChanged(); //Обновления адаптера
            }
        });

        //Пояснение галочки
        Favorite_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FavoriteClass.isCheck == 1) {
                    StyleableToast.makeText(myContext, ToastCheck,
                            Toast.LENGTH_SHORT, R.style.ThisQRightToast).show();
                }
            }
        });


        return convertView;
    }
    @Override
    public void fillString(){
        ToastCheck = activity.getString(R.string.J_JA_ToastCheck);
    }
}
