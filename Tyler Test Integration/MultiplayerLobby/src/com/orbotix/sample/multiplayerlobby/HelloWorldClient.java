//package com.orbotix.sample.multiplayerlobby;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.Display;
//import orbotix.macro.BackLED;
//import orbotix.multiplayer.LocalMultiplayerClient;
//import orbotix.robot.app.StartupActivity;
//import orbotix.robot.base.DeviceAsyncData;
//import orbotix.robot.base.DeviceMessenger;
//import orbotix.robot.base.DeviceSensorsAsyncData;
//import orbotix.robot.base.FrontLEDOutputCommand;
//import orbotix.robot.base.RGBLEDOutputCommand;
//import orbotix.robot.base.Robot;
//import orbotix.robot.base.RobotControl;
//import orbotix.robot.base.RobotProvider;
//import orbotix.robot.base.RollCommand;
//import orbotix.robot.base.SetDataStreamingCommand;
//import orbotix.robot.base.TiltDriveAlgorithm;
//import orbotix.robot.sensor.DeviceSensorsData;
//import orbotix.robot.sensor.LocatorData;
//import orbotix.robot.widgets.calibration.CalibrationView;
//import android.graphics.Point;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//
///**
// * Connects to an available Sphero robot, and then flashes its LED.
// */
//public class HelloWorldClient extends Activity implements SensorEventListener
//{
//
//	private final static int TOTAL_PACKET_COUNT = 200;
//	private final static int PACKET_COUNT_THRESHOLD = 50;
//	private int mPacketCounter;
//
//	private final static int BOUNDARY_DISTANCE_FROM_CENTER_CM = 160;
//	private final static int BACK_IN_BOUNDS_FROM_CENTER_CM = 140;
//	private boolean roll_back = false;
//
//	/**
//	 * ID for launching the StartupActivity for result to connect to the robot
//	 */
//	private final static int STARTUP_ACTIVITY = 0;
//
//	/**
//	 * The Sphero Robot
//	 */
//	//private Robot mRobot;
//	//private RobotControl robot_control;
//
//	private SensorManager sensor_manager;
//	private Sensor accelerometer;
//
//	public static Map<String, ArrayList<Float>> player_values;
//	public static LocalMultiplayerClient mMultiplayerClient;
//
//	/** World Variables */
//	//public World world;
//	public static String[] players = new String[4];
//	public static String own_player = ""; 
//	
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//		Display display = getWindowManager().getDefaultDisplay();
//		this.sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);
//		this.accelerometer = this.sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//	}
//	
//	private void sendChatMessage(String message){
//        
//        JSONObject json = new JSONObject();
//        
//        try {
//            json.put("CHAT", message);
//        } catch (JSONException e) {
//            throw new RuntimeException("Failed to send game data.", e);
//        }
//
//        mMultiplayerClient.sendGameDataToAll(json);
//    }
//
//	@Override
//	protected void onStart() {
//		//super.onStart();
//
//		//Launch the StartupActivity to connect to the robot
//		//Intent i = new Intent(this, StartupActivity.class);
//		//startActivityForResult(i, STARTUP_ACTIVITY);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if(requestCode == STARTUP_ACTIVITY && resultCode == RESULT_OK){
//
//			//Get the connected Robot
//			final String robot_id = data.getStringExtra(StartupActivity.EXTRA_ROBOT_ID);
//			if(robot_id != null && !robot_id.equals("")){
//				//mRobot = RobotProvider.getDefaultProvider().findRobot(robot_id);
//				//this.robot_control = RobotProvider.getDefaultProvider().getRobotControl(mRobot);
//				//this.robot_control.setDriveAlgorithm(new TiltDriveAlgorithm());
//				sensor_manager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
//				//FrontLEDOutputCommand.sendCommand(mRobot, 255.0f);
//				//requestDataStreaming();
//
//				//Set the AsyncDataListener that will process each response.
//				//DeviceMessenger.getInstance().addAsyncDataListener(mRobot, mDataListener);
//			}
//			
//
//			//Start blinking
//			blink(false);
//		}
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		//robot_control.stopMotors();
//		//mRobot = null;
//
//		//Disconnect Robot
//		RobotProvider.getDefaultProvider().removeAllControls();
//	}
//
//	/**
//	 * Causes the robot to blink once every second.
//	 * @param lit
//	 */
//	private void blink(final boolean lit){
//
//		/*if(mRobot != null){
//
//			//If not lit, send command to show blue light, or else, send command to show no light
//			if(lit){
//				RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 0);
//			}else{
//				RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 255);
//			}
//
//			//Send delayed message on a handler to run blink again
//			final Handler handler = new Handler();
//			handler.postDelayed(new Runnable() {
//				public void run() {
//					blink(!lit);
//				}
//			}, 1000);
//		}*/
//	}
//
//	@Override
//	public void onAccuracyChanged(Sensor arg0, int arg1) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onSensorChanged(SensorEvent event) {
//		//if(mRobot == null || event == null)
//			//return;
//		if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
//			return;
//		if (roll_back)
//			return;
//			
//		float x = event.values[0];
//		float y = event.values[1];
//		float z = event.values[2];
//
//		int num_players = player_values.size();
//		for (String player : player_values.keySet()) {
//			x += player_values.get(player).get(0);
//			y += player_values.get(player).get(1);
//			z += player_values.get(player).get(2);
//		}
//		x /= num_players;
//		y /= num_players;
//		z /= num_players;
//		//world.update(//robot location);
//		//robot_control.drive(x, y, z);
//		this.sendChatMessage(Float.toString(x)+" "+Float.toString(y)+" "+Float.toString(z));
//		System.out.println ("x: " + Float.toString(x) + ", y: " + Float.toString(y) + ", z: " + Float.toString(z));
//	}
//}