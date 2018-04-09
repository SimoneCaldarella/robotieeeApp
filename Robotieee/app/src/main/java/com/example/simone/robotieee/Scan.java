package com.example.simone.robotieee;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the main activity where you have 3 times. In the first one you press the button, you send the info to the exploration_solver
 * and you receive the moves to the robot;
 * once the exploration is finished you receive from the robot the position of the block and you can press again to send the new
 * information to the server.
 * After this you will receive other moves by the sokoban solver to complete the task;
 */


public class Scan extends AppCompatActivity implements View.OnClickListener{

    public String logText, posX, posY, jsonObj;

    MyJSONReply command;

    Button scan;
    TextView wifiStat, batteryStat, logConsole;
    Drawable green, red, best, good, bad, worst;

    World world;
    ArrayList<Cells> arrayList;
    JsonObjSend jsonObjSend;
    HTTPrequest request;
    BottonMap map;

    int rows, columns, version;

    Boolean readData, created, explorationFinished, sokobanFinished;

    NetworkInfo mWifi;
    ConnectivityManager connManager;

    /**
     * The bundle is used to receive all of the map from the
     */

    Bundle bundle;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent rs = getIntent();
        map = (BottonMap) rs.getSerializableExtra("map"); //you receive the serialized map;
        rows = rs.getIntExtra("rows", rows);
        columns = rs.getIntExtra("columns", columns);

        version = 0;

        scan = (Button) findViewById(R.id.Scan);
        wifiStat = (TextView) findViewById(R.id.ServerStat);

        batteryStat = (TextView) findViewById(R.id.Battery);
        logConsole = (TextView) findViewById(R.id.logConsole);

        scan.setOnClickListener(this);

        /**
         * Led status
         */

        green = getResources().getDrawable(R.drawable.green);
        red = getResources().getDrawable(R.drawable.red);

        /**
         * Battery status
         */

        best = getResources().getDrawable(R.drawable.best);
        good = getResources().getDrawable(R.drawable.good);
        bad = getResources().getDrawable(R.drawable.bad);
        worst = getResources().getDrawable(R.drawable.worst);

        /**
         * This methods are call first here to set the battery image;
         */

        receiveBattery();
        setBattery();
        setWifiStat();

        request = new HTTPrequest();
        arrayList = new ArrayList<>();

        readData = true;
        created = false;
        explorationFinished = false;
        sokobanFinished = false;

    }

    @Override
    public void onClick(View view) {

        setWifiStat();

        new Thread(new Runnable() {
            public void run() {

                //request.receive();

                if (!explorationFinished) {

                    createJsonForExploration();

                    try {
                        command = request.send(jsonObjSend);//Here you send the jsonObj with the map to the server to plan the explanation;

                        if (command != null) {
                            created = true;//here you set that he received the list of moves;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (created) {
                                    updateLogConsole();//Here are shown the list of moves;
                                    scan.setText("STOP");
                                    if (explorationFinished) {
                                        scan.setText("START TASKS");
                                    }
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (explorationFinished && !sokobanFinished){

                    version = 0;

                    createJsonForSokoban();

                }

            }
        }).start();

    }

    void addArray(int x, int y, String ent){

        /**
         * This method has been made only to simplify the arraylist's add method
         */

        arrayList.add (new Cells(x, y, ent));

    }

    void createJsonForExploration(){

        for (int y = 0; y < rows; y++){
            for (int x = 0; x < columns; x++){

                String ent = map.botton[y][x].attr;
                addArray(x, y, ent);

            }
        }

        world = new World(rows, columns, arrayList.toArray(new Cells[arrayList.size()]));
        jsonObjSend = new JsonObjSend("1"+version, world);

        version++;

        arrayList.clear();
    }

    void createJsonForSokoban(){

        for (int y = 0; y < rows; y++){
            for (int x = 0; x < columns; x++){

                String ent = map.botton[y][x].attr;
                addArray(x, y, ent);

            }
        }

        world = new World(rows, columns, arrayList.toArray(new Cells[arrayList.size()]));
        jsonObjSend = new JsonObjSend("1"+version, world);

        version++;

        arrayList.clear();
    }

    private void setWifiStat(){

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            wifiStat.setBackground(green);
        } else {
            wifiStat.setBackground(red);
        }

    }

    public void updateLogConsole(){

        for (int i = 0; i < command.actions.length; i++){

            logConsole.setText(command.actions[i].direction + "\n" + logConsole.getText());

        }

        logConsole.setText("\n~~~ Exploration solution " + "~~~" + "\n\n" + logConsole.getText());

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setBattery(){

        /**
         * This function receive battery status and set it for the user
         */

        float r = getRobotCharge();

        if (r<=25){
            batteryStat.setBackground(worst);
        } else if (r<=50){
            batteryStat.setBackground(bad);
        } else if (r<=75){
            batteryStat.setBackground(good);
        } else if (r<=100){
            batteryStat.setBackground(best);
        }

    }

    /**
     * Useless function
     */

    void sendMoves(){

        /**
         * Send moves to the robot five by five
         */

        int cont = 0;

        while (cont < command.actions.length && readData == true){

            if (readData == true) {

                readData = false;

                for (; cont % 5 != 0; cont++) {

                    send(command.actions[cont].direction);

                }

                readData = getRobotRead();

            }

        }

    }

    public boolean getRobotRead(){
        /**
         * This function should return true if the robot received and executed all of the five command
         * sent before
         */

        return true;

    }

    public int getRobotCharge(){

        int status = receiveBattery();

        /**
         * Ottieni percentuale
         */

        return status;

    }

    void send(String direction){
        /**
         * This should send data via bluetooth
         */
    }

    public int receiveBattery(){
        /**
         * Receive the battery stat from the robot
         */
        return 28;
    }

}