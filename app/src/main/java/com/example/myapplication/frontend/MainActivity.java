package com.example.myapplication.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText txt = findViewById(R.id.option);
        Button btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
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
        });
    }
}