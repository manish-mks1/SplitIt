package com.roommates.split_it;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Entries_users extends AppCompatActivity {
    Spinner spinner;
    EditText user;
    LinearLayout container;
    int no_of_user = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entries_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = ( EditText) findViewById(R.id.user_name) ;
        spinner = (Spinner) findViewById(R.id.no_of_user);
        container = findViewById(R.id.other_user_entries);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                no_of_user = Integer.parseInt(parentView.getItemAtPosition(position).toString());
                createEditTexts(no_of_user);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something if nothing is selected
            }
        });




    }
    private void createEditTexts(int count){
        container.removeAllViews();
        for (int i = 1; i < count; i++) {
            EditText editText = new EditText(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 20, 0, 20);

            editText.setLayoutParams(params);

            editText.setPadding(30, 20, 30, 30);

            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            editText.setHint("User " + (i + 1 ));

            editText.setId(View.generateViewId());

            container.addView(editText);
        }
        Button button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setText("START");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveEditTextValues();

                SharedPreferences pref = getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("NO_OF_USER",no_of_user);
                editor.putBoolean("isUserSetupComplete", true);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), Home_page.class);
                startActivity(intent);
                finish();
            }
        });

        container.addView(button);;
    }
    private void retrieveEditTextValues() {
        DBHelper db = new DBHelper(this);
        db.insertUser(user.getText().toString().trim(),0,0);
        for (int i = 0; i < no_of_user-1; i++) {
            EditText editText = (EditText) container.getChildAt(i);
            db.insertUser(editText.getText().toString().trim(),0,0);

        }


    }
}