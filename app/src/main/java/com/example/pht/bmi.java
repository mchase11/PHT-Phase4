package com.example.pht;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class bmi extends Fragment implements OnClickListener {
    Button b;
    EditText w, h, r;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bmi_layout, container, false);

        w = (EditText) rootView.findViewById(R.id.weightText);
        h = (EditText) rootView.findViewById(R.id.heightText);
        r = (EditText) rootView.findViewById(R.id.editText);
        r.setKeyListener(null);

        b = (Button) rootView.findViewById(R.id.calculateButton);
        b.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View view) {
        Integer weight = Integer.parseInt((w.getText().toString().isEmpty()) ? "0" : w.getText().toString());
        Integer height = Integer.parseInt((h.getText().toString().isEmpty()) ? "0" : h.getText().toString());
        Float result = (height*weight == 0) ? 0 : ((float)weight/(height*height))*703;
        r.setText(String.format("%3.1f", result));
    }
}