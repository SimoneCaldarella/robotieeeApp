package com.example.simone.robotieee;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.Serializable;

public class Options extends AppCompatActivity implements View.OnClickListener, Serializable{

    BottonMap map;

    Button startScan;
    TableLayout table;
    int rows, columns, buttonNumber;
    TableRow row[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent r = getIntent();
        rows = Integer.parseInt(r.getStringExtra("rows"));
        columns = Integer.parseInt(r.getStringExtra("columns"));

        map = new BottonMap(rows, columns);

        table = (TableLayout) findViewById(R.id.tableLayout);

        int w = 920 / (columns);
        int h = 920 / rows;
        buttonNumber = 0;

        row = new TableRow[rows];

        for (int i = 0; i < rows; i++){

            row[i] = new TableRow(this);

            for (int l = 0; l < columns; l++){

                map.botton[i][l].button = new Button(this);
                map.botton[i][l].button.setOnClickListener(this);
                map.botton[i][l].button.setText("");
                android.widget.TableRow.LayoutParams lp = new TableRow.LayoutParams(w, h);
                map.botton[i][l].button.setLayoutParams(lp);
                map.botton[i][l].button.setId(buttonNumber);
                buttonNumber++;

                row[i].addView(map.botton[i][l].button);

            }

            table.addView(row[i], new TableLayout.LayoutParams());

        }

        startScan = (Button) findViewById(R.id.startScan);
        startScan.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {

        Button button = (Button) findViewById(view.getId());
        BottonMap.Botton botton = null;

        for (int i = 0; i < rows; i++){

            for (int m = 0; m < columns; m++){

                if (map.botton[i][m].button.getId() == button.getId()){

                    botton = map.botton[i][m];

                }

            }

        }

        if (view.getId() == R.id.startScan){
            showAlert();
        } else {

            botton.changeStatus(botton);

        }

    }

    public void showAlert(){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Are you sure you want to start scan?");
        dlgAlert.setTitle("Start scan");

        dlgAlert.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        apriActivity();
                    }
                });
        dlgAlert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void apriActivity(){

        Intent start = new Intent(Options.this, Scan.class);
        start.putExtra("map", map);
        start.putExtra("rows", rows);
        start.putExtra("columns", columns);
        startActivity(start);
    }

}
