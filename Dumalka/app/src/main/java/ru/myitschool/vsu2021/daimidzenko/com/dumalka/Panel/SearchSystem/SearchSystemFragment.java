package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Panel.SearchSystem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.github.muddz.styleabletoast.StyleableToast;
import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.DoubleClickListener;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;


public class SearchSystemFragment extends Fragment implements Dumalka {

    private SwitchCompat Google, Yandex, Yahoo, Swisscows, Excite, Aol;
    private ImageView imitation;
    private boolean flagImitation = true;
    private DbGlbSvc dbGlbSvc;
    private DbGlbSvc.QuestionRecord qRec;
    private String ToastOn, ToastOff;
    private KonfettiView konfettiView = null;
    private Shape.DrawableShape drawableShape = null;
    private Drawable drawable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_searchsystem, container, false);
        fillString();

        //Конфети
        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_heart);
        drawableShape = new Shape.DrawableShape(drawable, true);
        konfettiView = root.findViewById(R.id.konfettiView);

        Google = root.findViewById(R.id.CB_Google);
        Yandex = root.findViewById(R.id.CB_Yandex);
        Yahoo = root.findViewById(R.id.CB_Yahoo);
        Swisscows = root.findViewById(R.id.CB_Swisscows);
        Excite = root.findViewById(R.id.CB_Excite);
        Aol = root.findViewById(R.id.CB_Aol);
        imitation = root.findViewById(R.id.imitation);

        //Заполнения изначально из БД поисковых систем
        Google.setChecked(DbGlbSvc.getSystemValue("SS", "Google").equals("Y"));
        Yandex.setChecked(DbGlbSvc.getSystemValue("SS", "Yandex").equals("Y"));
        Yahoo.setChecked(DbGlbSvc.getSystemValue("SS", "Yahoo").equals("Y"));
        Swisscows.setChecked(DbGlbSvc.getSystemValue("SS", "Swisscows").equals("Y"));
        Excite.setChecked(DbGlbSvc.getSystemValue("SS", "Excite").equals("Y"));
        Aol.setChecked(DbGlbSvc.getSystemValue("SS", "Aol").equals("Y"));

        flagImitation = DbGlbSvc.getSystemValue("SYS1", "Имитация").equals("Y");

        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbGlbSvc.setSystemValue("SS", "Google", Google.isChecked() ? "Y" : "N");
            }
        });

        Yandex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbGlbSvc.setSystemValue("SS", "Yandex", Yandex.isChecked() ? "Y" : "N");
            }
        });

        Yahoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbGlbSvc.setSystemValue("SS", "Yahoo", Yahoo.isChecked() ? "Y" : "N");

            }
        });

        Swisscows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbGlbSvc.setSystemValue("SS", "Swisscows", Swisscows.isChecked() ? "Y" : "N");
            }
        });

        Excite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbGlbSvc.setSystemValue("SS", "Excite", Excite.isChecked() ? "Y" : "N");
            }
        });

        Aol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbGlbSvc.setSystemValue("SS", "Aol", Aol.isChecked() ? "Y" : "N");
            }
        });


        /** Double click для Пасхалки ( Включение имитации -> отключение поиска )*/
        imitation.setOnClickListener(new DoubleClickListener() {

            @Override
            public void onSingleClick(View v) {
            } // для одного клика

            @Override
            public void onDoubleClick(View v) {

                if (flagImitation) {
                    flagImitation = false;
                    StyleableToast.makeText(getActivity(), ToastOff,
                            Toast.LENGTH_SHORT, R.style.TurnOnImitationToast).show();
                    DbGlbSvc.setSystemValue("SYS1", "Имитация", "N"); //Вносим значение в бд

                } else {
                    flagImitation = true;
                    StyleableToast.makeText(getActivity(), ToastOn,
                            Toast.LENGTH_SHORT, R.style.TurnOffImitationToast).show();
                    KonfettiViewExplode();
                    DbGlbSvc.setSystemValue("SYS1", "Имитация", "Y");

                }


            }
        });

        return root;
    }


    //Конфети
    public void KonfettiViewExplode() {
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.4))
                        .build()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void fillString() {
        ToastOn = getString(R.string.J_SS_ToastOn);
        ToastOff = getString(R.string.J_SS_ToastOff);
    }
}