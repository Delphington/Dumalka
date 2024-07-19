package ru.myitschool.vsu2021.daimidzenko.com.dumalka.AdapterQuestion;

public class MyQuestionClass {
    public int gameId;          // ID игры
    public String questionName; // Относительный номер вопроса
    public int questionId;      // ID вопроса
    public String questionText; // Текст вопроса
    public int isFav;           // 0/1 Фаворит ли?
    public int isCheck;         // 0/1 Правильный ли?

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }


}
