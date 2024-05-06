package com.example.myapplication.frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.backend.src.Entities.AccommodationRoom;
import com.example.myapplication.backend.src.Entities.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        Log.d("ROOMS", "---------");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    socket = new Socket("192.168.2.2", 1234);
                    // Reading objects from Server
                    ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

                    // Writing objects to Server
                    ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

                    // Declaring to Master to use the renter interface
                    objectOut.writeObject("renter");

                    Task task = new Task();
                    task.setMethod("showAllRooms");

                    // Send request to Master
                    objectOut.writeObject(task);

                    // Master Response
                    ArrayList<AccommodationRoom> result = (ArrayList<AccommodationRoom>) objectIn.readObject();

                    Log.d("ROOMS", result.size() + "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyAdapter adapter = new MyAdapter(context, result);
                            listView.setAdapter(adapter);
                        }
                    });



                } catch (IOException| ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setText("koumpi");
                String text = txt.getText().toString();
                // For debugging
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

                Log.d("app","I am here");

                Intent i = new Intent(getApplicationContext(),ImageActivity.class);
                i.putExtra("text",text);
                startActivity(i);
            }
        });*/
    }
}