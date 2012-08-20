package com.orbotix.sample.multiplayerlobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import orbotix.macro.BackLED;
import orbotix.robot.app.StartupActivity;
import orbotix.robot.base.FrontLEDOutputCommand;
import orbotix.robot.base.RGBLEDOutputCommand;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotControl;
import orbotix.robot.base.RobotProvider;
import orbotix.robot.base.TiltDriveAlgorithm;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
* Connects to an available Sphero robot, and then flashes its LED.
*/
public class HelloWorld extends Activity implements SensorEventListener
{
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
    
    private Float[] endurance = new Float[3];
    
    Map<String, ArrayList<Float>> player_values;
    
    // For endurance
    long time_x;
    long time_y;
    boolean pos_x;
    boolean pos_y;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
     this.sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);
    
     /*List<Sensor> sensors = sensor_manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
Sensor s;
if (!sensors.isEmpty())
s = sensors.get(0);*/
    
     this.accelerometer = this.sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
     player_values = new HashMap<String, ArrayList<Float>>();
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
             sensor_manager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
             endurance[0] = endurance[1] = endurance[2] = 0.0f;
                FrontLEDOutputCommand.sendCommand(mRobot, 255.0f);
            }
            
            ArrayList<Float> vals = new ArrayList<Float>();
            vals.add(0.0f);
            vals.add(0.0f);
            vals.add(0.0f);
            player_values.put("Derp", vals);
         
            //Start blinking
            blink(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        robot_control.stopMotors();
        mRobot = null;
        
        //Disconnect Robot
        RobotProvider.getDefaultProvider().removeAllControls();
    }

    /**
* Causes the robot to blink once every second.
* @param lit
*/
    private void blink(final boolean lit){
        
        if(mRobot != null){
            
            //If not lit, send command to show blue light, or else, send command to show no light
            if(lit){
                RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 0);
            }else{
                RGBLEDOutputCommand.sendCommand(mRobot, 0, 0, 255);
            }
            
            //Send delayed message on a handler to run blink again
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    blink(!lit);
                }
            }, 1000);
        }
    }

@Override
public void onAccuracyChanged(Sensor arg0, int arg1) {
// TODO Auto-generated method stub

}

@Override
public void onSensorChanged(SensorEvent event) {
if(mRobot == null || event == null)
return;
if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
return;

float x = event.values[0];
float y = event.values[1];
float z = event.values[2];

int num_players = player_values.size();
for (String player : player_values.keySet()) {
x += player_values.get(player).get(0);
y += player_values.get(player).get(1);
z += player_values.get(player).get(2);
}
x /= num_players;
y /= num_players;
z /= num_players;

robot_control.drive(x, y, z);
System.out.println ("x: " + Float.toString(x) + ", y: " + Float.toString(y) + ", z: " + Float.toString(z));
}
}