package com.example.pht;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.example.pht.database.exerciseDBHelper;
import com.example.pht.database.vitalDBHelper;
import com.example.pht.database.glucoseMealDBHelper;

public class MainActivity extends Activity {

	// Declaring our tabs and the corresponding fragments.
	ActionBar.Tab homeTab, bmiTab, vitalTab, glucoseTab, calorieinTab, calorieburnedTab, dataTab;
	Fragment home = new home();
	Fragment bmi =  new bmi();
	Fragment vital = new vital();
	Fragment glucose = new glucose();
	Fragment caloriein = new caloriein();
	Fragment caloriesburned = new caloriesburned();
	Fragment data = new data();

    public exerciseDBHelper exerciseDb;
    public vitalDBHelper vitalDb;
    public glucoseMealDBHelper glucoseMealDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Create the sqlite database object
        exerciseDb = new exerciseDBHelper(this);
        if(exerciseDb.getExerciseCount()==0) {
            exerciseDb.populate();
            exerciseDb.testPopulateExercises();     // Generates test data for the DB
        }

        vitalDb = new vitalDBHelper(this);
        if(vitalDb.getVitalCount()==0) {
            vitalDb.populate();
            vitalDb.testPopulateVitals();           // Generates test data for the DB
        }

        glucoseMealDb = new glucoseMealDBHelper(this);
        if(glucoseMealDb.getMealCount()==0) {
            glucoseMealDb.populate();
            glucoseMealDb.testPopulateGlucose();    // Generates test data for the DB
            glucoseMealDb.testPopulateMeals();      // Generates test data for the DB
        }

        setContentView(R.layout.activity_main);
		
		// Asking for the default ActionBar element that our platform supports.
		ActionBar actionBar = getActionBar();
		 
        // Screen handling while hiding ActionBar icon.
        actionBar.setDisplayShowHomeEnabled(false);
 
        // Screen handling while hiding Actionbar title.
        actionBar.setDisplayShowTitleEnabled(false);
 
        // Creating ActionBar tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        // Setting custom tab icons.
        homeTab = actionBar.newTab().setText("Home");
        bmiTab = actionBar.newTab().setText("BMI Calc");
        vitalTab = actionBar.newTab().setText("Vital Signs");
        glucoseTab = actionBar.newTab().setText("Glucose");
        calorieinTab = actionBar.newTab().setText("Calorie Log");
        calorieburnedTab = actionBar.newTab().setText("Exercise Calc");
        dataTab = actionBar.newTab().setText("View Data");
        
        // Setting tab listeners.
        homeTab.setTabListener(new TabListener(home));
        bmiTab.setTabListener(new TabListener(bmi));
        vitalTab.setTabListener(new TabListener(vital));
        glucoseTab.setTabListener(new TabListener(glucose));
        calorieinTab.setTabListener(new TabListener(caloriein));
        calorieburnedTab.setTabListener(new TabListener(caloriesburned));
        dataTab.setTabListener(new TabListener(data));
       
        // Adding tabs to the ActionBar.
        actionBar.addTab(homeTab);
        actionBar.addTab(bmiTab);
        actionBar.addTab(vitalTab);
        actionBar.addTab(glucoseTab);
        actionBar.addTab(calorieinTab);
        actionBar.addTab(calorieburnedTab);
        actionBar.addTab(dataTab);
	}
}