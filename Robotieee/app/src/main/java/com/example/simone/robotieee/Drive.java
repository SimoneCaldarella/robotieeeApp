package com.example.simone.robotieee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Drive extends AppCompatActivity implements View.OnClickListener {

    /**
     *Drive mode, you use it when you want to control the robot in real-time. There is also a button
     * that you could use anytime you want to switch to robot's real task
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);

        Button go = (Button) findViewById(R.id.go);
        go.setOnClickListener(this);

        Button right = (Button) findViewById(R.id.right);
        right.setOnClickListener(this);

        Button left = (Button) findViewById(R.id.left);
        left.setOnClickListener(this);

        Button retro = (Button) findViewById(R.id.Scan);
        retro.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.stop:

                stop();
                break;

            case R.id.go:

                go();
                break;

            case R.id.right:

                right();
                break;

            case R.id.left:

                left();
                break;

            case R.id.Scan:

                goToScan();
                break;

        }
    }

    void stop(){

        sendViaBluetooth("Stop");

    }

    void go(){

        sendViaBluetooth("Go");

    }

    void right(){

        sendViaBluetooth("turnR");

    }

    void left(){

        sendViaBluetooth("turnL");

    }

    void goToScan(){

        /**
         * This button changes the activity to robot's real task
         */

        Intent scanAct = new Intent(Drive.this, SetMap.class);
        startActivity(scanAct);

    }

    void sendViaBluetooth(String command){

        /**
         * You use this to send the message to the robot to control it
         */

    }

}
