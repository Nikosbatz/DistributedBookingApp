package com.example.myapplication.frontend;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        System.out.println("asdasda");

        String text = getIntent().getExtras().getString("text");
        TextView onScreenText = findViewById(R.id.myText);

        // Print the user input text from previous screen
        onScreenText.setText(text);
        ImageView img = findViewById(R.id.photo);
        img.setImageResource(R.drawable.room1);

        listView = findViewById(R.id.listview);

        ArrayList<String> items = new ArrayList<>();
        items.add("First Item");
        items.add("Second Item");
        items.add("Third Item");


        /*ListingsAdapter adapter = new ListingsAdapter(this,items);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Toast.makeText(ImageActivity.this, "Clicked: "+i, Toast.LENGTH_SHORT).show();
            }

        });*/

    }
}
