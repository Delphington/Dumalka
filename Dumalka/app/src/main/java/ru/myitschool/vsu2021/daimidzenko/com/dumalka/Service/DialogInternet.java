package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Service;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import ru.myitschool.vsu2021.daimidzenko.com.dumalka.R;


/** Класс создан, чтобы диалог No Internet Закрыть открыть вместо того чтобы это делать в HomeFragment*/
public class DialogInternet extends DialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_no_internet, null);

        Button back = v.findViewById(R.id.NoInternetBack);
        Button TurnOn  =v.findViewById(R.id.onInternetTurnOn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // переход на настройку в Wi-Fi
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                dismiss();
            }
        });

        return v;
    }
}
