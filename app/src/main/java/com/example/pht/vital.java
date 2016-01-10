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

import com.example.pht.database.vitalDBHelper;

public class vital extends Fragment implements OnClickListener{
    Button b;
    EditText bp, tm, pl, rp, sp;

    vitalDBHelper vitalDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Assign vital DB
        MainActivity activity = (MainActivity)getActivity();
        vitalDbHelper = activity.vitalDb;

        View rootView = inflater.inflate(R.layout.vitals_layout, container, false);

        bp = (EditText) rootView.findViewById(R.id.bloodPresureText);
        tm = (EditText) rootView.findViewById(R.id.temperatureText);
        pl = (EditText) rootView.findViewById(R.id.pulseText);
        rp = (EditText) rootView.findViewById(R.id.respText);
        sp = (EditText) rootView.findViewById(R.id.sp02Text);

        b = (Button) rootView.findViewById(R.id.submit);
        b.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View view) {
        if(!bp.getText().toString().isEmpty() || !tm.getText().toString().isEmpty() ||
                !pl.getText().toString().isEmpty() || !rp.getText().toString().isEmpty() ||
                !sp.getText().toString().isEmpty())
            alertMessage();
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked

                        if(!bp.getText().toString().isEmpty())
                            vitalDbHelper.logVital("Blood Pressure", Integer.parseInt(bp.getText().toString()));

                        if(!tm.getText().toString().isEmpty())
                            vitalDbHelper.logVital("Temperature", Integer.parseInt(tm.getText().toString()));

                        if(!pl.getText().toString().isEmpty())
                            vitalDbHelper.logVital("Pulse", Integer.parseInt(pl.getText().toString()));

                        if(!rp.getText().toString().isEmpty())
                            vitalDbHelper.logVital("Respirations", Integer.parseInt(rp.getText().toString()));

                        if(!sp.getText().toString().isEmpty())
                            vitalDbHelper.logVital("Sp02", Integer.parseInt(sp.getText().toString()));

                        Toast.makeText(getActivity(), "Vitals Recorded",
                                Toast.LENGTH_LONG).show();

                        bp.setText("");
                        tm.setText("");
                        pl.setText("");
                        rp.setText("");
                        sp.setText("");

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage("Record Vitals?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}