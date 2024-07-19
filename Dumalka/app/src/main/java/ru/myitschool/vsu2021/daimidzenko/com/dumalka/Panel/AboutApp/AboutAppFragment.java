package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Panel.AboutApp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service.Dumalka;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Panel.AboutApp.Src.Versions;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.Panel.AboutApp.Src.VersionsAdapter;
import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;


public class AboutAppFragment extends Fragment implements Dumalka {

    private RecyclerView recyclerView;
    private List<Versions> versionsList;
    private String Question_1, Question_2, Question_3, Question_4, Question_5, Question_6, Question_7, Question_8;
    private String Answer_1, Answer_2, Answer_3, Answer_4, Answer_5, Answer_6, Answer_7, Answer_8;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aboutapp, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        fillString();
        initData();
        setRecyclerView();
        return root;

    }

    private void setRecyclerView() {
        VersionsAdapter versionsAdapter = new VersionsAdapter(versionsList);
        recyclerView.setAdapter(versionsAdapter);
        recyclerView.setHasFixedSize(true);
    }

    // Заполнения FAQ
    private void initData() {
        versionsList = new ArrayList<>();
        versionsList.add(new Versions(Question_1, Answer_1));
        versionsList.add(new Versions(Question_2, Answer_2));
        versionsList.add(new Versions(Question_3, Answer_3));
        versionsList.add(new Versions(Question_4, Answer_4));
        versionsList.add(new Versions(Question_5, Answer_5));
        versionsList.add(new Versions(Question_6, Answer_6));
        versionsList.add(new Versions(Question_7, Answer_7));
        versionsList.add(new Versions(Question_8, Answer_8));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void fillString() {
        Question_1 = getString(R.string.J_FAQ_Q_1);
        Question_2 = getString(R.string.J_FAQ_Q_2);
        Question_3 = getString(R.string.J_FAQ_Q_3);
        Question_4 = getString(R.string.J_FAQ_Q_4);
        Question_5 = getString(R.string.J_FAQ_Q_5);
        Question_6 = getString(R.string.J_FAQ_Q_6);
        Question_7 = getString(R.string.J_FAQ_Q_7);
        Question_8 = getString(R.string.J_FAQ_Q_8);

        Answer_1 = getString(R.string.J_FAQ_A_1);
        Answer_2 = getString(R.string.J_FAQ_A_2);
        Answer_3 = getString(R.string.J_FAQ_A_3);
        Answer_4 = getString(R.string.J_FAQ_A_4);
        Answer_5 = getString(R.string.J_FAQ_A_5);
        Answer_6 = getString(R.string.J_FAQ_A_6);
        Answer_7 = getString(R.string.J_FAQ_A_7);
        Answer_8 = getString(R.string.J_FAQ_A_8);

    }
}