package com.example.kajom.arapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//A simple MENU for different augementation approaches.
public class MainActivity extends ListActivity {

    String classes[] = {"ARCore","FaceTracker"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setListAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, classes));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        String class_activity = null;
        if(classes[position] == "ARCore"){
            class_activity = "ARCore";
        }
        if(classes[position] == "FaceTracker"){
            class_activity = "FaceTracker";
        }
        Class ourClass = null;
        try {
            ourClass = Class.forName("com.example.kajom.arapp." + class_activity);
            Intent ourIntent =new Intent(MainActivity.this, ourClass);
            startActivity(ourIntent);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
