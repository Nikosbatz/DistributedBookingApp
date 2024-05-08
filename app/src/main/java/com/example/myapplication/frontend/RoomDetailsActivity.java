package com.example.myapplication.frontend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.backend.src.Entities.AccommodationRoom;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class RoomDetailsActivity extends AppCompatActivity {

    ImageView image;
    TextView name;
    TextView area;
    TextView price;
    TextView capacity;
    RatingBar rating;
    TableRow datesRow;
    Button rateBtn;
    Button bookBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccommodationRoom room = (AccommodationRoom) getIntent().getSerializableExtra("rooms");

        setContentView(R.layout.activity_room_details);

        rateBtn = findViewById(R.id.rateBtn);
        bookBtn = findViewById(R.id.bookBtn);
        image = findViewById(R.id.image);
        name = findViewById(R.id.roomName);
        area = findViewById(R.id.areaText);
        price = findViewById(R.id.priceText);
        capacity = findViewById(R.id.capacityText);
        rating = findViewById(R.id.ratingBar);
        datesRow = findViewById(R.id.datesRow);


        name.setText(room.getName());
        area.setText(room.getArea() + "");
        price.setText(String.valueOf(room.getPrice()) +"/day");
        capacity.setText(String.valueOf(room.getCapacity()) + " persons");
        rating.setRating(room.getStars());
        Log.d("asd", room.getStars() + "");


        // Instantiate the Bitmap from the Byte[] array of the AccommodationRoom
        Bitmap bitmap = BitmapFactory.decodeByteArray(room.getImageData(), 0, room.getImageData().length);
        image.setImageBitmap(bitmap);

        // Add available dates to the TableLayout
        addAvailableDates(room);

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("asd", "mpike");
                Intent intent = new Intent(getApplicationContext(),RatingActivity.class);
                intent.putExtra("room", room);
                startActivity(intent);
                Log.d("asd", "PERASEEE");
            }
        });

    }


    private void addAvailableDates(AccommodationRoom room){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        HashMap<LocalDate, LocalDate> availableDates = room.getAvailableDates();

        // Create layout parameters for TextViews
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER; // Set layout gravity


        // Instantiating new TextView
        TextView dateText = new TextView(this);
        String tempString = "";

        // Iterating through all available dates of this room
        for (LocalDate tempDate: availableDates.keySet()){

            // LocalDate -> String
            String date1 = tempDate.format(formatter);
            String date2 = availableDates.get(tempDate).format(formatter);
            tempString += date1 + " - " + date2 + "\n";

        }

        // Modifying View parameters
        dateText.setText(tempString);
        dateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        dateText.setLayoutParams(params);

        // Inserting View to the TableRow
        datesRow.addView(dateText);
    }






}
