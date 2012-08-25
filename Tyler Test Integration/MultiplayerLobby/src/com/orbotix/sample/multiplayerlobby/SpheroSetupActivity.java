package com.orbotix.sample.multiplayerlobby;

import orbotix.robot.app.StartupActivity;
import orbotix.robot.base.DeviceMessenger;
import orbotix.robot.base.FrontLEDOutputCommand;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotProvider;
import orbotix.robot.base.TiltDriveAlgorithm;
import orbotix.robot.widgets.ControllerActivity;
import orbotix.robot.widgets.calibration.CalibrationView;
import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SpheroSetupActivity extends ControllerActivity {
	public static Robot robot;
	private final static int STARTUP_ACTIVITY = 0;
	public static String EXTRA_USER_NAME;
	
	@Override
	public void onStart() {
		super.onStart();
		Intent i = new Intent(this, StartupActivity.class);
		startActivityForResult(i, STARTUP_ACTIVITY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == STARTUP_ACTIVITY && resultCode == RESULT_OK){

			//Get the connected Robot
			final String robot_id = data.getStringExtra(StartupActivity.EXTRA_ROBOT_ID);
			if(robot_id != null && !robot_id.equals("")){
				robot = RobotProvider.getDefaultProvider().findRobot(robot_id);
				this.setRobot(robot);
				FrontLEDOutputCommand.sendCommand(robot, 255.0f);
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sphero_setup);
		addController((CalibrationView)findViewById(R.id.calibration));
	}
	
	@Override
	public void onStop() {
		super.onStop();
		FrontLEDOutputCommand.sendCommand(robot, 0);
		robot = null;

		//Disconnect Robot
		RobotProvider.getDefaultProvider().removeAllControls();
	}
	
	public void onDone(View v) {
		Intent i = new Intent(this, LobbyActivity.class);
        i.putExtra(LobbyActivity.EXTRA_ACTION, LobbyActivity.EXTRA_ACTION_HOST_GAME);
        i.putExtra(LobbyActivity.EXTRA_USER_NAME, EXTRA_USER_NAME);
        LobbyActivity.robot = robot;
        startActivity(i);
	}
}
