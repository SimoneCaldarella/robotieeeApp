package com.example.simone.robotieee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * In this activity you set the dimension of the map;
 */

public class SetMap extends AppCompatActivity implements View.OnClickListener{

    Button setMap;
    EditText rows, columns;

    String row, column;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_map);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setMap = (Button) findViewById(R.id.SetMap);
        setMap.setOnClickListener(this);

        rows = (EditText) findViewById(R.id.Rows);
        columns = (EditText) findViewById(R.id.Columns);

    }

    /**
     * Pressing the button you send the dimension to the next acitivity;
     * @param view
     */

    @Override
    public void onClick(View view) {

        row = rows.getText().toString();
        column = columns.getText().toString();

        Intent start = new Intent(SetMap.this, Options.class);
        start.putExtra("rows", row);
        start.putExtra("columns", column);
        startActivity(start);

    }

}
