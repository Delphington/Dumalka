package ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterQuestion;

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
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main.Question;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main.MainActivity2;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;


public class MyQuestionAdapter extends ArrayAdapter<MyQuestionClass> implements Dumalka {

    private Context myContext;
    private DbGlbSvc dbGlbSvc;
    private DbGlbSvc.QuestionRecord qRec;
    private String ToastAdd, ToastNoAdd, ToastIsCheck;
    private ArrayList<MyQuestionClass> arr;
    private Question activity = (Question) getContext();


    public MyQuestionAdapter(Context context, ArrayList<MyQuestionClass> arr) {
        super(context, R.layout.adapter_question_item, arr);
        this.myContext = context;
        this.arr = arr;
    }

    //Поиск
    public void update(ArrayList<MyQuestionClass> results) {
        arr.clear();
        arr.addAll(results);
        notifyDataSetChanged();
    }


    //Отрисовка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MyQuestionClass QuestionClass = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_question_item, null);
        }
        fillString();

        dbGlbSvc = new DbGlbSvc(myContext);

        TextView textQuestion = convertView.findViewById(R.id.textQuestion);
        textQuestion.setText(QuestionClass.questionText);

        ImageView ChoiceFavorite = convertView.findViewById(R.id.ChoiceFavorite);

        TextView NumberOfQuestion = convertView.findViewById(R.id.Txt_NumberOfQuestion);
        NumberOfQuestion.setText(QuestionClass.questionName);
        LinearLayout TextQLayout = convertView.findViewById(R.id.TextQLayout);

        ImageView check = convertView.findViewById(R.id.check);

        LinearLayout ll = convertView.findViewById(R.id.content);

        ImageView ButtonDeleteQuestion = convertView.findViewById(R.id.ButtonDeleteQuestion);
        NumberOfQuestion.setBackgroundResource(R.drawable.desing_question_number);

        //Установка правильности ответа руками
        if (QuestionClass.isCheck == 1) {
            check.setBackgroundResource(R.drawable.ic_favorite_check); //
        }

        if (QuestionClass.isFav == 0) {
            ChoiceFavorite.setBackgroundResource(R.drawable.ic_favorite_no_activ);
            ll.setBackgroundResource(R.color.AQ_A_backgroundLinear);


        } else {
            ChoiceFavorite.setBackgroundResource(R.drawable.ic_favorite_activ);
            ll.setBackgroundResource(R.color.AQ_A_backgroundLinearFavorite);
        }

        ChoiceFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int isFav = QuestionClass.isFav; //Установка Favorite
                ///------------------------------------------
                MyQuestionClass objQuestion = arr.get(position);
                int questionId = objQuestion.questionId; //Установка Game id
                dbGlbSvc.setFavorite(questionId);
                ///------------------------------------------
                //Установка Favorite
                if (isFav == 0) {
                    ChoiceFavorite.setBackgroundResource(R.drawable.ic_favorite_activ);
                    ll.setBackgroundResource(R.color.AQ_A_backgroundLinearFavorite);
                    QuestionClass.isFav = 1;
                    StyleableToast.makeText(myContext, ToastAdd, Toast.LENGTH_SHORT
                            , R.style.FavoriteToast).show();

                }  //Удаление Favorite
                else {
                    StyleableToast.makeText(myContext, ToastNoAdd, Toast.LENGTH_SHORT
                            , R.style.DeleteFavoriteToast).show();
                    QuestionClass.isFav = 0;
                    ChoiceFavorite.setBackgroundResource(R.drawable.ic_favorite_no_activ);
                    ll.setBackgroundResource(R.color.AQ_A_backgroundLinear);
                }
            }
        });

        // Удаление элемента
        ButtonDeleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///------------------------------------------
                MyQuestionClass objQuestion = arr.get(position);
                int questionId = objQuestion.questionId;
                dbGlbSvc.deleteQuestion(questionId);
                ///------------------------------------------
                arr.remove(position); //удаление из listview
                notifyDataSetChanged(); //Обновления адаптера
            }
        });

        //Переход на MainActivity2 Через TextView
        textQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyQuestionClass objQuestion = arr.get(position);
                int gameId = objQuestion.gameId;
                String gameIds = String.valueOf(gameId);
                int questionId = objQuestion.questionId;
                String questionIds = String.valueOf(questionId);

                Intent intent = new Intent(myContext, MainActivity2.class);
                intent.putExtra("mode", "question");
                intent.putExtra("gameId", gameIds);
                intent.putExtra("questionId", questionIds);
                myContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //Переход на MainActivity2 Через Layout
        TextQLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyQuestionClass objQuestion = arr.get(position);
                int gameId = objQuestion.gameId;
                String gameIds = String.valueOf(gameId);
                int questionId = objQuestion.questionId;
                String questionIds = String.valueOf(questionId);

                Intent intent = new Intent(myContext, MainActivity2.class);
                intent.putExtra("mode", "question");
                intent.putExtra("gameId", gameIds);
                intent.putExtra("questionId", questionIds);
                myContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

        //Пояснение галочки
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QuestionClass.isCheck == 1) {
                    StyleableToast.makeText(myContext, ToastIsCheck, Toast.LENGTH_SHORT, R.style.ThisQRightToast).show();
                }
            }
        });

        return convertView;
    }

    @Override
    public void fillString() {
        ToastAdd = activity.getString(R.string.J_QA_ToastAdd);
        ToastNoAdd = activity.getString(R.string.J_QA_ToastNoAdd);
        ToastIsCheck = activity.getString(R.string.J_QA_ToastIsCheck);
    }

}
