package com.orbotix.sample.multiplayerlobby;

import java.io.Console;

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
	

	public void onCloseClickWithSaving(View v){
		EditText name = (EditText) findViewById(R.id.name_field);
		
		String nameText = name.getText().toString();
		
		// checks to see if the name is empty or nothing but spaces
		if(nameText.trim().isEmpty()){
			finish();
			return;
		}
		
		// Saves the name
		HomePage.yourName = nameText;
		prefEditor.putString("USER_NAME", HomePage.yourName);
		prefEditor.commit();
		
		finish();
	}
	
	public void onCloseClickNoSaving(View v){
		finish();		
	}
}
