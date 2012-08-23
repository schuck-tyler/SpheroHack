package com.orbotix.sample.multiplayerlobby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomePage extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
    }
	
	public void onCreateGameInstance(View v){
        Intent i = new Intent(this, AvailableGamesActivity.class);
        startActivity(i);
	}
}
