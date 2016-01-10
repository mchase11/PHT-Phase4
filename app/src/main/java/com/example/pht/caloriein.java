package com.example.pht;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pht.database.meal;
import com.example.pht.database.glucoseMealDBHelper;

import java.util.ArrayList;
import java.util.List;

public class caloriein extends Fragment implements View.OnClickListener {

    Spinner ml;
    EditText cal;
    Button b;
    glucoseMealDBHelper mealDb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.caloriein_layout, container, false);

        // Assign meal DB
        MainActivity activity = (MainActivity)getActivity();
        mealDb = activity.glucoseMealDb;

        cal = (EditText) rootView.findViewById(R.id.inputcaloriesText);
        ml = (Spinner) rootView.findViewById(R.id.mealspinner);
        b = (Button) rootView.findViewById(R.id.calorieintakeButton);

        loadSpinnerData();

        b.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View view){
        if(!cal.getText().toString().isEmpty())
            alertMessage();
    }

    private void loadSpinnerData() {
        List<meal> meals = mealDb.getAllMeals();
        List<String> labels = new ArrayList<String>();

        for (meal temp : meals)
            labels.add(temp.getName());

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, labels);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        ml.setAdapter(dataAdapter);
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        mealDb.logCalories(ml.getSelectedItem().toString(),
                                Integer.parseInt(cal.getText().toString()));
                        ml.setSelection(0);
                        cal.setText("");
                        Toast.makeText(getActivity(), "Calories Recorded",
                                Toast.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage("Record Calories?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}