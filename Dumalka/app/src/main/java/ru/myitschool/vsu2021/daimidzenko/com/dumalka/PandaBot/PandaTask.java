package ru.myitschool.vsu2021.daimidzenko.com.dumalka.PandaBot;

import android.database.Cursor;
import android.os.AsyncTask;

import java.io.IOException;

import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;

/**
 * **********************************************************************
 * 2022.02 -= PANDA BOT =- (версия 1.0) Интеллектуальная собственность
 * С уважением к любителям викторин!
 * Вход:
 * - Вопрос
 * - Любое количество вариантов ответов (обычно это 3 илт 4)
 * Выход:
 * - Каждый ответ получает расчетную вероятность правильности
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * // Входящие данные
 * int idx = 1;
 * String question = "В каком музее находится картина «Богатыри» Васнецова?";
 * String[] answers = new String[3];
 * answers[0] = "Третьяковская галерея";
 * answers[1] = "Музей истории тридевятого царства";
 * answers[2] = "Богатырский пассаж";
 * // Получение результата
 * Panda.PandaQuestion result = Panda.runner(idx, question, answers);
 * // Вывод результата (если надо)
 * Panda.displayConsoleResult(result);
 * **********************************************************************
 **/

public class PandaTask extends AsyncTask<String, Void, Panda.PandaQuestion> {

    public static String G_QUESTION;
    public static String[] G_ANSWER;

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }

    @Override
    protected Panda.PandaQuestion doInBackground(String... str) {
        // Конфигурация бота

     //   Cursor q = DbGlbSvc.getSystemForBot();


        Panda.PandaSet sets = new Panda.PandaSet();

        G_ANSWER = new String[str.length - 1];
        G_QUESTION = str[0];
        for (int i = 1; i < 5; i++) {
            G_ANSWER[i-1] = str[i];
        }

        sets.imitator = str[5].equals("Y");
        sets.ssGoogle = str[6].equals("Y");
        sets.ssYahoo = str[7].equals("Y");
        sets.ssYandex = str[8].equals("Y");
        sets.ssSwisscows = str[9].equals("Y");
        sets.ssExcite = str[10].equals("Y");
        sets.ssAol = str[11].equals("Y");

        Panda.PandaQuestion pQuestion = null;
        try {
            // Работает мой бот
            pQuestion = Panda.runner(G_QUESTION, G_ANSWER, sets);
            return pQuestion;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
