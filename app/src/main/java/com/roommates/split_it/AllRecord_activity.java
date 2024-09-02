package com.roommates.split_it;

import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AllRecord_activity extends AppCompatActivity {

    DBHelper db;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_record);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        db = new DBHelper(this);

        layout = (LinearLayout) findViewById(R.id.container_all_records);

//        Toast.makeText(this,"Before fetch",Toast.LENGTH_SHORT).show();
        fetchUserRecords();
//        Toast.makeText(this,"After fetch",Toast.LENGTH_SHORT).show();



    }
    public void fetchUserRecords(){
        layout.removeAllViews();
        Cursor cursor = null;

        try {
            cursor = db.getAllItems();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final View view = getLayoutInflater().inflate(R.layout.item_card,null);

                    TextView name = view.findViewById(R.id.buyer_name);
                    TextView type = view.findViewById(R.id.item_type);
                    TextView items = view.findViewById(R.id.item_name);
                    TextView price = view.findViewById(R.id.price);
//                    Toast.makeText(this,"In fetch: " ,Toast.LENGTH_SHORT).show();

                    int name_Index = cursor.getColumnIndex("name");
                    int type_Index = cursor.getColumnIndex("type");
                    int items_Index = cursor.getColumnIndex("items_name");
                    int price_Index = cursor.getColumnIndex("price");
                    if (price_Index != -1 && name_Index != -1 && items_Index != -1 && type_Index != -1)  {
                        String n =cursor.getString(name_Index);
                        name.setText(n);
                        type.setText(cursor.getString(type_Index));
                        items.setText(cursor.getString(items_Index));
                        price.setText(cursor.getInt(price_Index)+"");

                    }
//                    Toast.makeText(this,"In fetch: "+name_Index+ " : "+type_Index+": "+items_Index+" : "+price_Index,Toast.LENGTH_SHORT).show();

                    layout.addView(view,0);
                } while (cursor.moveToNext());
            }else{
                layout.setGravity(Gravity.CENTER);

                TextView textView = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                textView.setText("No Records Found");
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setLayoutParams(params);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                layout.addView(textView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

}