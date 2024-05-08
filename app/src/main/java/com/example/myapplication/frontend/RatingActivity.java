package com.example.myapplication.frontend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.backend.src.Entities.AccommodationRoom;
import com.example.myapplication.backend.src.Entities.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RatingActivity extends AppCompatActivity {

    private Button submitBtn;
    private RatingBar ratingBar;
    private TextView confirmText;
    private ImageView image;
    private TextView name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        image = findViewById(R.id.image);
        submitBtn = findViewById(R.id.submitBtn);
        ratingBar = findViewById(R.id.ratingBar);
        confirmText = findViewById(R.id.confirmText);
        name = findViewById(R.id.roomName);

        AccommodationRoom room = (AccommodationRoom) getIntent().getSerializableExtra("room");
        // Instantiate the Bitmap from the Byte[] array of the AccommodationRoom
        Bitmap bitmap = BitmapFactory.decodeByteArray(room.getImageData(), 0, room.getImageData().length);
        image.setImageBitmap(bitmap);
        name.setText(room.getName());



        Log.d("asd", "rating");
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get rating from RatingBar
                int rating = (int)ratingBar.getRating();

                // After the submit is pressed the button is disabled
                // and the RatingBar is locked
                submitBtn.setEnabled(false);
                ratingBar.setIsIndicator(true);

                // Instantiate Task object and set the request attributes
                Task task = new Task();
                task.setRoomName(room.getName());
                task.setStarsFilter(rating);
                task.setMethod("rate");

                // Get the output stream from SocketHandler
                ObjectOutputStream out = SocketHandler.getObjectOut();
                ObjectInputStream in = SocketHandler.getObjectIn();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Send Task to Master
                            out.writeObject(task);
                            Boolean response = (Boolean)in.readObject();
                            String text;
                            if (response){
                                text = "Review sumbited successfully!";
                            }
                            else {
                                text = "Operation Failed...";
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    confirmText.setText(text);
                                    finish();
                                }
                            });



                        } catch (IOException| ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        });



    }
}
