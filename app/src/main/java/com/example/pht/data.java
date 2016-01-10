package com.example.pht;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.pht.database.exerciseDBHelper;
import com.example.pht.database.glucoseMealDBHelper;
import com.example.pht.database.vitalDBHelper;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class data extends Fragment implements OnClickListener{

    Button b;
    Spinner data, timeframe;
    GraphView graph;

    glucoseMealDBHelper glucoseMealDb;
    exerciseDBHelper exerciseDb;
    vitalDBHelper vitalDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.data_layout, container, false);

        b = (Button) rootView.findViewById(R.id.data_Submit);
        data = (Spinner) rootView.findViewById(R.id.spinner1);
        timeframe = (Spinner) rootView.findViewById(R.id.spinner2);
        graph = (GraphView) rootView.findViewById(R.id.graph);

        b.setOnClickListener(this);

        // Assign databases
        MainActivity activity = (MainActivity)getActivity();
        glucoseMealDb = activity.glucoseMealDb;
        exerciseDb = activity.exerciseDb;
        vitalDb = activity.vitalDb;

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.data_options, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        data.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.timeframe_options, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeframe.setAdapter(adapter2);

        return rootView;
    }

    public void onClick(View view) {
        String dataType = data.getSelectedItem().toString();
        Integer range = Integer.parseInt(timeframe.getSelectedItem().toString());

        graph.removeAllSeries();

        HashMap<Integer, Integer> dataHm = new HashMap<Integer, Integer>();

        switch (dataType) {
            case "Calories In":
                dataHm = glucoseMealDb.getCalories(range);
                break;
            case "Calories Burned":
                dataHm = exerciseDb.getCalories(range);
                break;
            case "Glucose":
                dataHm = glucoseMealDb.getGlucose(range);
                break;
            case "Blood Pressure":
                dataHm = vitalDb.getVitals(range, vitalDb.getVital("Blood Pressure"));
                break;
            case "Temperature":
                dataHm = vitalDb.getVitals(range, vitalDb.getVital("Temperature"));
                break;
            case "Pulse":
                dataHm = vitalDb.getVitals(range, vitalDb.getVital("Pulse"));
                break;
            case "Respiration":
                dataHm = vitalDb.getVitals(range, vitalDb.getVital("Respiration"));
                break;
            case "Sp02":
                dataHm = vitalDb.getVitals(range, vitalDb.getVital("Sp02"));
                break;
        }

        List<DataPoint> points = new ArrayList<>();

        for(int i=range-1; i>0; i--) {
            int value = dataHm.get(i);
            if(value != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, i*-1);
                DataPoint point = new DataPoint(calendar.getTime(), value);
                points.add(point);
            }
        }

        if(points.isEmpty())
            return;

        DataPoint[] dpArr = new DataPoint[points.size()];
        dpArr = points.toArray(dpArr);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dpArr);
        graph.addSeries(series);

        // Not sure why, but I have to graph this second series to make the graph not break.
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0)
        });
        //graph.addSeries(series2);
        graph.setTitle(dataType);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(dpArr[0].getX());
        graph.getViewport().setMaxX(dpArr[dpArr.length-1].getX());
        graph.getViewport().setXAxisBoundsManual(true);

    }


}