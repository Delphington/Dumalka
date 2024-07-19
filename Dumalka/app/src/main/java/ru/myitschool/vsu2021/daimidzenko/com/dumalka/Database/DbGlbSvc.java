package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

public class DbGlbSvc {

    private static DatabaseHelper mDBHelper;
    private static SQLiteDatabase mDb;

    //Сюда внес, что бы в каждом JAVA классе не прописывать
    public DbGlbSvc(Context activity) {
        /** ##### База данных ####################### */
        mDBHelper = new DatabaseHelper(activity);
        try {
            this.mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            this.mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        /** ######################################### */
    }

    public static class ids {
        public int id;
        public int loc_id; // устарел. не будем использовать. всегда = 0

        public ids(int id, int loc_id) {
            this.id = id;
            this.loc_id = loc_id;
        }
    }

    /**
     * Record Question
     **/
    public static class QuestionRecord {
        public int id;
        public int game_id;
        public int loc_id;
        public String name;
        public String q;
        public int is_fav;
        public int is_last;
        public int is_bot;
        public String a1;
        public String a2;
        public String a3;
        public String a4;
        public Double a1_res;
        public Double a2_res;
        public Double a3_res;
        public Double a4_res;
        public String a1_marker;
        public String a2_marker;
        public String a3_marker;
        public String a4_marker;
        public int best;

        // Конструктор
        public QuestionRecord(int gameId, int questionId) {
            String query = "UPDATE GAME SET IS_LAST = 0 WHERE IS_LAST = 1";
            mDb.execSQL(query);
            query = "UPDATE GAME SET IS_LAST = 1 WHERE ID = ?";
            mDb.execSQL(query, new String[]{String.valueOf(gameId)});
            query = "UPDATE QUESTION SET IS_LAST = 0 WHERE IS_LAST = 1";
            mDb.execSQL(query);
            query = "UPDATE QUESTION SET IS_LAST = 1 WHERE ID = ?";
            mDb.execSQL(query, new String[]{String.valueOf(questionId)});
            //Из бд данные в Java
            Cursor q = mDb.rawQuery("SELECT id, game_id, loc_id, name, q, is_fav, is_last, is_bot, a1, a2, a3, a4, " +
                    "a1_res, a2_res, a3_res, a4_res, a1_marker, a2_marker, a3_marker, a4_marker, best " +
                    "FROM QUESTION WHERE GAME_ID = ? AND id = ?", new String[]{String.valueOf(gameId), String.valueOf(questionId)});
            q.moveToFirst();
            while (!q.isAfterLast()) {
                this.id = q.getInt(0);
                this.game_id = q.getInt(1);
                this.loc_id = q.getInt(2);
                this.name = q.getString(3);
                this.q = q.getString(4);
                this.is_fav = q.getInt(5);
                this.is_last = q.getInt(6);
                this.is_bot = q.getInt(7);
                this.a1 = q.getString(8);
                this.a2 = q.getString(9);
                this.a3 = q.getString(10);
                this.a4 = q.getString(11);
                this.a1_res = q.getDouble(12);
                this.a2_res = q.getDouble(13);
                this.a3_res = q.getDouble(14);
                this.a4_res = q.getDouble(15);
                this.a1_marker = q.getString(16);
                this.a2_marker = q.getString(17);
                this.a3_marker = q.getString(18);
                this.a4_marker = q.getString(19);
                this.best = q.getInt(20);
                break;
            }
            q.close();
        }
    }

    public static int createNewGame() {
        // удалим все левые вопросы. это чистка возможного мусора
        //mDb.delete("QUESTION", "(Q IS NULL) OR Q = ''", null);
        mDb.execSQL("DELETE FROM QUESTION " +
                "WHERE ((Q IS NULL) OR Q = '') " +
                "AND (GAME_ID NOT IN (SELECT ID FROM GAME WHERE IS_LAST = 1))");

        int id = 0;
        String query = "UPDATE GAME SET IS_LAST = 0";
        mDb.execSQL(query);

        //Insert и получаем ID игры
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", "game");
        contentValues.put("IS_LAST", 1);
        long id_ = mDb.insert("GAME", null, contentValues);

        //Update поля NAME = "Игра ID"
        ContentValues values = new ContentValues();
        values.put("NAME", "Игра " + id_);
        mDb.update("GAME", values, "ID = ?", new String[]{String.valueOf(id_)});

        id = (int) id_;
        return id;
    }

    //id последний игры
    public static int getLastGame() {
        int id = 0;
        // LIMIT на всякий случай
        Cursor q = mDb.rawQuery("SELECT g.ID FROM GAME g WHERE g.IS_LAST = 1 LIMIT 1", null);
        q.moveToFirst();
        while (!q.isAfterLast()) {
            id = q.getInt(0);
            break;
        }
        q.close();
        if (id == 0) {
            // Попробуем установить LAST у существуюшей другой игры
            id = setLastMaxGame();
        }
        if (id == 0) {
            id = createNewGame();
        }
        return id;
    }

    public static int setLastMaxGame() {
        int id = 0;

        String query = "UPDATE GAME SET IS_LAST = 0 WHERE IS_LAST = 1";
        mDb.execSQL(query);

        query = "UPDATE GAME SET IS_LAST = 1 WHERE ID = (SELECT MAX(g.ID) FROM GAME g)";
        mDb.execSQL(query);

        Cursor q = mDb.rawQuery("SELECT g.ID FROM GAME g WHERE g.IS_LAST = 1 LIMIT 1", null);
        q.moveToFirst();
        while (!q.isAfterLast()) {
            id = q.getInt(0);
            break;
        }
        q.close();
        return id;
    }

    public static int getFirstQuestionId(int gameId) {
        int id = 0;
        Cursor q = mDb.rawQuery("SELECT min(q.ID) FROM QUESTION q WHERE q.GAME_ID = ?", new String[]{String.valueOf(gameId)});
        q.moveToFirst();
        while (!q.isAfterLast()) {
            id = q.getInt(0);
            break;
        }
        q.close();
        return id;
    }

    public static int getNextQuestionId(int gameId, int questionId) {
        int id = 0;
        Cursor q = mDb.rawQuery("SELECT min(q.ID) FROM QUESTION q WHERE q.GAME_ID = ? AND ID > ?", new String[]{String.valueOf(gameId), String.valueOf(questionId)});
        q.moveToFirst();
        while (!q.isAfterLast()) {
            id = q.getInt(0);
            break;
        }
        q.close();
        return id;
    }

    public static int getPrevQuestionId(int gameId, int questionId) {
        int id = 0;
        Cursor q = mDb.rawQuery("SELECT max(q.ID) FROM QUESTION q WHERE q.GAME_ID = ? AND ID < ?", new String[]{String.valueOf(gameId), String.valueOf(questionId)});
        q.moveToFirst();
        while (!q.isAfterLast()) {
            id = q.getInt(0);
            break;
        }
        q.close();
        return id;
    }


    //Для листания фаворитов
    public static ids getNextFavoriteId(int gameId, int questionId) {
        int id = 0;
        int loc_id = 0;
        Cursor q = getFavoriteList();
        q.moveToFirst();
        boolean f = false;
        while (!q.isAfterLast()) {
            id = q.getInt(2);
            loc_id = q.getInt(0);
            if (f) {
                break;
            }
            if (id == questionId) {
                f = true;
            }
            q.moveToNext();
        }
        q.close();
        if (id != questionId) {
            return new ids(id, loc_id);
        } else {
            return new ids(0, 0);
        }
    }

    public static ids getPrevFavoriteId(int gameId, int questionId) {
        int id = 0;
        int loc_id = 0;
        int prevId = 0;
        Cursor q = getFavoriteList();
        q.moveToFirst();
        while (!q.isAfterLast()) {
            id = q.getInt(2);
            if (id == questionId) {
                break;
            }
            prevId = id;
            loc_id = q.getInt(0);
            q.moveToNext();
        }
        q.close();
        return new ids(prevId, loc_id);
    }

    public static int getGameId(int questionId) {
        int id = 0;
        Cursor q = mDb.rawQuery("SELECT GAME_ID FROM QUESTION q WHERE q.ID = ?", new String[]{String.valueOf(questionId)});
        q.moveToFirst();
        while (!q.isAfterLast()) {
            id = q.getInt(0);
            break;
        }
        q.close();
        return id;
    }

    public static int createNullQuestion(int gameId) {
        // Определяем относительный номер будущего вопроса
        //Cursor q = mDb.rawQuery("SELECT g.Q_LAST_ID FROM GAME g WHERE g.ID = ?", new String[]{String.valueOf(gameId)});
        Cursor q = mDb.rawQuery("SELECT max(LOC_ID) FROM QUESTION g WHERE g.GAME_ID = ?", new String[]{String.valueOf(gameId)});
        q.moveToFirst();
        int newId = 0;
        while (!q.isAfterLast()) {
            newId = q.getInt(0);
            break;
        }
        q.close();
        newId++;

        ContentValues values = new ContentValues();
        values.put("GAME_ID", gameId);
        values.put("Q", "");
        values.put("LOC_ID", newId);
        values.put("NAME", newId + ""); // уже лишнее это
        Long rowId = mDb.insert("QUESTION", null, values);

        q = mDb.rawQuery("SELECT q.ID FROM QUESTION q WHERE ROWID = ?", new String[]{String.valueOf(rowId)});
        q.moveToFirst();
        int id = q.getInt(0);
        q.close();

        // уже лишнее это
        mDb.execSQL("UPDATE GAME SET Q_LAST_ID = ? WHERE ID = ?", new String[]{String.valueOf(newId), String.valueOf(gameId)});

        return id;
    }

    public static QuestionRecord getFirstQuestion(int gameId) {
        int id = getFirstQuestionId(gameId);
        if (id == 0) {
            id = createNullQuestion(gameId);
        }
        return new QuestionRecord(gameId, id);
    }

    public static QuestionRecord getQuestion(int gameId, int questionId) {
        return new QuestionRecord(gameId, questionId);
    }

    public static void updateQuestion(QuestionRecord qRec) {
        ContentValues values = new ContentValues();
        values.put("Q", qRec.q);
        values.put("A1", qRec.a1);
        values.put("A2", qRec.a2);
        values.put("A3", qRec.a3);
        values.put("A4", qRec.a4);
        values.put("A1_RES", qRec.a1_res);
        values.put("A2_RES", qRec.a2_res);
        values.put("A3_RES", qRec.a3_res);
        values.put("A4_RES", qRec.a4_res);
        values.put("A1_MARKER", qRec.a1_marker);
        values.put("A2_MARKER", qRec.a2_marker);
        values.put("A3_MARKER", qRec.a3_marker);
        values.put("A4_MARKER", qRec.a4_marker);
        values.put("BEST", qRec.best);
        values.put("IS_BOT", qRec.is_bot);
        mDb.update("QUESTION", values, "ID = ?", new String[]{String.valueOf(qRec.id)});
    }

    //Установка или сброс фаворита
    public static void setFavorite(int questionId) {
        mDb.execSQL("UPDATE QUESTION SET IS_FAV = ABS(IS_FAV - 1) WHERE ID = ?", new String[]{String.valueOf(questionId)});
    }

    //Счет статистики фаворитов
    public static int getCountFavorite() {
        int cnt = 0;
        Cursor q = mDb.rawQuery("SELECT count(*) FROM QUESTION WHERE IS_FAV = 1", null);
        q.moveToFirst();
        while (!q.isAfterLast()) {
            cnt = q.getInt(0);
            break;
        }
        q.close();
        return cnt;
    }

    /**
     * =========================================================================
     */
    public static void deleteGame(int gameId) {
        mDb.execSQL("DELETE FROM QUESTION " +
                "WHERE (IS_FAV = 0) " +
                "AND (GAME_ID NOT IN (SELECT ID FROM GAME))");
        //Чистилка вопросов игр так почему - то DELETE CASCADE не робит
        mDb.delete("QUESTION", "GAME_ID = ? AND IS_FAV = 0", new String[]{String.valueOf(gameId)});
        mDb.delete("GAME", "ID = ?", new String[]{String.valueOf(gameId)});
    }

    public static void deleteQuestion(int questionId) {
        mDb.delete("QUESTION", "ID = ?", new String[]{String.valueOf(questionId)});
    }

    /**
     * =========================================================================
     */
    // 0 - gameId
    // 1 - Имя игры
    // 2 - количество вопросов
    // 3 - количество успешных
    public static Cursor getGameList() {
        // удалим все левые вопросы. это чистка возможного мусора
        mDb.execSQL("DELETE FROM QUESTION " +
                "WHERE ((Q IS NULL) OR Q = '') " +
                "AND (GAME_ID NOT IN (SELECT ID FROM GAME WHERE IS_LAST = 1))");

        return mDb.rawQuery("SELECT g.ID, g.NAME, " +
                "(select count(*) from QUESTION WHERE GAME_ID=g.ID) cnt, " +
                "(select count(*) from QUESTION WHERE GAME_ID=g.ID " +
                "AND ((BEST == 1 AND A1_MARKER == 'Y') " +
                "  OR (BEST == 2 AND A2_MARKER == 'Y')" +
                "  OR (BEST == 3 AND A3_MARKER == 'Y')" +
                "  OR (BEST == 4 AND A4_MARKER == 'Y')" +
                ")) cntBest " +
                "FROM GAME g " +
                "ORDER BY g.ID DESC", null);
    }

    public static Cursor getQuestionList(int gameId) {
        return mDb.rawQuery("SELECT q.ID, q.NAME, q.Q, q.IS_FAV, " +
                "(   (BEST == 1 AND A1_MARKER == 'Y') " +
                " OR (BEST == 2 AND A2_MARKER == 'Y') " +
                " OR (BEST == 3 AND A3_MARKER == 'Y') " +
                " OR (BEST == 5 AND A4_MARKER == 'Y')) 'CHECK' " +
                " FROM QUESTION q WHERE q.GAME_ID = ?" +
                " AND q.Q IS NOT NULL AND q.Q != '' " +
                " ORDER BY q.ID DESC", new String[]{String.valueOf(gameId)});
    }

    public static Cursor getFavoriteList() {
        //String nn = "row_number() over ()"; //  порядковый  номер строки выборки
        String nn = "ROW_NUMBER() OVER(ORDER BY Q) AS RowNumber"; //  порядковый  номер строки выборки
        nn = "0";
        String sql = "SELECT " + nn + ", Q, ID, GAME_ID, " +
                "(   (BEST == 1 AND A1_MARKER == 'Y') " +
                " OR (BEST == 2 AND A2_MARKER == 'Y') " +
                " OR (BEST == 3 AND A3_MARKER == 'Y') " +
                " OR (BEST == 5 AND A4_MARKER == 'Y')) 'CHECK' " +
                "FROM (SELECT q.Q, q.ID, q.GAME_ID, " +
                " BEST, A1_MARKER, A2_MARKER, A3_MARKER, A4_MARKER " +
                " FROM QUESTION q WHERE q.IS_FAV = 1" +
                " AND q.Q IS NOT NULL AND q.Q != '' " +
                " ORDER BY q.Q ASC)";
        return mDb.rawQuery(sql, null);
    }

    /**
     * =========================================================================
     */
    //  не используем
    public static Cursor getSystemList() {
        //mDb.execSQL("DELETE FROM SYSTEM WHERE ID = 9");
        String sql = "SELECT ID, TYPE, NAME, VALUE " +
                "FROM SYSTEM " +
                "ORDER BY TYPE, NAME";
        return mDb.rawQuery(sql, null);
    }

    public static Cursor getSystemForBot() {
        String sql = "SELECT VALUE " +
                "FROM SYSTEM " +
                "WHERE TYPE in ('SYS1', 'SS') " +
                "ORDER BY ID";
        return mDb.rawQuery(sql, null);
    }

    public static String getSystemValue(String sType, String sName) {
        String val = "";
        String sql = "SELECT VALUE " +
                "FROM SYSTEM " +
                "WHERE TYPE = ? AND NAME = ?";
        Cursor q = mDb.rawQuery(sql, new String[]{String.valueOf(sType), String.valueOf(sName)});
        q.moveToFirst();
        while (!q.isAfterLast()) {
            val = q.getString(0);
            break;
        }
        q.close();
        return val;
    }

    public static void setSystemValue(String sType, String sName, String sValue) {
//        mDb.execSQL("UPDATE SYSTEM " +
//                "SET VALUE = ? " +
//                "WHERE TYPE = ? AND NAME = ?", new String[]{String.valueOf(sValue), String.valueOf(sType), String.valueOf(sName)});
        ContentValues values = new ContentValues();
        values.put("VALUE", sValue);
        long id = mDb.update("SYSTEM", values, "TYPE = ? AND NAME = ?", new String[]{sType, sName});
        if (id == 0 && sType == "MODE") {
            values.put("TYPE", sType);
            values.put("NAME", sName);
            id = mDb.insert("SYSTEM", null, values);
        }
    }

    public static void setNewGameName(int gameId, String newName) {
        ContentValues values = new ContentValues();
        values.put("NAME", newName);
        mDb.update("GAME", values, "ID = ?", new String[]{String.valueOf(gameId)});
    }

    public static String getGameName(int gameId) {
        Cursor q = mDb.rawQuery("SELECT NAME FROM GAME WHERE ID = ?", new String[]{String.valueOf(gameId)});
        q.moveToFirst();
        String name = null;
        while (!q.isAfterLast()) {
            name = q.getString(0);
            break;
        }
        q.close();
        return name;
    }

}
