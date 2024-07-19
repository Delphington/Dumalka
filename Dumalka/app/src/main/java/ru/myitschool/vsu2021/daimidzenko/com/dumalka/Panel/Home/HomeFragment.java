package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Panel.Home;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


import io.github.muddz.styleabletoast.StyleableToast;
import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.DialogInternet;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main.History;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Main.MainActivity2;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.PandaBot.Panda;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.PandaBot.PandaTask;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;


public class HomeFragment extends Fragment implements Dumalka {


    private KonfettiView konfettiView = null;
    private Shape.DrawableShape drawableShape = null;
    Drawable drawable;


    private DialogInternet dialogInternet;
    private DbGlbSvc dbGlbSvc;
    private DbGlbSvc.QuestionRecord qRec;
    private String G_MODE, ToastFirst, ToastLast, ToastCorrect, ToastEnterMainQ,
            ToastEnterQ, ToastError, ToastNoneSS, ToastCurrentQ, TextQ;
    private int G_GAME_ID = 0;
    private int G_QUESTION_ID = 0;
    // private  Dialog dialog;

    private ImageView Image_Btn_Main, Image_Btn_1, Image_Btn_2, Image_Btn_3, Image_Btn_4;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    private EditText Edit_Text_Main, Edit_Text_1, Edit_Text_2, Edit_Text_3, Edit_Text_4;

    private int cnt = 100;

    private TextView text_question, Text_View_1, Text_View_2, Text_View_3, Text_View_4;

    private LinearLayout LinearLayout_1, LinearLayout_2, LinearLayout_3, LinearLayout_4, LinearLayout_Main;

    ConstraintLayout Main2Background;

    private String StrEdit_Text_Main, StrEdit_Text_1, StrEdit_Text_2, StrEdit_Text_3, StrEdit_Text_4;
    private boolean flagImitation;

    //------Первоначальная инцилизация конец---------------------------------------------------------------


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        fillString(); //Для заполения Java текста
        dbGlbSvc = new DbGlbSvc(getActivity());

        dialogInternet = new DialogInternet(); //создаем диалог проверьте подключение

//        //Передача данных: cont/new/question
        MainActivity2 activity = (MainActivity2) getActivity();
        Bundle bundle = activity.getBundleData();
        String mode = bundle.getString("mode");
        if (mode == null) {
            mode = "cont";
        }
        G_MODE = mode;
        //Определяем откуда пришли
        try {
            if (mode.equals("new")) {
                G_GAME_ID = dbGlbSvc.createNewGame();
                G_MODE = "question"; // и теперь мы как-бы в режиме вопросов
            } else if (mode.equals("cont")) {
                G_GAME_ID = dbGlbSvc.getLastGame();
                G_MODE = "question"; // и теперь мы как-бы в режиме вопросов
            } else if (mode.equals("question")) {
                G_GAME_ID = Integer.parseInt(bundle.getString("gameId"));
                G_QUESTION_ID = Integer.parseInt(bundle.getString("questionId"));
            } else if (mode.equals("favorite")) {
                G_GAME_ID = Integer.parseInt(bundle.getString("gameId"));
                G_QUESTION_ID = Integer.parseInt(bundle.getString("questionId"));
            } else if (mode.equals("newQuestion")) {
                G_GAME_ID = Integer.parseInt(bundle.getString("gameId"));
                G_QUESTION_ID = 0; // далее создастся новый пустой вопрос
                G_MODE = "question"; // и теперь мы как-бы в режиме вопросов
            } else if (mode.equals("back")) {
                //====================================
                G_MODE = dbGlbSvc.getSystemValue("MODE", "G_MODE");
                G_GAME_ID = Integer.parseInt(dbGlbSvc.getSystemValue("MODE", "G_GAME_ID"));
                G_QUESTION_ID = Integer.parseInt(dbGlbSvc.getSystemValue("MODE", "G_QUESTION_ID"));
                //====================================
            } else {
                // здесь не должны быть
                Toast.makeText(activity, "ERROR mode=" + mode, Toast.LENGTH_SHORT).show();
                throw new Error("MODE [" + mode + "] не поддерживаем");
            }
        } catch (Exception ex) {
            StyleableToast.makeText(activity,
                    ToastError + " " + ex.toString() + mode,
                    Toast.LENGTH_SHORT, R.style.ErrorToast).show();
        }

        //====================================
        dbGlbSvc.setSystemValue("MODE", "G_MODE", G_MODE);
        dbGlbSvc.setSystemValue("MODE", "G_GAME_ID", String.valueOf(G_GAME_ID));
        dbGlbSvc.setSystemValue("MODE", "G_QUESTION_ID", String.valueOf(G_QUESTION_ID));
        //====================================

        //------ Инцилизация ----------------------------------------------
        ImageView btn_back = root.findViewById(R.id.Image_btn_back);
        ImageView btn_next = root.findViewById(R.id.Image_btn_next);
        Image_Btn_Main = root.findViewById(R.id.Image_Btn_Main);
        Image_Btn_1 = root.findViewById(R.id.Image_Btn_1);
        Image_Btn_2 = root.findViewById(R.id.Image_Btn_2);
        Image_Btn_3 = root.findViewById(R.id.Image_Btn_3);
        Image_Btn_4 = root.findViewById(R.id.Image_Btn_4);

        text_question = root.findViewById(R.id.text_question);
        Text_View_1 = root.findViewById(R.id.Text_View_1);
        Text_View_2 = root.findViewById(R.id.Text_View_2);
        Text_View_3 = root.findViewById(R.id.Text_View_3);
        Text_View_4 = root.findViewById(R.id.Text_View_4);

        Edit_Text_Main = root.findViewById(R.id.Edit_Text_Main);
        Edit_Text_1 = root.findViewById(R.id.Edit_Text_1);
        Edit_Text_2 = root.findViewById(R.id.Edit_Text_2);
        Edit_Text_3 = root.findViewById(R.id.Edit_Text_3);
        Edit_Text_4 = root.findViewById(R.id.Edit_Text_4);

        LinearLayout_1 = root.findViewById(R.id.LinearLayout_1);
        LinearLayout_2 = root.findViewById(R.id.LinearLayout_2);
        LinearLayout_3 = root.findViewById(R.id.LinearLayout_3);
        LinearLayout_4 = root.findViewById(R.id.LinearLayout_4);

        Main2Background = root.findViewById(R.id.Main2Background);

        Button Btn_History = (Button) root.findViewById(R.id.Btn_History);
        Button Btn_new_question = (Button) root.findViewById(R.id.Btn_new_q);
        Button Btn_Sent = (Button) root.findViewById(R.id.Btn_Sent);
        Button Btn_NewGame = (Button) root.findViewById(R.id.newGame);

        fSetTextTextView(); // Установка 00 в проценты
        fSetTextEditText(); // Установка "" для editText

        //------------------------------------------------------
        if (G_QUESTION_ID == 0) {
            qRec = dbGlbSvc.getFirstQuestion(G_GAME_ID);
            G_QUESTION_ID = qRec.id;
        } else {
            qRec = dbGlbSvc.getQuestion(G_GAME_ID, G_QUESTION_ID);
        }
        drawQuestion(null);

        /**---------------------------------------------------*/

        /** Меняем фон когда иммитация включенна  */
        flagImitation = DbGlbSvc.getSystemValue("SYS1", "Имитация").equals("Y");
        if (flagImitation) {
            Main2Background.setBackgroundColor(Color.parseColor("#D779EE"));
        }


        /**#######  Konfetti  #############################################################*/

        drawable = ContextCompat.getDrawable(activity, R.drawable.ic_heart);
        drawableShape = new Shape.DrawableShape(drawable, true);
        konfettiView = root.findViewById(R.id.konfettiView);

        /**#################################################################################*/



        //Просмотр вопросов назад
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0;
                String locIds = null;
                if (G_MODE.equals("question")) {
                    id = dbGlbSvc.getPrevQuestionId(G_GAME_ID, G_QUESTION_ID);
                } else if (G_MODE.equals("favorite")) {
                    DbGlbSvc.ids ids = dbGlbSvc.getPrevFavoriteId(G_GAME_ID, G_QUESTION_ID);
                    id = ids.id;
                    locIds = String.valueOf(ids.loc_id);
                } else {
                    // здесь не должны быть
                    Toast.makeText(getActivity(), "здесь не должны быть", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (id == 0) {
                    StyleableToast.makeText(getActivity(), ToastFirst, Toast.LENGTH_SHORT
                            , R.style.FirstQToast).show();
                } else {
                    G_QUESTION_ID = id;
                    if (G_MODE.equals("favorite")) {
                        G_GAME_ID = dbGlbSvc.getGameId(id);
                    }
                    qRec = dbGlbSvc.getQuestion(G_GAME_ID, G_QUESTION_ID);
                    drawQuestion(locIds);
                }
            }
        });
        //Просмотр вопросов вперед
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0;
                String locIds = null;
                if (G_MODE.equals("question")) {
                    id = dbGlbSvc.getNextQuestionId(G_GAME_ID, G_QUESTION_ID);
                } else if (G_MODE.equals("favorite")) {
                    DbGlbSvc.ids ids = dbGlbSvc.getNextFavoriteId(G_GAME_ID, G_QUESTION_ID);
                    id = ids.id;
                    locIds = String.valueOf(ids.loc_id);
                } else {
                    // здесь не должны быть
                    Toast.makeText(getActivity(), "здесь не должны быть", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (id == 0) {
                    StyleableToast.makeText(getActivity(), ToastLast, Toast.LENGTH_SHORT
                            , R.style.LastQToast).show();
                } else {
                    G_QUESTION_ID = id;
                    if (G_MODE.equals("favorite")) {
                        G_GAME_ID = dbGlbSvc.getGameId(id);
                    }
                    qRec = dbGlbSvc.getQuestion(G_GAME_ID, G_QUESTION_ID);
                    drawQuestion(locIds);
                }
            }
        });

        /** ########################################################################### */
//
        //----- История
        Btn_History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), History.class);
                startActivity(intent);
                //Анимация
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //Анимация
            }
        });

        //Новая игра
        Btn_NewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G_GAME_ID = dbGlbSvc.createNewGame();
                qRec = dbGlbSvc.getFirstQuestion(G_GAME_ID);
                G_QUESTION_ID = qRec.id;
                drawQuestion(null);
            }
        });


        //-------- Отправка на проверку начало
        Btn_Sent.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                /** Диаловое окно на интернет */
                if (!isConnected(getContext())) {
                    dialogInternet.show(getFragmentManager(), "dialogInternet"); //Показываем диалог
                } else {

                    StrEdit_Text_1 = Edit_Text_1.getText().toString();
                    StrEdit_Text_2 = Edit_Text_2.getText().toString();
                    StrEdit_Text_3 = Edit_Text_3.getText().toString();
                    StrEdit_Text_4 = Edit_Text_4.getText().toString();
                    StrEdit_Text_Main = Edit_Text_Main.getText().toString();

                    //Проверка что edit Не пустые
                    int count = 1;
                    if (StrEdit_Text_1.trim().length() > 1) {
                        count++;
                    }
                    if (StrEdit_Text_2.trim().length() > 1) {
                        count++;
                    }
                    if (StrEdit_Text_3.trim().length() > 1) {
                        count++;
                    }
                    if (StrEdit_Text_4.trim().length() > 1) {
                        count++;
                    }

                    if ((StrEdit_Text_Main.trim().length() > 1) && count > 2) {

                        //#####################################################
                        // Дебажная распечатка системных параметров
//                        Cursor q0 = dbGlbSvc.getSystemList();
//                        q0.moveToFirst();
//                        while (!q0.isAfterLast()) {
//                            int id_ = q0.getInt(0);
//                            String type_ = q0.getString(1);
//                            String name_ = q0.getString(2);
//                            String value_ = q0.getString(3);
//                            q0.moveToNext();
//                        }
//                        q0.close();
                        //#####################################################

                        int parsSize = 5 + 7;
                        String[] pars = new String[parsSize];
                        pars[0] = StrEdit_Text_Main; // вопрос
                        pars[1] = StrEdit_Text_1;    // ответ 1
                        pars[2] = StrEdit_Text_2;    // ответ 2
                        pars[3] = StrEdit_Text_3;    // ответ 3
                        pars[4] = StrEdit_Text_4;    // ответ 4
                        // Параметры конфигурации передаем стрингами
                        boolean ssNone = true;
                        Cursor q = dbGlbSvc.getSystemForBot();
                        q.moveToFirst();
                        int i = 4;
                        while (!q.isAfterLast()) {
                            i++;
                            pars[i] = q.getString(0);
                            q.moveToNext();
                            if (pars[i].equals("Y")) {
                                ssNone = false;
                            }
                        }
                        q.close();

                        if (ssNone) {
                            StyleableToast.makeText(getActivity(), ToastNoneSS, Toast.LENGTH_SHORT
                                    , R.style.NoneSSToast).show();
                            return;
                        }

//                        progressDialog = new ProgressDialog(getActivity());
//                        progressDialog.getWindow().setBackgroundDrawableResource
//                                (android.R.color.transparent);
//
//                        progressDialog.show();
//                        progressDialog.setContentView(R.layout.progress_dialog);
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

                        Panda.PandaQuestion pQuestion = null;
                        try {

                            Toast.makeText(getActivity(), "Ждите результата....", Toast.LENGTH_SHORT).show();
                            PandaTask task = new PandaTask();
                            task.execute(pars);
                            pQuestion = task.get();

                            // Сохранение в БД вопроса
                            qRec.q = StrEdit_Text_Main;
                            qRec.a1 = StrEdit_Text_1;
                            qRec.a2 = StrEdit_Text_2;
                            qRec.a3 = StrEdit_Text_3;
                            qRec.a4 = StrEdit_Text_4;
                            qRec.a1_res = pQuestion.answers[0].probability;
                            qRec.a2_res = pQuestion.answers[1].probability;
                            qRec.a3_res = pQuestion.answers[2].probability;
                            qRec.a4_res = pQuestion.answers[3].probability;
                            qRec.a1_marker = pQuestion.answers[0].marker;
                            qRec.a2_marker = pQuestion.answers[1].marker;
                            qRec.a3_marker = pQuestion.answers[2].marker;
                            qRec.a4_marker = pQuestion.answers[3].marker;
                            qRec.best = 0; //Лучший руками
                            qRec.is_bot = 1; // true
                            dbGlbSvc.updateQuestion(qRec);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        drawQuestion(null);


                    } else {
                        //Проверка что edit Не пустые
                        count = 0;
                        if (StrEdit_Text_Main.trim().length() < 1) {

                            StyleableToast.makeText(getActivity(), ToastEnterMainQ, Toast.LENGTH_SHORT
                                    , R.style.EnterQuestionToast).show();
                        }
                        if (StrEdit_Text_Main.trim().length() > 1) {

                            if (StrEdit_Text_1.trim().length() > 1) {
                                count++;
                            }
                            if (StrEdit_Text_2.trim().length() > 1) {
                                count++;
                            }
                            if (StrEdit_Text_3.trim().length() > 1) {
                                count++;
                            }
                            if (StrEdit_Text_4.trim().length() > 1) {
                                count++;
                            }
                            if (count <= 1) {
                                StyleableToast.makeText(getActivity(), ToastEnterQ, Toast.LENGTH_SHORT
                                        , R.style.EnterAnswerToast).show();
                            }
                        }
                    }
                }
            }
        });

        //====== Пометка правильного ответа начало ============
        Text_View_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBest(1);
            }
        });

        Text_View_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBest(2);
            }
        });

        Text_View_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBest(3);
            }
        });

        Text_View_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBest(4);
            }
        });

        //====== Новый вопрос начало ========
        Btn_new_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (qRec.is_bot == 0) {
                    StyleableToast.makeText(activity, ToastCurrentQ,
                            Toast.LENGTH_SHORT, R.style.СurrentQuestionToast).show();
                    return;
                }
                fSetTextEditText(); //Обнуление TextEdit
                fSetTextTextView(); //Обнуление %

                LinearLayout_1.setBackgroundResource(R.drawable.layout_style_answers);
                LinearLayout_2.setBackgroundResource(R.drawable.layout_style_answers);
                LinearLayout_3.setBackgroundResource(R.drawable.layout_style_answers);
                LinearLayout_4.setBackgroundResource(R.drawable.layout_style_answers);

                G_QUESTION_ID = dbGlbSvc.createNullQuestion(G_GAME_ID);
                qRec = dbGlbSvc.getQuestion(G_GAME_ID, G_QUESTION_ID);
                drawQuestion(null);

                try {
                } catch (Exception e) {
                }
            }
        });


        //====Нажатие на микрофоны начало=====================
        Image_Btn_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // запускает действие, которое будет запрашивать у
                // пользователя речь и отправлять ее через распознаватель речи.
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ru-RU");
                Locale locale = new Locale("RU");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                int a = 0;
                cnts(a);
                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                }
            }
        });
        //=================================================
        Image_Btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ru-RU");
                Locale locale = new Locale("RU");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                int a = 1;
                cnts(a);
                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                }
            }
        });
        //====================================
        Image_Btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ru-RU");
                Locale locale = new Locale("RU");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                int a = 2;
                cnts(a);
                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                }
            }
        });
        //====================================
        Image_Btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ru-RU");
                Locale locale = new Locale("RU");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                int a = 3;
                cnts(a);
                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                }
            }
        });
        //====================================
        Image_Btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ru-RU");
                Locale locale = new Locale("RU");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                int a = 4;
                cnts(a);
                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                }
            }
        });
        return root;
    }

    //-----Речь перевод в текст -------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_SPEECH_INPUT) && (resultCode == RESULT_OK && data != null)) {
            if (cnt == 0) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Edit_Text_Main.setText(Objects.requireNonNull(result).get(0));
            }
            if (cnt == 1) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Edit_Text_1.setText(Objects.requireNonNull(result).get(0));
            }

            if (cnt == 2) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Edit_Text_2.setText(Objects.requireNonNull(result).get(0));
            }
            if (cnt == 3) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Edit_Text_3.setText(Objects.requireNonNull(result).get(0));

            }
            if (cnt == 4) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Edit_Text_4.setText(Objects.requireNonNull(result).get(0));
            }
            try {
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void fillString() {
        ToastFirst = getString(R.string.J_HF_ToastFirst);
        ToastLast = getString(R.string.J_HF_ToastLast);
        ToastCorrect = getString(R.string.J_HF_ToastCorrect);
        ToastEnterMainQ = getString(R.string.J_HF_ToastEnterMainQ);
        ToastEnterQ = getString(R.string.J_HF_ToastEnterQ);
        ToastError = getString(R.string.J_HF_ToastError);
        ToastNoneSS = getString(R.string.J_HF_ToastNoneSS);
        ToastCurrentQ = getString(R.string.J_HF_ToastCurrentQ);
        TextQ = getString(R.string.J_HF_TextQ);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Обнуление поля для Вопрсов
    private void fSetTextEditText() {
        Edit_Text_Main.setText("");
        Edit_Text_1.setText("");
        Edit_Text_2.setText("");
        Edit_Text_3.setText("");
        Edit_Text_4.setText("");
    }

    // Обнуление процентов результатов
    private void fSetTextTextView() {
        Text_View_2.setText("0,00");
        Text_View_3.setText("0,00");
        Text_View_4.setText("0,00");
        Text_View_1.setText("0,00");
    }

    //Для микрофона
    private int cnts(int a) {
        return cnt = a;
    }


    //
    private void drawQuestion(String locQuestionName) {
        if (locQuestionName == null) {
            text_question.setText(TextQ + " " + qRec.name);
        } else {
            text_question.setText(TextQ + " " + locQuestionName);
        }
        Edit_Text_Main.setText(qRec.q);
        Edit_Text_1.setText(qRec.a1);
        Edit_Text_2.setText(qRec.a2);
        Edit_Text_3.setText(qRec.a3);
        Edit_Text_4.setText(qRec.a4);
        Text_View_1.setText(String.format("%.2f", qRec.a1_res));
        Text_View_2.setText(String.format("%.2f", qRec.a2_res));
        Text_View_3.setText(String.format("%.2f", qRec.a3_res));
        Text_View_4.setText(String.format("%.2f", qRec.a4_res));
        drawQuestionColor();
    }

    // Отмечаем Финальный результат руками
    private void setBest(int num) {
        if (qRec.is_bot == 0) {
            return;
        }
        if (qRec.best == num) {
            qRec.best = 0;
        } else {
            qRec.best = num;
            Konfetti();
        //    FToast();

        }
        dbGlbSvc.updateQuestion(qRec);
        drawQuestionColor();
    }

    // Отмечаем Финальный результат
    private void drawQuestionColor() {
        LinearLayout_1.setBackgroundResource(R.drawable.layout_style_answers);
        LinearLayout_2.setBackgroundResource(R.drawable.layout_style_answers);
        LinearLayout_3.setBackgroundResource(R.drawable.layout_style_answers);
        LinearLayout_4.setBackgroundResource(R.drawable.layout_style_answers);

        if (qRec.a1_marker != null && qRec.a1_marker.equals("Y")) {
            LinearLayout_1.setBackgroundResource(R.drawable.layout_style_answers_end);
        }
        if (qRec.a2_marker != null && qRec.a2_marker.equals("Y")) {
            LinearLayout_2.setBackgroundResource(R.drawable.layout_style_answers_end);
            //LinearLayout_2.setBackgroundColor(Color.parseColor("#0bda51"));
        }
        if (qRec.a3_marker != null && qRec.a3_marker.equals("Y")) {
            LinearLayout_3.setBackgroundResource(R.drawable.layout_style_answers_end);
        }
        if (qRec.a4_marker != null && qRec.a4_marker.equals("Y")) {
            LinearLayout_4.setBackgroundResource(R.drawable.layout_style_answers_end);
        }
        if (qRec.best == 1) {
            LinearLayout_1.setBackgroundResource(R.drawable.layout_style_final_answer);

        } else if (qRec.best == 2) {
            LinearLayout_2.setBackgroundResource(R.drawable.layout_style_final_answer);

        } else if (qRec.best == 3) {
            LinearLayout_3.setBackgroundResource(R.drawable.layout_style_final_answer);

        } else if (qRec.best == 4) {
            LinearLayout_4.setBackgroundResource(R.drawable.layout_style_final_answer);
        }
    }


    private void FToast(){
        StyleableToast.makeText(getActivity(), ToastCorrect, Toast.LENGTH_SHORT
                , R.style.CorrectAnswerToast).show();
    }

//Когда руками подтвержаем правильный ответ Конфети
    private void Konfetti() {
        EmitterConfig emitterConfig = new Emitter(3, TimeUnit.SECONDS).perSecond(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .angle(Angle.BOTTOM)
                        .spread(Spread.ROUND)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 15f)
                        .timeToLive(10L)
                        .spread(80)
                        .fadeOutEnabled(true)
                        .position(new Position.Relative(0.0, 0.0).between(new Position.Relative(1.0, 0.0)))
                        .build()
        );
    }


    //Диаловое окно для кнопки отправить с интернетом
    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) | (mobileConn != null &&
                mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

}