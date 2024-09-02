package com.roommates.split_it;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home_page extends AppCompatActivity {

    Button add;
    ImageView all;
    TextView average_spend, total_spend ;
    AlertDialog dialog;
    LinearLayout layout;
    LinearLayout container_other_user;
    int no_of_user;
    String item_type;
    String buyer_name;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences pref = getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
        no_of_user = pref.getInt("NO_OF_USER",2);
        db = new DBHelper(this);

        total_spend = findViewById(R.id.total_spend);
        average_spend = findViewById(R.id.average_spend);

        container_other_user = findViewById(R.id.container_other_user);
        fetchUserData();

        all = findViewById(R.id.view_all_records);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllRecord_activity.class);
                startActivity(intent);
            }
        });


        add = findViewById(R.id.add_item_btn);

        buildDialog();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }
    private void fetchUserData(){

        int total =db.get_totalSpend_by_all_user();
        total_spend.setText(Integer.toString(total));
        int average = total / no_of_user;
        average_spend.setText(Integer.toString(average));

        Cursor cursor = null;

        try {
            cursor = db.getAllUsers();
            if (cursor != null && cursor.moveToFirst()) {
                container_other_user.removeAllViews();
                do {
                    int name_Index = cursor.getColumnIndex("user_name");
                    int spend_Index = cursor.getColumnIndex("user_spend");
                    if (name_Index != -1  & spend_Index != -1)  {
                        String name = cursor.getString(name_Index);
                        int spend = cursor.getInt(spend_Index);
                        add_other_user(name,spend - average, spend );
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void add_other_user(String name, int status, int spend){
        final View view = getLayoutInflater().inflate(R.layout.other_user_status_layout,null);

        TextView user_name =  view.findViewById(R.id.name);
        TextView user_status =  view.findViewById(R.id.status);
        TextView user_spend =  view.findViewById(R.id.user_spend);
        user_name.setText(name);
        user_status.setText(Integer.toString(status));
        user_spend.setText(Integer.toString(spend));

        container_other_user.addView(view);
    }
    private void buildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_item_dialog,null);

        Cursor cursor = null;

        String[] users = new String[no_of_user];
        int i =0 ;
        try {
            cursor = db.getAllUsers();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int name_Index = cursor.getColumnIndex("user_name");
                    if (name_Index != -1 )  {
                        String name = cursor.getString(name_Index);
                        users[i++] = name ;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, users);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner name = view.findViewById(R.id.buyer_name);
        name.setAdapter(adapter);

        Spinner spinner_item_type = view.findViewById(R.id.item_type);
        final EditText items = view.findViewById(R.id.item_name);
        final EditText price = view.findViewById(R.id.item_price);

        name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                buyer_name = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something if nothing is selected
            }
        });

        spinner_item_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                item_type = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something if nothing is selected
            }
        });

        builder.setView(view);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addRecord(buyer_name,item_type,items.getText().toString().trim(),Integer.parseInt(price.getText().toString()));
                        items.setText("");
                        price.setText("");
                        fetchUserData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog = builder.create();

    }


    private void addRecord(String buyer_name, String item_type, String items_name, int item_price){
        long id = db.insertItem(buyer_name, item_type, items_name, item_price);
        if (id > 0) {
            Toast.makeText(this, "Item inserted successfully", Toast.LENGTH_SHORT).show();
        }
        db.updateUserStatus(buyer_name,item_price);

    }

}





