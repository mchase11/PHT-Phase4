package com.example.pht;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

import com.example.pht.database.exerciseDBHelper;
import com.example.pht.database.glucoseMealDBHelper;
import com.example.pht.database.vitalDBHelper;

public class home extends Fragment implements OnClickListener {

    EditText vit, glc, calIn, calOut;
    Button b;

    glucoseMealDBHelper glucoseDb;
    exerciseDBHelper exerciseDb;
    vitalDBHelper vitalDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_layout, container, false);

        // Assign databases
        MainActivity activity = (MainActivity)getActivity();
        glucoseDb = activity.glucoseMealDb;
        exerciseDb = activity.exerciseDb;
        vitalDb = activity.vitalDb;

        vit = (EditText) rootView.findViewById(R.id.lastVitalsText);
        glc = (EditText) rootView.findViewById(R.id.lastglucoseText);
        calIn = (EditText) rootView.findViewById(R.id.caloriesTodaytext);
        calOut = (EditText) rootView.findViewById(R.id.caloriesBurnedtext);
        b = (Button) rootView.findViewById(R.id.hmRefresh);
        b.setOnClickListener(this);

        vit.setKeyListener(null);
        glc.setKeyListener(null);
        calIn.setKeyListener(null);
        calOut.setKeyListener(null);

        glc.setText(Integer.toString(glucoseDb.getLastGlucose()));
        calIn.setText(Integer.toString(glucoseDb.getDailyCalories(0)));
        calOut.setText(Integer.toString(exerciseDb.getDailyCalories(0)));
        vit.setText(vitalDb.getLatest());

        return rootView;
    }

    public void onClick(View view) {
        glc.setText(Integer.toString(glucoseDb.getLastGlucose()));
        calIn.setText(Integer.toString(glucoseDb.getDailyCalories(0)));
        calOut.setText(Integer.toString(exerciseDb.getDailyCalories(0)));
        vit.setText(vitalDb.getLatest());
    }
}