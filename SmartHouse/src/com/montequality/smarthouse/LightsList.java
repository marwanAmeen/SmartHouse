package com.montequality.smarthouse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.montequality.smarthouse.entity.Light;
import com.montequality.smarthouse.tasks.OnOffTask;
import com.montequality.smarthouse.util.CustomListAdapter;
import com.montequality.smarthouse.util.SharedPrefs;
import com.montequality.smarthouse.util.SoundAndVibration;

public class LightsList extends ListActivity {

    public CustomListAdapter adapter;

    List<Light> lightList = new ArrayList<Light>();
    List<Integer> drawableLeft = new ArrayList<Integer>();
    List<Integer> drawableRight = new ArrayList<Integer>();

    private SharedPrefs sharedPrefs;
    private SoundAndVibration soundAndVibra;

    OnOffTask onOffTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	// Show the Up button in the action bar.
	setupActionBar();

	sharedPrefs = new SharedPrefs(this);
	soundAndVibra = new SoundAndVibration(sharedPrefs, this);

	if (getIntent().getStringExtra("jsonDevices") != null) {
	    getDevicesFromJSON();
	}

	List<String> lightsString = new ArrayList<String>();
	for (int i = 0; i < lightList.size(); i++) {
	    lightsString.add(lightList.get(i).getRoom());
	    drawableLeft.add(R.drawable.lightbuble_left);
	    if (lightList.get(i).isPower()) {
		drawableRight.add(R.drawable.lightbuble_right_on);
	    } else {
		drawableRight.add(R.drawable.lightbuble_right_off);
	    }
	}

	adapter = new CustomListAdapter(this, lightsString, drawableLeft, drawableRight);
	setListAdapter(adapter);

	ListView list = getListView();
	ColorDrawable blue = new ColorDrawable(this.getResources().getColor(R.color.blue));
	list.setDivider(blue);
	list.setDividerHeight(1);
	list.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.lights_list, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    // This ID represents the Home or Up button. In the case of this
	    // activity, the Up button is shown. Use NavUtils to allow users
	    // to navigate up one level in the application structure. For
	    // more details, see the Navigation pattern on Android Design:
	    //
	    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
	    //
	    onBackPressed();
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

	soundAndVibra.playSoundAndVibra();

	onOffTask = new OnOffTask(this, lightList.get(position).getId(), "Light", lightList.get(position).getRoom(), adapter, position);
	onOffTask.execute((Void) null);

    }

    private void getDevicesFromJSON() {

	JSONObject jsonObj = null;
	JSONArray jArray = null;

	try {

	    jsonObj = new JSONObject(getIntent().getStringExtra("jsonDevices"));
	    jArray = jsonObj.getJSONArray("lightList");

	    for (int i = 0; i < jArray.length(); i++) {

		JSONObject json_data;

		json_data = jArray.getJSONObject(i);

		Light light_single = new Light(json_data.getInt("id"), json_data.getBoolean("power"), json_data.getString("room"));

		lightList.add(light_single);
	    }

	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    @Override
    protected void onResume() {

	soundAndVibra = new SoundAndVibration(sharedPrefs, this);
	super.onResume();
    }

}
