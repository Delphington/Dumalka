package ru.myitschool.vsu2021.daimidzenko.com.dumalka.PandaBot;

/**
 * **********************************************************************
 * Интеллектуальная собственность

 * **********************************************************************
 **/

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Panda {

    public static boolean consoleFlag = true;

    public static final String G_BEST = "Y";
    public static final String G_DOWN = "N";
    public static final String G_NONE = "-";
    public static final String G_MISSING = "X";

    static String G_LOG = "";  // журнал работы

    private static void log(String text) {
        G_LOG = G_LOG + text + "\n";
    }

    /**
     * Настройки
     **/
    public static class PandaSet {
        public boolean imitator;
        public boolean consoleFlag;
        public boolean ssGoogle;     // 1
        public boolean ssYahoo;      // 2
        public boolean ssYandex;     // 3
        public boolean ssSwisscows;  // 4
        public boolean ssExcite;     // 5
        public boolean ssAol;        // 6

        // Конструктор
        public PandaSet() {
            this.imitator = false;
            this.consoleFlag = false;
            this.ssGoogle = true;
            this.ssYahoo = true;
            this.ssYandex = true;
            this.ssSwisscows = true;
            this.ssExcite = true;
            this.ssAol = true;
        }
    }

    /**
     * Структурный объект ответа
     **/
    public static class PandaAnswer {
        String text;                // текст ответа
        int coincidences;           // количество совпадений ответа
        public double probability;  // итоговая вероятность в группе ответов
        public String marker;       // результирующий признак (Y - лучший ответ, N - худший ответ, X - ответа не было)
        boolean needRevers;         // надо ли применять реверсивный алгоритм после первого прохода

        // Конструктор
        public PandaAnswer(String text, int coincidences) {
            this.text = text;
            this.coincidences = coincidences;
            this.marker = text != null && text.trim().length() > 1 ? G_NONE : G_MISSING;
            this.needRevers = false;
        }

        // Фиксация посчитанной вероятности
        public void setProbability(double newValue) {
            this.probability = newValue;
        }

        public void setMarker(String marker) {
            this.marker = marker;
        }
        //Для журнала
        public String getStr() {
            return this.text + " | " + this.probability + "%";
        }

    }

    /**
     * Результат работы бота
     **/
    public static class PandaQuestion {
        String question;                // вопрос
        public PandaAnswer[] answers;   // массив ответов (структурных объектов)
        boolean reverse;                // реверсивный ли вопрос?
        public String best;             // текст про лучший ответ
        public int bestIdx;             // индекс лучшего ответа
        String log;                     // журнал работы бота

        // Конструктор
        public PandaQuestion(String question, PandaAnswer[] answers) {
            this.question = question;
            this.answers = answers;
            this.reverse = (question.indexOf(" не ") >= 0 || question.indexOf(" ни ") >= 0 ||
                    question.indexOf(" нельзя ") >= 0 || question.indexOf(" нет ") >= 0 ||
                    question.indexOf("для другой цели") >= 0 || question.indexOf("осторожным против чего") >= 0);
            this.best = "";
            this.bestIdx = 0;
        }

        private void setLog() {
            this.log = G_LOG;
        }

        public String getLog() {
            return this.log;
        }

        // Простой расчет вероятности (на основе выявленных совпадений)
        public void calculate_probability() {
            int total = 0;
            // подсчитываем общее количество совпадений по всем вопросам
            for (PandaAnswer answer : this.answers) {
                if (answer.marker == G_MISSING) {
                    continue;
                }
                total += answer.coincidences;
            }
            // пробегаем по каждому вопросу и высчитываем вероятность
            for (PandaAnswer answer : this.answers) {
                if (answer.marker == G_MISSING) {
                    continue;
                }
                if (total == 0) {
                    answer.setProbability(0);
                } else {
                    Double d1 = ((double) answer.coincidences) / total * 100;
                    // Усекаем до двух значений после запятой
                    BigDecimal bd = new BigDecimal(Double.toString(d1));
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    answer.setProbability(bd.doubleValue());
                }
            }
            if (total == 0) {
                this.question += " | NOT FOUND!";
                this.best = "0. ???";
            } else {
                // поиск MAX
                int max_ = 0;
                for (PandaAnswer answer : this.answers) {
                    if (answer.marker != G_MISSING && answer.coincidences >= max_) {
                        max_ = answer.coincidences;
                    }
                }
                // поиск MIN
                int min_ = max_;
                for (PandaAnswer answer : this.answers) {
                    if (answer.marker != G_MISSING && answer.coincidences < min_) {
                        min_ = answer.coincidences;
                    }
                }
                // Определяем лучший/худший ответ. Потенциально их может быть несколько
                // в BEST пойдет последний
                int i = 0;
                if (this.reverse) {
                    for (PandaAnswer answer : this.answers) {
                        i++;
                        if (answer.marker == G_MISSING) {
                            continue;
                        }
                        if (answer.coincidences == min_) {
                            answer.setMarker(G_BEST);
                            this.best = i + ". " + answer.text;
                            this.bestIdx = i;
                        } else if (answer.coincidences == max_) {
                            answer.setMarker(G_DOWN);
                        }
                    }
                } else {
                    for (PandaAnswer answer : this.answers) {
                        i++;
                        if (answer.marker == G_MISSING) {
                            continue;
                        }
                        if (answer.coincidences == max_) {
                            answer.setMarker(G_BEST);
                            this.best = i + ". " + answer.text;
                            this.bestIdx = i;
                        } else if (answer.coincidences == min_) {
                            answer.setMarker(G_DOWN);
                        }
                    }
                }
            }
        }
    }

    /**
     * **********************************************************************
     * *** Основной класс поискового PANDA бота
     * **********************************************************************
     **/
    public static class PandaGoogler {
        String[] FILTERED_WORDS = {"сколько", "как много", "вошли в историю как", "какие", "как называется", "чем является",
                "что из этого", "(у |)как(ой|ого|их) из( этих|)", "какой из героев", "традиционно", "согласно",
                " - ",
                "чем занимается", "чья профессия", "состоялся", "из фильма", "что из этого",
                "какой", "является", "в мире", "и к", "термин(ов|ы|)", "относ(и|я)тся", "в какой",
                "у как(ого|ой|их)", "(на|в) каком", "по численности", " ни "};
        HashMap<String, String> OPTIMIZE_DICT = new HashMap<>();
        String question;         // исходный вопрос
        PandaAnswer[] answers;   // массив ответов (структурных объектов)
        String __question;       // очищенный вопрос
        String __newquestion;    // очищенный комбо-вопрос (вопрос + все ответы)
        String[] __answers;      // массив очищенных ответов
        // ----------------------------------
        PandaSet sets;
        String httpResult;
        int httpCount;

        private void initializationDict() {
            this.OPTIMIZE_DICT.put("в каком году", "когда");
            this.OPTIMIZE_DICT.put("какого животного", "кого");
            this.OPTIMIZE_DICT.put("один", "1");
            this.OPTIMIZE_DICT.put("одна", "1");
            this.OPTIMIZE_DICT.put("одно", "1");
            this.OPTIMIZE_DICT.put("два", "2");
            this.OPTIMIZE_DICT.put("двое", "2");
            this.OPTIMIZE_DICT.put("две", "2");
            this.OPTIMIZE_DICT.put("три", "3");
            this.OPTIMIZE_DICT.put("трое", "3");
            this.OPTIMIZE_DICT.put("четыре", "4");
            this.OPTIMIZE_DICT.put("четверо", "4");
            this.OPTIMIZE_DICT.put("пять", "5");
            this.OPTIMIZE_DICT.put("пятеро", "5");
            this.OPTIMIZE_DICT.put("шесть", "6");
            this.OPTIMIZE_DICT.put("шестеро", "6");
            this.OPTIMIZE_DICT.put("семь", "7");
            this.OPTIMIZE_DICT.put("семеро", "7");
            this.OPTIMIZE_DICT.put("восемь", "8");
            this.OPTIMIZE_DICT.put("девять", "9");
            this.OPTIMIZE_DICT.put("десять", "10");
            this.OPTIMIZE_DICT.put("одиннадцать", "11");
            this.OPTIMIZE_DICT.put("двенадцать", "12");
            this.OPTIMIZE_DICT.put("тринадцать", "13");
            this.OPTIMIZE_DICT.put("четырнадцать", "14");
            this.OPTIMIZE_DICT.put("пятнадцать", "15");
            this.OPTIMIZE_DICT.put("шестнадцать", "16");
            this.OPTIMIZE_DICT.put("семнадцать", "18");
            this.OPTIMIZE_DICT.put("девятнадцать", "19");
            this.OPTIMIZE_DICT.put("двадцать", "20");
            this.OPTIMIZE_DICT.put("тридцать", "30");
            this.OPTIMIZE_DICT.put("сорок", "40");
            this.OPTIMIZE_DICT.put("пятьдесят", "50");
            this.OPTIMIZE_DICT.put("шестьдесят", "60");
            this.OPTIMIZE_DICT.put("семьдесят", "70");
            this.OPTIMIZE_DICT.put("восемьдесят", "80");
            this.OPTIMIZE_DICT.put("девяносто", "90");
            this.OPTIMIZE_DICT.put("сто", "100");
            this.OPTIMIZE_DICT.put("двести", "200");
            this.OPTIMIZE_DICT.put("триста", "300");
            this.OPTIMIZE_DICT.put("четыреста", "400");
            this.OPTIMIZE_DICT.put("пятьсот", "500");
            this.OPTIMIZE_DICT.put("шестьсот", "600");
            this.OPTIMIZE_DICT.put("семьсот", "700");
            this.OPTIMIZE_DICT.put("восемьсот", "800");
            this.OPTIMIZE_DICT.put("девятьсот", "900");
            this.OPTIMIZE_DICT.put("тысяча", "1000");
            this.OPTIMIZE_DICT.put("тысячи", "000");
            this.OPTIMIZE_DICT.put("тысяч", "000");
            this.OPTIMIZE_DICT.put("миллион", "1000000");
            this.OPTIMIZE_DICT.put("миллиона", "000000");
            this.OPTIMIZE_DICT.put("миллионов", "000000");
        }

        // Конструктор
        public PandaGoogler(String question, String[] answers, PandaSet sets) {
            this.initializationDict();
            this.question = question;
            this.answers = new PandaAnswer[answers.length];

            this.__question = optimizeString(question);
            this.__answers = new String[answers.length];

            int i = 0;
            for (String answer : answers) {
                __answers[i] = optimizeString(answer);
                i++;
            }

            String s = "";
            for (String __answer : __answers) {
                s = s + " | " + __answer;
            }
            if (s.length() > 3) {
                s = s.substring(3);
            }
            this.__newquestion = __question + " (" + s + ")";

            this.httpResult = "";
            this.httpCount = 0;
            this.sets = sets;

            //------------------
            log("вопрос: " + this.__question);
            for (String __answer : this.__answers) {
                log("ответ: " + __answer);
            }
            //------------------
        }

        // Технический метод для выполнения GET запроса
        // Необходим клиент:  OkHttpClient client = new OkHttpClient();
        private String httpGet(OkHttpClient client, String urlStr) throws IOException {
            Request request = new Request.Builder()
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Connection", "keep-alive")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:97.0) Gecko/20100101 Firefox/97.0")
                    .url(urlStr)
                    .build();
            Call call = client.newCall(request);
            String body;
            try {
                Response response = call.execute();
                return response.body().string();
            } catch (IOException e) {
                return "";
            }
        }

        // Очистка HTML
        public static String clearHtml(String html) {
            String result = html;
            //--------------------
            // Удаляем inline JavaScript/CSS:
            result = result.replaceAll("<style>.*?(</style>)", " ");
            result = result.replaceAll("<script>.*?(</script>)", " ");
            // Удаляем комментарии
            result = result.replaceAll("<!--(.*?)-->[\\n]", " ");
            // Удаляем оставшиеся теги
            result = result.replaceAll("<.*?>", " ");
            //--------------------
            // Вообще удаляем не русские буквы
            result = result.replaceAll("[^а-я ]", "");
            //--------------------
            // Удаляем жесткие пробелы
            result = result.replaceAll("&nbsp;", " ");
            // Удаляем любые конструкции &...;
            result = result.replaceAll("&(.*?);", " ");
            // Избавляемся от переноса стоки;
            result = result.replaceAll("\n", " ");
            // Сжимаем множественные пробелы в один пробел
            result = result.replaceAll(" +", " ");
            //--------------------
            // Удаляем граничные пробелы
            result = result.trim();
            //--------------------
            return result;
        }

        private String fetch(String qwery, String newquery) throws IOException {
            String out = "";
            String url = "";

            OkHttpClient client = new OkHttpClient();

            // .......................................................
            log("1) google -----------------------------");
            if (this.sets.ssGoogle) {
                url = "https://www.google.ru/search?q=" + URLEncoder.encode(qwery, "UTF-8");
                log(url);
                String google = httpGet(client, url).toLowerCase(Locale.ROOT);
                //log("raw: " + google);
                if (google.indexOf("Our systems have detected unusual traffic from your computer network") == -1 &&
                        google.indexOf("support.google.com/websearch/answer/86640") == -1) {
                    google = clearHtml(google);
                    out += " " + google;
                    log("google: OK");
                    log("google: " + google);
                } else {
                    log("google: FAIL");
                }
            } else {
                log("google: DISABLED");
            }
            // .......................................................
            log("2) yahoo ------------------------------");
            if (this.sets.ssYahoo) {
                url = "https://search.yahoo.com/search?p=" + URLEncoder.encode(qwery, "UTF-8");
                log(url);
                String yahoo = httpGet(client, url).toLowerCase(Locale.ROOT);
                //log("raw: " + yahoo);
                if (yahoo.indexOf("captcha") == -1) {
                    yahoo = clearHtml(yahoo);
                    out += " " + yahoo;
                    log("yahoo: OK");
                    log("yahoo: " + yahoo);
                } else {
                    log("yahoo: FAIL");
                }
            } else {
                log("yahoo: DISABLED");
            }
            // .......................................................
            log("3) yandex -----------------------------");
            if (this.sets.ssYandex) {
                url = "https://www.yandex.ru/search/?text=" + URLEncoder.encode(qwery, "UTF-8");
                log(url);
                String yandex = httpGet(client, url).toLowerCase(Locale.ROOT);
                //log("raw: " + yandex);
                if (yandex.indexOf("captchaSound") == -1) {
                    yandex = clearHtml(yandex);
                    out += " " + yandex;
                    log("yandex: OK");
                    log("yandex: " + yandex);
                } else {
                    log("yandex: FAIL");
                }
            } else {
                log("yandex: DISABLED");
            }
            // .......................................................
            log("4) swisscows --------------------------");
            if (this.sets.ssSwisscows) {
                url = "https://swisscows.com/web?query=" + URLEncoder.encode(qwery, "UTF-8");
                log(url);
                String swisscows = httpGet(client, url).toLowerCase(Locale.ROOT);
                //log("raw: " + swisscows);
                if (swisscows.indexOf("captcha") == -1) {
                    swisscows = clearHtml(swisscows);
                    out += " " + swisscows;
                    log("swisscows: OK");
                    log("swisscows: " + swisscows);
                } else {
                    log("swisscows: FAIL");
                }
            } else {
                log("swisscows: DISABLED");
            }
            // .......................................................
            log("5) excite --------------------------");
            if (this.sets.ssExcite) {
                url = "https://results.excite.com/serp?q=" + URLEncoder.encode(qwery, "UTF-8");
                log(url);
                String excite = httpGet(client, url).toLowerCase(Locale.ROOT);
                //log("raw: " + excite);
                if (excite.indexOf("captcha") == -1) {
                    excite = clearHtml(excite);
                    out += " " + excite;
                    log("excite: OK");
                    log("excite: " + excite);
                } else {
                    log("excite: FAIL");
                }
            } else {
                log("excite: DISABLED");
            }
            // .......................................................
            log("6) aol --------------------------");
            if (this.sets.ssAol) {
                url = "https://www.aolкsearch.com/search?q=" + URLEncoder.encode(qwery, "UTF-8");
                log(url);
                String aol = httpGet(client, url).toLowerCase(Locale.ROOT);
                if (aol.indexOf("captcha") == -1) {
                    aol = clearHtml(aol);
                    out += " " + aol;
                    log("aol: OK");
                    log("aol: " + aol);
                } else {
                    log("aol: FAIL");
                }
            } else {
                log("aol: DISABLED");
            }
            // .......................................................
            //String newyandex = httpGet("https://www.yandex.ru/search/?text=" + URLEncoder.encode(newquery, "UTF-8")).toLowerCase(Locale.ROOT);
//            String ddg = httpGet("https://duckduckgo.com/?q=" + URLEncoder.encode(qwery, "UTF-8") + "&format=json");
//
//            // .......................................................
//            try {
//                JSONObject ddgJson = new JSONObject(ddg);
//                if (ddgJson.optString("AbstractURL") != null) {
//                    String smth = httpGet(ddgJson.getString("AbstractURL")).toLowerCase(Locale.ROOT);
//                    smth = clearHtml(smth);
//                    out += " " + smth;
//                    log("duckduckgo: OK");
//                    log("duckduckgo: " + smth);
//                }
//                String abst = ddgJson.optString("Abstract");
//                abst = clearHtml(abst);
//                out += " " + abst;
//                log("duckduckgo abstract: OK");
//                log("duckduckgo abstract: " + abst);
//            } catch (Exception ex) {
//                log("duckduckgo: FAIL (" + ex.getMessage() + ")");
//            }
            // .......................................................
//            if (newyandex.indexOf("captchaSound") == -1) {
//                newyandex = clearHtml(newyandex);
//                out += " " + newyandex;
//                log("yandex combo: OK");
//                log("yandex combo: " + newyandex);
//            } else {
//                log("yandex combo: FAIL");
//            }
            // .......................................................
            out = clearHtml(out);
            log("RESULT ----------------------------");
            log("RESULT: " + out);
            log("RESULT ----------------------------");
            return out;
        }

        private void search() throws IOException {
            int maxCount = 0;
            String response = fetch(this.__question, this.__newquestion);
            // Добавми пробелы. иначе потеряются граничные слова
            response = " " + response + " ";
            prn("search: " + response);
            boolean multFlag = true;
            for (String __answer : this.__answers) {
                if (__answer != null && __answer.indexOf(" и ") == -1) {
                    multFlag = false;
                    break;
                }
            }
            if (multFlag) {
                log("- Это вопрос из многих частей");
                int i = 0;
                for (String __answer : this.__answers) {
                    int currentCount = 0;
                    String[] words = __answer.split(" и ");
                    log("-----");
                    for (String word : words) {
                        prn(word);
                        Pattern p = Pattern.compile("(:|-|!|.|,|\\?|;|\"|'|`| )" + word + "(:|-|!|.|,|\\?|;|\"|'|`| )");
                        Matcher m = p.matcher(response);
                        int counter = 0;
                        while (m.find()) {
                            counter++;
                        }
                        currentCount += counter;
                        log("> " + word + ": " + counter);
                    }
                    log("итого: " + currentCount);
                    this.answers[i] = new PandaAnswer(__answer, currentCount);
                    if (maxCount < currentCount) {
                        maxCount = currentCount;
                    }
                    i++;
                }
            } else {
                log("- Это обычный вопрос");
                int i = 0;
                for (String __answer : __answers) {
                    int currentCount = 0;
                    if (__answer != null) {
                        String[] words = __answer.split(" ");
                        log("-----");
                        for (String word : words) {
                            prn(word);
                            Pattern p = Pattern.compile("(:|-|!|.|,|\\?|;|\"|'|`| )" + word + "(:|-|!|.|,|\\?|;|\"|'|`| )");
                            Matcher m = p.matcher(response);
                            int counter = 0;
                            while (m.find()) {
                                counter++;
                            }
                            currentCount += counter;
                            log("> " + word + ": " + counter);
                        }
                    }
                    log("итого: " + currentCount);
                    this.answers[i] = new PandaAnswer(__answer, currentCount);
                    if (maxCount < currentCount) {
                        maxCount = currentCount;
                    }
                    i++;
                }
            }
            // Если лучших получилось несколько, то применяем реверсивный поиск
            boolean flagNeedReverse = false;
            int countMax = 0;
            for (PandaAnswer answer : this.answers) {
                if (answer.marker == G_MISSING) {
                    continue;
                }
                if (answer.coincidences == maxCount) {
                    countMax++;
                }
            }
            if (countMax > 1) {
                flagNeedReverse = true;
                for (PandaAnswer answer : this.answers) {
                    if (answer.marker == G_MISSING) {
                        continue;
                    }
                    if (answer.coincidences == maxCount) {
                        answer.needRevers = true;
                    }
                }
            }
            // Есть необходимость в реверсном поиске
            if (flagNeedReverse) {
                this.doReverse(countMax);
            }
        }

        private void imitator() throws IOException {
            int maxCount = 0;
            log("- Это имитация результата");
            int i = 0;
            for (String __answer : this.__answers) {
                int currentCount = (int) (Math.random() * 100);
                log("итого: " + currentCount);
                this.answers[i] = new PandaAnswer(__answer, currentCount);
                if (maxCount < currentCount) {
                    maxCount = currentCount;
                }
                i++;
            }

            // Если лучших получилось несколько, то применяем реверсивный поиск
            boolean flagNeedReverse = false;
            int countMax = 0;
            for (PandaAnswer answer : this.answers) {
                if (answer.marker == G_MISSING) {
                    continue;
                }
                if (answer.coincidences == maxCount) {
                    countMax++;
                }
            }
            if (countMax > 1) {
                flagNeedReverse = true;
                for (PandaAnswer answer : this.answers) {
                    if (answer.marker == G_MISSING) {
                        continue;
                    }
                    if (answer.coincidences == maxCount) {
                        answer.needRevers = true;
                    }
                }
            }
            // Есть необходимость в реверсном поиске
            if (flagNeedReverse) {
                this.doReverse(countMax);
            }
        }

        private void doReverse(int cnt) {
            log("- Требуется реверсивный алгоритм. для ответов (шт): " + cnt);
        }

        private PandaQuestion genQuestion() {
            PandaQuestion pQ = new PandaQuestion(this.question, this.answers);
            pQ.calculate_probability();
            pQ.setLog();
            return pQ;
        }

        private String optimizeString(String base) {
            if (base == null) {
                return null;
            }
            // все приводим к нижнему регистру
            String _base = base.toLowerCase(Locale.ROOT);
            // удаляем всякий мусор (его по идее не должно быть)
            _base = _base.replaceAll("[\'@<!«»?,.]", "");
            // удаляем слова по справочнику FILTERED_WORDS
            String s = "";
            for (String word : FILTERED_WORDS) {
                s = s + "|" + word;
            }
            if (s.length() > 1) {
                s = s.substring(1);
            }
            _base = _base.replaceAll(s, " ");
            // заменяем слова по справочнику OPTIMIZE_DICT
            for (Map.Entry<String, String> entry : OPTIMIZE_DICT.entrySet()) {
                _base = _base.replaceAll(" " + entry.getKey() + " ", entry.getValue());
            }
            // заменяем множественные пробелы на один
            _base = _base.replaceAll(" +", " ");
            // удаляем граничные пробелы
            _base = _base.trim();
            //------------------------------------
            if (_base.equals("") || _base.length() == 0) {
                return null;
            } else {
                return _base;
            }
        }

        private String getLemmas() {
            return "";
        }
    }

    /**
     * ======================================================================
     * Запуск алгоритма Panda бота
     * ======================================================================
     **/
    public static PandaQuestion runner(String q, String[] s, PandaSet sets) throws IOException {
        consoleFlag = sets.consoleFlag;
        PandaGoogler pGoogler = new PandaGoogler(q, s, sets);  // Инициализируем
        if (sets.imitator) {
            pGoogler.imitator();                               // Имитируем результат
        } else {
            pGoogler.search();                                 // Выполняем поиск
        }
        PandaQuestion pQuestion = pGoogler.genQuestion();      // Формируем ответ
        return pQuestion;
    }

    /**
     * ======================================================================
     * Вариант работы с результатом (вывод на консоль)
     * ======================================================================
     **/
    public static void prn(String text) {
        if (consoleFlag) {
            System.out.println(text);
        }
    }

    public static void displayConsoleResult(PandaQuestion question) {
        prn("=================================================");
        prn("Вопрос: " + question.question);
        prn("=================================================");
        int i = 1;
        for (PandaAnswer answer : question.answers) {
            prn(i + ") " + answer.getStr());
            i++;
        }
        prn("=================================================");
        prn("Ответ " + question.best);
        prn("=================================================");
        prn("+++++++++++++++++++++++++++++++++++++++++++++++++");
        prn("используйте у себя: ");
        prn(".bestIdx = " + question.bestIdx);
        for (int j = 0; j < question.answers.length; j++) {
            prn(".answers[" + j + "].probability = " + question.answers[j].probability + " (" +
                    ".marker = " + question.answers[j].marker + ")");
        }
        prn("+++++++++++++++++++++++++++++++++++++++++++++++++");
        prn("Журнал: ");
        prn(question.getLog());
    }
}
