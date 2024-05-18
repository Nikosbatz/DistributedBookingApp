package com.example.myapplication.frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    Button search_btn;
    Context context = this;
    public static Boolean refreshNeeded = false;
    Socket socket;
    public static ArrayList<AccommodationRoom> rooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_btn = findViewById(R.id.search_btn);
        listView = findViewById(R.id.listview);

        // Show all listings
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = null;
                try {
                    // Change IP according to the server's local IP in the network
                    socket = new Socket("192.168.1.1", 1234);

                    // Reading objects from Server
                    ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

                    // Writing objects to Server
                    ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

                    // Set Socket and Streams to SocketHandler static variables so every activity can access them
                    SocketHandler.setSocket(socket);
                    SocketHandler.setObjectIn(objectIn);
                    SocketHandler.setObjectOut(objectOut);

                    // Declaring to Master to use the renter interface
                    objectOut.writeObject("renter");

                    Task task = new Task();
                    task.setMethod("showAllRooms");

                    // Send request to Master
                    objectOut.writeObject(task);

                    // Master Response
                   rooms = (ArrayList<AccommodationRoom>) objectIn.readObject();


                    // Render ListView on the UI based on the rooms that master returned
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            renderListView();
                        }
                    });



                } catch (IOException| ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        Log.d("asd", "MAIN");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(getApplicationContext(),RoomDetailsActivity.class);
                Toast.makeText(context, rooms.get(i).getName(), Toast.LENGTH_SHORT).show();
                intent.putExtra("rooms", rooms.get(i));
                startActivity(intent);
            }

        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SearchActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(refreshNeeded){
            renderListView();
        }

    }

    private void renderListView(){
        ListingsAdapter adapter = new ListingsAdapter(context, rooms);
        listView.setAdapter(adapter);
    }


}