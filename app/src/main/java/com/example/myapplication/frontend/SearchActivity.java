package com.example.myapplication.frontend;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.backend.src.Entities.AccommodationRoom;
import com.example.myapplication.backend.src.Entities.Task;


import com.example.myapplication.R;

public class SearchActivity extends AppCompatActivity {
    private Button submitButton;
    private RatingBar ratingBar;
    private TextView filtersText;
    private TextView locationText;
    private EditText locationEdit;
    private TextView priceText;
    private EditText priceEdit;
    private TextView checkInText;
    private TextView checkInButtonText;
    private Button checkInButton;
    private TextView checkOutText;
    private TextView checkOutButtonText;
    private Button checkOutButton;
    private TextView numberOfPeopleText;
    private NumberPicker numberPicker;
    private TextView ratingsText;

    private LocalDate initialDate;
    private LocalDate finalDate;
    private Boolean isInitialDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        filtersText = findViewById(R.id.filtersText);
        locationText = findViewById(R.id.locationText);
        locationEdit = findViewById(R.id.locationEdit);
        priceText = findViewById(R.id.priceText);
        priceEdit = findViewById(R.id.priceEdit);
        checkInText = findViewById(R.id.checkInText);
        checkInButtonText = findViewById(R.id.checkInButtonText);
        checkInButton = findViewById(R.id.checkInButton);
        checkOutText = findViewById(R.id.checkOutText);
        checkOutButtonText = findViewById(R.id.checkOutButtonText);
        checkOutButton = findViewById(R.id.checkOutButton);
        numberOfPeopleText = findViewById(R.id.numberOfPeopleText);
        numberPicker = findViewById(R.id.numberPicker);
        ratingsText = findViewById(R.id.ratingsText);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitButton);

        checkInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                isInitialDate = true;
                showDatePicker();
            }

        });

        checkOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                isInitialDate = false;
                showDatePicker();

            }

        });



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get rating from RatingBar
                int rating = (int) ratingBar.getRating();
                String location = String.valueOf(locationEdit.getText());
                int price;
                if (String.valueOf(priceEdit.getText()).isEmpty()){
                    price = 0;
                }else {
                    price = Integer.parseInt(String.valueOf(priceEdit.getText()));
                }
                int capacity = numberPicker.getValue();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ObjectOutputStream out = SocketHandler.getObjectOut();
                        ObjectInputStream in = SocketHandler.getObjectIn();
                        Task task = new Task();
                        task.setMethod("filter");
                        task.setDateFirst(initialDate);
                        task.setDateLast(finalDate);
                        if (location.isEmpty()){
                            task.setAreaFilter("null");
                        }else{
                            task.setAreaFilter(location);
                        }
                        task.setPriceFilter(price);
                        task.setCapacityFilter(capacity);
                        task.setStarsFilter(rating);

                        try {
                            out.writeObject(task);
                            String text = "Rooms are now filtered";
                            MainActivity.rooms = (ArrayList<AccommodationRoom>) in.readObject();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SearchActivity.this, text, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });

                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();


            }
        });
    }




    private void showDatePicker(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Toast.makeText(this, year+"|"+month+"|"+day , Toast.LENGTH_LONG).show();


        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = String.format("%02d-%02d-%04d", dayOfMonth, month+1, year);
                if (isInitialDate){
                    initialDate = LocalDate.parse(date, formatter);
                    isInitialDate = false;
                    checkInButtonText.setText(date);

                }
                else {
                    finalDate = LocalDate.parse(date, formatter);
                    isInitialDate = true;

                    // Check if Initial date is before Final date
                    if(initialDate.isAfter(finalDate) || initialDate.equals(finalDate)){
                        checkOutButtonText.setText("Invalid Date!");
                        Toast.makeText(SearchActivity.this, "Invalid dates!", Toast.LENGTH_LONG).show();

                    }
                    else {
                        checkOutButtonText.setText(date);
                    }
                }
            }
        }, year, month, day );

        datePicker.show();

    }


}