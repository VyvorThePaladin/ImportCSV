package com.example.android.importcsv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_csv = findViewById(R.id.csv_button);
        Button button_print = findViewById(R.id.print_button);
        final ListView listView = findViewById(R.id.db_list);


        button_csv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }  else {
                    DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                    SQLiteDatabase db = helper.getWritableDatabase();

                    try {
                        String path = Environment.getExternalStorageDirectory() + "/" +
                                Environment.DIRECTORY_DOWNLOADS + "/yellow.csv";
                        FileReader file = new FileReader(path);
                        BufferedReader buffer = new BufferedReader(file);
                        String line = "";
                        String tableName = "yellow_pages";
                        String columns = "ID,Name,Contact_No,Address";
                        String InsertString1 = "INSERT INTO " + tableName + "(" + columns + ") values(";
                        String InsertString2 = ");";

                        db.beginTransaction();
                        while ((line = buffer.readLine()) != null) {
                            StringBuilder sb = new StringBuilder(InsertString1);
                            String[] split_array = line.split(",");
                            sb.append("'" + split_array[0] + "','");
                            sb.append(split_array[1] + "','");
                            sb.append(split_array[2] + "','");
                            sb.append(split_array[3] + "'");

                            sb.append(InsertString2);
                            db.execSQL(sb.toString());

                        }

                        db.setTransactionSuccessful();
                        db.endTransaction();

                        Toast.makeText(MainActivity.this, "CSV Upload Success!", Toast.LENGTH_SHORT).show();


                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "CSV Upload Failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }

            }
        });

        button_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                SQLiteDatabase db = helper.getReadableDatabase();

                try {
                    Cursor cursor = db.query(
                            DatabaseHelper.TABLE_NAME,
                            DatabaseHelper.COLUMNS,
                            null,
                            null,
                            null,
                            null,
                            null);

                    List<String> values = new ArrayList<String>();
                    cursor.moveToFirst();
                    do {
                        String id = cursor.getString(0);
                        String name = cursor.getString(1);
                        String phone = cursor.getString(2);
                        String adrss = cursor.getString(3);

                        values.add(id + " - " + name + " - " + phone + " - " + adrss);
                    } while (cursor.moveToNext());

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getApplicationContext(), R.layout.simple_list_item_1, values);

                    listView.setAdapter(adapter);

                    cursor.close();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "No data in database!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

}
