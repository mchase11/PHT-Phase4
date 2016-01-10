package com.example.pht;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pht.database.glucoseMealDBHelper;
 
public class glucose extends Fragment implements OnClickListener {
    Button b;
    EditText gl;

    glucoseMealDBHelper glucoseDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.glucose_layout, container, false);

        // Assign glucose DB
        MainActivity activity = (MainActivity)getActivity();
        glucoseDb = activity.glucoseMealDb;

        b = (Button) rootView.findViewById(R.id.glSubmit);
        gl = (EditText) rootView.findViewById(R.id.glucoseText);

        b.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View view) {
        if(!gl.getText().toString().isEmpty())
            alertMessage();
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        glucoseDb.logGlucose(Integer.parseInt(gl.getText().toString()));
                        Toast.makeText(getActivity(), "Glucose Recorded",
                                Toast.LENGTH_LONG).show();

                        gl.setText("");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage("Record Glucose?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
