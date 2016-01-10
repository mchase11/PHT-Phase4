package com.example.pht;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import com.example.pht.database.exercise;

import com.example.pht.database.exerciseDBHelper;

public class caloriesburned extends Fragment implements OnClickListener{
    Button b;
    EditText min, wei, res;
    Spinner act;

    exerciseDBHelper exerciseDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calorieburned_layout, container, false);

        // Assign exercise DB
        MainActivity activity = (MainActivity)getActivity();
        exerciseDbHelper = activity.exerciseDb;

        b = (Button) rootView.findViewById(R.id.exerciseSubmit);
        min = (EditText) rootView.findViewById(R.id.minutesText);
        wei = (EditText) rootView.findViewById(R.id.weightExText);
        act = (Spinner) rootView.findViewById(R.id.exerciseSpinner);
        res = (EditText) rootView.findViewById(R.id.resultsText);

        res.setKeyListener(null);

        loadSpinnerData();

        b.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View view) {
        if(!min.getText().toString().isEmpty() && !wei.getText().toString().isEmpty())
            alertMessage();
    }

    private void loadSpinnerData() {
        List<exercise> exercises = exerciseDbHelper.getAllExercises();
        List<String> labels = new ArrayList<String>();

        for (exercise temp : exercises)
            labels.add(temp.getName());

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, labels);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        act.setAdapter(dataAdapter);
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        exerciseDbHelper.logExercise(act.getSelectedItem().toString(),
                                Integer.parseInt(min.getText().toString()),
                                Integer.parseInt(wei.getText().toString()));

                        res.setText((int) Math.round(exerciseDbHelper.calculateCalories(
                                exerciseDbHelper.getExercise(act.getSelectedItem().toString()),
                                Integer.parseInt(min.getText().toString()),
                                Integer.parseInt(wei.getText().toString()))) + " Calories Burned");

                        act.setSelection(0);
                        min.setText("");
                        wei.setText("");
                        Toast.makeText(getActivity(), "Exercise Recorded",
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
        builder.setMessage("Record Exercise?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}