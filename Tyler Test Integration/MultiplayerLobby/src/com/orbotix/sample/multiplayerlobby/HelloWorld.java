package com.orbotix.sample.multiplayerlobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import orbotix.macro.BackLED;
import orbotix.multiplayer.LocalMultiplayerClient;
import orbotix.multiplayer.RemotePlayer;
import orbotix.robot.app.StartupActivity;
import orbotix.robot.base.DeviceAsyncData;
import orbotix.robot.base.DeviceMessenger;
import orbotix.robot.base.DeviceSensorsAsyncData;
import orbotix.robot.base.FrontLEDOutputCommand;
import orbotix.robot.base.RGBLEDOutputCommand;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotControl;
import orbotix.robot.base.RobotProvider;
import orbotix.robot.base.RollCommand;
import orbotix.robot.base.SetDataStreamingCommand;
import orbotix.robot.base.TiltDriveAlgorithm;
import orbotix.robot.sensor.DeviceSensorsData;
import orbotix.robot.sensor.LocatorData;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Connects to an available Sphero robot, and then flashes its LED.
 */
public class HelloWorld extends Activity
{
	private final static int TOTAL_PACKET_COUNT = 200;
	private final static int PACKET_COUNT_THRESHOLD = 50;
	private int mPacketCounter;

	private final static int BOUNDARY_DISTANCE_FROM_CENTER_CM = 2000;
	private final static int BACK_IN_BOUNDS_FROM_CENTER_CM = 140;
	private boolean roll_back = false;

	/**
	 * ID for launching the StartupActivity for result to connect to the robot
	 */
	private final static int STARTUP_ACTIVITY = 0;

	/**
	 * The Sphero Robot
	 */
	private Robot mRobot;
	private RobotControl robot_control;

	private SensorManager sensor_manager;
	private Sensor accelerometer;

	public static Map<String, ArrayList<Float>> player_values;
	public static LocalMultiplayerClient mMultiplayerClient;
	public static String own_player = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);
		this.accelerometer = this.sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		if(mMultiplayerClient != null) {
			// On data recieved
			mMultiplayerClient.setOnGameDataReceivedListener(new LocalMultiplayerClient.OnGameDataReceivedListener() {
				@Override
				public void onGameDataReceived(Context context, JSONObject game_data, RemotePlayer sender) {

					if(game_data.has("CHAT")){

						try {
							//addChatMessage(sender.getName(), game_data.getString("CHAT"));
							String[] acceleration = game_data.getString("CHAT").split(" ");
							for(int i = 0; i < acceleration.length; i++) {
								player_values.get(sender.getName()).set(i, Float.parseFloat(acceleration[i]));
							}
						} catch (JSONException e) {
							Log.e(AvailableGamesActivity.TAG, "Failed to get chat message from game data.", e);
						}
					}
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		//Launch the StartupActivity to connect to the robot
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
				mRobot = RobotProvider.getDefaultProvider().findRobot(robot_id);
				this.robot_control = RobotProvider.getDefaultProvider().getRobotControl(mRobot);
				this.robot_control.setDriveAlgorithm(new TiltDriveAlgorithm());
				sensor_manager.registerListener(this.accelerometer_listener, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);

				FrontLEDOutputCommand.sendCommand(mRobot, 255.0f);
				requestDataStreaming();

				//Set the AsyncDataListener that will process each response.
				DeviceMessenger.getInstance().addAsyncDataListener(mRobot, mDataListener);
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		robot_control.stopMotors();
		FrontLEDOutputCommand.sendCommand(mRobot, 0);
		mRobot = null;

		//Disconnect Robot
		RobotProvider.getDefaultProvider().removeAllControls();
	}
	
	private SensorEventListener accelerometer_listener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			System.out.print("In onSensorChanged, ");
			if(mRobot == null || event == null)
				return;
			System.out.print("(mRobot != null && event != null), ");
			if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
				return;
			System.out.print("event is from accelerometer, ");
			if (roll_back)
				return;
			System.out.print("(!roll_back)");
			System.out.println();


			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			if(player_values != null) {
				int num_players = player_values.size();
				for (String player : player_values.keySet()) {
					x += player_values.get(player).get(0);
					y += player_values.get(player).get(1);
					z += player_values.get(player).get(2);
				}
				x /= num_players;
				y /= num_players;
				z /= num_players;
			}

			robot_control.drive(x, y, z);
			//System.out.println ("x: " + Float.toString(x) + ", y: " + Float.toString(y) + ", z: " + Float.toString(z));
		}
		
	};

	/**
	 * AsyncDataListener that will be assigned to the DeviceMessager, listen for streaming data, and then do the
	 *
	 */
	private DeviceMessenger.AsyncDataListener mDataListener = new DeviceMessenger.AsyncDataListener() {
		@Override
		public void onDataReceived(DeviceAsyncData data) {

			if(data instanceof DeviceSensorsAsyncData){

				// If we are getting close to packet limit, request more
				mPacketCounter++;
				if( mPacketCounter > (TOTAL_PACKET_COUNT - PACKET_COUNT_THRESHOLD) ) {
					requestDataStreaming();
				}

				//get the frames in the response
				List<DeviceSensorsData> data_list = ((DeviceSensorsAsyncData)data).getAsyncData();
				if(data_list != null){

					// Iterate over each frame, however we set data streaming as only one frame
					for(DeviceSensorsData datum : data_list){

						LocatorData locatorData = datum.getLocatorData();
						if( locatorData != null ) {
							System.out.println("roll_command: " + Boolean.toString(roll_back) + " x: " + Float.toString(locatorData.getPositionX()) + " y: " + Float.toString(locatorData.getPositionY()));
							if (roll_back) {
								if (locatorData.getPositionX() < BACK_IN_BOUNDS_FROM_CENTER_CM
										&& locatorData.getPositionX() > -BACK_IN_BOUNDS_FROM_CENTER_CM
										&& locatorData.getPositionY() < BACK_IN_BOUNDS_FROM_CENTER_CM
										&& locatorData.getPositionY() > -BACK_IN_BOUNDS_FROM_CENTER_CM) {
									RollCommand.sendStop(mRobot);
									roll_back = false;
								}
							}

							else if (locatorData.getPositionX() > BOUNDARY_DISTANCE_FROM_CENTER_CM
									|| locatorData.getPositionX() < -BOUNDARY_DISTANCE_FROM_CENTER_CM
									|| locatorData.getPositionY() > BOUNDARY_DISTANCE_FROM_CENTER_CM
									|| locatorData.getPositionY() < -BOUNDARY_DISTANCE_FROM_CENTER_CM) {
								//FrontLEDOutputCommand.sendCommand(mRobot, 255);
								RollCommand.sendStop(mRobot);
								if (!roll_back) {
									// Roll back into bounds

									// Find current angle
									double angle = Math.atan2((double) locatorData.getPositionY(), (double) locatorData.getPositionX());
									angle = 90 - Math.toDegrees(angle);

									// Invert
									if (angle < 180)
										angle += 180;
									else
										angle -= 180;

									roll_back = true;
									//RollCommand.sendCommand(mRobot, (int) angle, 0.6f);
									robot_control.roll((float)angle, 0.6f);
								}

							} else {
								FrontLEDOutputCommand.sendCommand(mRobot, 0);
							}
						}
					}
				}

			}
		}

	};

	private void requestDataStreaming(){

		if(mRobot == null) return;

		// Set up a bitmask containing the sensor information we want to stream, in this case locator
		// with which only works with Firmware 1.20 or greater.
		final long mask = SetDataStreamingCommand.DATA_STREAMING_MASK_LOCATOR_ALL;

		//Specify a divisor. The frequency of responses that will be sent is 400hz divided by this divisor.
		final int divisor = 50;

		//Specify the number of frames that will be in each response. You can use a higher number to "save up" responses
		//and send them at once with a lower frequency, but more packets per response.
		final int packet_frames = 1;

		// Reset finite packet counter
		mPacketCounter = 0;

		// Count is the number of async data packets Sphero will send you before
		// it stops. You want to register for a finite count and then send the command
		// again once you approach the limit. Otherwise data streaming may be left
		// on when your app crashes, putting Sphero in a bad state
		final int response_count = TOTAL_PACKET_COUNT;


		// Send this command to Sphero to start streaming.
		// If your Sphero is on Firmware less than 1.20, Locator values will display as 0's
		SetDataStreamingCommand.sendCommand(mRobot, divisor, packet_frames, mask, response_count);
	}
}