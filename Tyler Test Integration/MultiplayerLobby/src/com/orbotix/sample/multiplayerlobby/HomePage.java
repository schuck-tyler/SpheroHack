package com.orbotix.sample.multiplayerlobby;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class HomePage extends Activity{
	public static final String PREFERENCE_FILENAME = "AppGamePrefs";
	private SharedPreferences gameSettings;
	public static String yourName;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        
        gameSettings = getSharedPreferences(PREFERENCE_FILENAME, MODE_PRIVATE);
        yourName = gameSettings.getString("USER_NAME", yourName);
    }
	
	public void onCreateGameInstance(View v){
        Intent i = new Intent(this, AvailableGamesActivity.class);
        startActivity(i);
	}
	
    public void launchOptions(View v){
    	Intent i = new Intent(this, OptionsMenu.class);
    	startActivity(i);
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	if(yourName.trim().isEmpty()){
    		Intent i = new Intent(this, OptionsMenu.class);
    		startActivity(i);
    	}
    }
}
