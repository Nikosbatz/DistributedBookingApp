package com.example.myapplication.frontend;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.example.myapplication.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.BreakIterator;
import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private EditText editTextStartDate, editTextEndDate;
    private Spinner roomSpinner;
    private Button buttonBook;
    private TextView confirmText;
    private BreakIterator editTextDate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        buttonBook = findViewById(R.id.buttonBook);
        roomSpinner = findViewById(R.id.roomSpinner);
        confirmText = findViewById(R.id.confirmText);
        setupRoomSpinner();

        editTextStartDate.setOnClickListener(view -> showDatePickerDialog(editTextStartDate));
        editTextEndDate.setOnClickListener(view -> showDatePickerDialog(editTextEndDate));

        buttonBook.setOnClickListener(view -> {
            String startDate = editTextStartDate.getText().toString();
            String endDate = editTextEndDate.getText().toString();
            String selectedRoom = roomSpinner.getSelectedItem().toString();

            Entities.Task task = new Entities.Task();

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
                            text = "Booking was successful!";
                        }
                        else {
                            text = "Booking was unsuccessful!";
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                confirmText.setText(text);
                                finish();
                            }
                        });



                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
            //String time = editTextTime.getText().toString();
            // Here you can handle the booking logic, like sending data to a server or a database
            // Example: submitBooking(date, time);
        });
    }

    private void setupRoomSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.room_names, android.R.layout.simple_spinner_item); // Assume 'room_names' is defined in strings.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(adapter);
    }
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) ->
                editTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day).show();
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
            editText.setText(selectedDate);
        }, year, month, day).show();
    }
}
