package com.orbotix.sample.multiplayerlobby;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class OptionsMenu extends Activity {
	private SharedPreferences gameSettings;
	private SharedPreferences.Editor prefEditor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_menu);
		gameSettings = getSharedPreferences(HomePage.PREFERENCE_FILENAME, MODE_PRIVATE);
		prefEditor = gameSettings.edit();
		
		TextView playerName = (TextView)findViewById(R.id.player_name);
		playerName.setText(HomePage.yourName);
	}
	
	public void onSaveNameClick(View v){

		EditText name = (EditText) findViewById(R.id.name_field);
		
		HomePage.yourName = name.getText().toString();
		
		//call functions to check for spaces and errors in name
		
		prefEditor.putString("USER_NAME", HomePage.yourName);
		prefEditor.commit();
	}

	
	public void onCloseClick(View v){
		finish();
	}
}
