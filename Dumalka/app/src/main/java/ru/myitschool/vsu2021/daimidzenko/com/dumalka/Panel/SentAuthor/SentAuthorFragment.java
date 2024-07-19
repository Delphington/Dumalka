package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Panel.SentAuthor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Database.DbGlbSvc;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;

public class SentAuthorFragment extends Fragment implements Dumalka {

    private String gmail, subject;
    private DbGlbSvc dbGlbSvc;
    private String obj;
    private Button sent;
    private ImageView image;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sentauthor, container, false);
        fillString();

        sent = root.findViewById(R.id.sentAutos);
        image = root.findViewById(R.id.LogoImage2);

        //== Для темы, меняем цвет логотипа
        String f = dbGlbSvc.getSystemValue("CFG", "LightThema");
        if (!f.equals("Y")) {
            image.setImageResource(R.drawable.ic_d_start_logo_night);
        }
        //================================================

        //Переход на gmail при нажатии на кнопку
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj = "mailto:" + gmail + "?&subject=" + Uri.encode(subject);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(obj));
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void fillString() {
        gmail = getString(R.string.J_SA_gmail);
        subject = getString(R.string.J_SA_subject);
    }
}