package com.example.myapplication.frontend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.backend.src.Entities.AccommodationRoom;

public class RoomDetailsActivity extends AppCompatActivity {

    ImageView image;
    TextView name;
    TextView area;
    TextView price;
    TextView capacity;
    RatingBar rating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccommodationRoom room = (AccommodationRoom) getIntent().getSerializableExtra("rooms");

        setContentView(R.layout.activity_room_details);


        image = findViewById(R.id.image);
        name = findViewById(R.id.roomName);
        area = findViewById(R.id.areaText);
        price = findViewById(R.id.priceText);
        capacity = findViewById(R.id.capacityText);
        rating = findViewById(R.id.ratingBar);



        name.setText(room.getName());
        area.setText(room.getArea() + "");
        price.setText(String.valueOf(room.getPrice()) +"/day");

        capacity.setText(String.valueOf(room.getCapacity()) + " persons");
        Log.d("asd", "eftase");

        rating.setRating(room.getStars());

        // Instantiate the Bitmap from the Byte[] array of the AccommodationRoom
        Bitmap bitmap = BitmapFactory.decodeByteArray(room.getImageData(), 0, room.getImageData().length);
        image.setImageBitmap(bitmap);







    }
}
