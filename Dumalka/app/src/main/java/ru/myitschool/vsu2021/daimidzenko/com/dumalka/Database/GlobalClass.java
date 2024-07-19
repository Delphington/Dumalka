package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database;

import android.app.Application;

//// Создание глобальной переменной
public class GlobalClass extends Application {
    private int G_GAME_ID;
    private int G_QUESTION_ID;

    public void setGameId(int id) {
        this.G_GAME_ID = id;
    }
    public int getGameId() {
        return this.G_GAME_ID;
    }

    public void setuestionId(int id) {
        this.G_QUESTION_ID = id;
    }
    public int getQuestionId() {
        return this.G_QUESTION_ID;
    }
}
