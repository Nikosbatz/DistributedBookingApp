package com.example.myapplication.frontend;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.backend.src.Entities.AccommodationRoom;
import com.example.myapplication.backend.src.Entities.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kotlinx.coroutines.scheduling.TasksKt;

public class BookingActivity extends AppCompatActivity {

    private LocalDate initialDate;
    private LocalDate finalDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private Boolean isInitialDate;
    private TextView initialDateText;
    private TextView finalDateText;
    private TextView confirmText;
    private Button btnBook;
    private Button btnInitialDate;
    private Button btnFinalDate;
    private TextView roomName;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        confirmText = findViewById(R.id.confirmText);
        btnInitialDate = findViewById(R.id.initialDate);
        btnFinalDate = findViewById(R.id.finalDate);
        btnBook = findViewById(R.id.bookBtn);
        image = findViewById(R.id.image);
        roomName = findViewById(R.id.roomName);


        AccommodationRoom room = (AccommodationRoom) getIntent().getSerializableExtra("room");
        // Instantiate the Bitmap from the Byte[] array of the AccommodationRoom
        Bitmap bitmap = BitmapFactory.decodeByteArray(room.getImageData(), 0, room.getImageData().length);
        image.setImageBitmap(bitmap);
        roomName.setText(room.getName());

        // Pick initial date of the booking
        btnInitialDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInitialDate = true;
                showDatePicker();
            }
            
        });

        // Pick final date of the booking
        btnFinalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInitialDate = false;
                showDatePicker();
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ObjectOutputStream out = SocketHandler.getObjectOut();
                        ObjectInputStream in = SocketHandler.getObjectIn();
                        Task task = new Task();
                        task.setMethod("book");
                        task.setRoomName(room.getName());
                        task.setDateFirst(initialDate);
                        task.setDateLast(finalDate);
                        try {
                            out.writeObject(task);
                            String text;
                            if ((boolean)in.readObject()){
                                text = "Booking was successful!";
                                task = new Task();
                                task.setMethod("showAllRooms");
                                out.writeObject(task);
                                MainActivity.rooms = (ArrayList<AccommodationRoom>) in.readObject();
                            }
                            else{
                                text = "Booking was unsuccessful!";
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    confirmText.setText(text);
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
                    btnInitialDate.setText(date);

                }
                else {
                    finalDate = LocalDate.parse(date, formatter);
                    isInitialDate = true;

                    // Check if Initial date is before Final date
                    if(initialDate.isAfter(finalDate) || initialDate.equals(finalDate)){
                        Toast.makeText(BookingActivity.this, "Invalid dates!", Toast.LENGTH_LONG).show();
                        btnFinalDate.setText("Invalid Date!");
                    }
                    else {
                        btnFinalDate.setText(date);
                    }
                }
            }
        }, year, month, day );

        datePicker.show();

    }



}