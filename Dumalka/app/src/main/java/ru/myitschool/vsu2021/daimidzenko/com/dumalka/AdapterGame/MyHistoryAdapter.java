package ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterGame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main.History;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main.Question;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;

public class MyHistoryAdapter extends ArrayAdapter<MyHistoryClass> implements Dumalka {

    private Context myContext;
    private DbGlbSvc dbGlbSvc;
    private DbGlbSvc.QuestionRecord qRec;
    private ArrayList<MyHistoryClass> arr;
    private String HA_NumberGame, HA_AllQ, HA_CorrectQ, HA_WrongQ, Toast_txt;

    private History activity = (History) getContext();

    public MyHistoryAdapter(Context context, ArrayList<MyHistoryClass> arr) {
        super(context, R.layout.adapter_history_item, arr);
        myContext = context;
        this.arr = arr;
    }

    // Метод для покdаза
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MyHistoryClass myHistory = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_history_item, null);
        }


        fillString(); //Заполнения Java Text

        dbGlbSvc = new DbGlbSvc(myContext);

        //инцилизируем и устанавливаем данные
        TextView allQuestions = convertView.findViewById(R.id.allQuestions);
        allQuestions.setText(HA_AllQ + " " + myHistory.allQuestion);
//        //Подсвечиваем если нт вопросов
//        if (myHistory.allQuestion == 0) {
//            allQuestions.setTextAppearance(myContext, R.style.ZeroQuestion);
//        }

        TextView rightQuestions = convertView.findViewById(R.id.rightQuestions);
        rightQuestions.setText(HA_CorrectQ + " " + myHistory.trueQuestion);

        TextView falseQuestions = convertView.findViewById(R.id.falseQuestions);
        falseQuestions.setText(HA_WrongQ + " " + myHistory.falseQuestion);

        TextView gameNumber = convertView.findViewById(R.id.gameNumber);
        gameNumber.setText(myHistory.gameName);






        //-------------------------------------------
        ImageView image = (ImageView) convertView.findViewById(R.id.ButtonDelete);
        LinearLayout liner = convertView.findViewById(R.id.Liner);
        ImageView Game_edit = convertView.findViewById(R.id.Game_edit);
        LinearLayout LinearEdit = convertView.findViewById(R.id.LinearEdit);


        //Изменение Имя игры По ImageView
        Game_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Создание диалога для изменения
                AlertDialog.Builder alert = new AlertDialog.Builder(myContext);
                View mView = View.inflate(myContext, R.layout.dialog_edit_game, null);
                EditText changeText = mView.findViewById(R.id.editTextDialog);
                Button Btn_OnBack = mView.findViewById(R.id.Btn_OnBack);
                Button Btn_onSave = mView.findViewById(R.id.Btn_onSave);
                changeText.setText(gameNumber.getText().toString()); // Текст который был дефолт
                alert.setView(mView);
                AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog_edit_game);
                alertDialog.show();

                Btn_OnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                Btn_onSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newGameName = changeText.getText().toString();
                        int leng = changeText.getText().toString().trim().length();

                        if (leng < 1) {
                            StyleableToast.makeText(activity,
                                    "Название должно содержать минимум 1 символ",
                                    Toast.LENGTH_SHORT, R.style.MinNameToast).show();

                        } else {

                            //замена в позиции
                            MyHistoryClass s = arr.get(position);
                            s.gameName = newGameName;
                            arr.set(position, s);
                            DbGlbSvc.setNewGameName(s.gameId, newGameName);
                            gameNumber.setText(newGameName);
                            alertDialog.dismiss();
                        }

                    }
                });
            }
        });


        //Изменение Имя игры По Layout
        LinearEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Создание диалога для изменения
                AlertDialog.Builder alert = new AlertDialog.Builder(myContext);
                View mView = View.inflate(myContext, R.layout.dialog_edit_game, null);
                EditText changeText = mView.findViewById(R.id.editTextDialog);
                Button Btn_OnBack = mView.findViewById(R.id.Btn_OnBack);
                Button Btn_onSave = mView.findViewById(R.id.Btn_onSave);
                changeText.setText(gameNumber.getText().toString()); // Текст который был дефолт
                alert.setView(mView);
                AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog_edit_game);
                alertDialog.show();

                //Возвращаемся обратно
                Btn_OnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                //Сохранения
                Btn_onSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newGameName = changeText.getText().toString();
                        int leng = changeText.getText().toString().length();

                        if (leng < 1) {
                            Toast.makeText(myContext, Toast_txt, Toast.LENGTH_SHORT).show();
                        } else {
                            MyHistoryClass s = arr.get(position);
                            s.gameName = newGameName;
                            arr.set(position, s);
                            DbGlbSvc.setNewGameName(s.gameId, newGameName);
                            gameNumber.setText(newGameName);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });


        // Удаление элемента
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ///------------------------------------------
                MyHistoryClass objGame = arr.get(position);
                int gameId = objGame.gameId;
                DbGlbSvc.deleteGame(gameId); //Удаление из БД
                ///------------------------------------------
                arr.remove(position); //удаление из listview
                notifyDataSetChanged(); //Обновления адаптера

            }
        });

        //Переход на активность с вопросами
        liner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyHistoryClass objGame = arr.get(position);
                int gameId = objGame.gameId;
                String gameIds = String.valueOf(gameId);
                Intent intent = new Intent(myContext, Question.class);
                intent.putExtra("gameId", gameIds);
                myContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return convertView;
    }

    @Override
    public void fillString() {
        HA_NumberGame = activity.getString(R.string.J_HA_NumberG);
        HA_AllQ = activity.getString(R.string.J_HA_AllQ);
        HA_CorrectQ = activity.getString(R.string.J_HA_CorrectQ);
        HA_WrongQ = activity.getString(R.string.J_HA_WrongQ);
        Toast_txt = activity.getString(R.string.J_HA_MinTextToast);
    }


}
