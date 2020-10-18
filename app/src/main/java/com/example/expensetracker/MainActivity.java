package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
String date_string;
String pos;
int dd,mm,yy;
ArrayList<String> arrayList;

int costInt;
private DatePickerDialog.OnDateSetListener mDateSetListener;
PieChart pieChart;
int[] colorClass= new int[]{Color.RED,Color.CYAN,Color.MAGENTA,Color.GREEN};
FirebaseDatabase database;
DatabaseReference db1;
    DatabaseReference db;
String amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView date=(TextView)findViewById(R.id.textView2);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                date_string = day + "/" + (month+1) + "/" + year;
                TextView date=(TextView)findViewById(R.id.textView2);
                String today_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String date_array[] = today_date.split("-");
                dd = Integer.parseInt(date_array[2]);
                mm = Integer.parseInt(date_array[1]);
                yy = Integer.parseInt(date_array[0]);
                date.setText(date_string);
                if(yy==year) {
                    if (mm <= month+1) {
                        if (dd <= day) {
                            date.setText(date_string);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Past date given", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Past month given", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(yy<year){
                    date.setText(date_string);
                }
                else{
                    Toast.makeText(MainActivity.this,"Past year given",Toast.LENGTH_SHORT).show();
                }
            }
        };

        Spinner spinner = findViewById(R.id.spinner);
        arrayList = new ArrayList<>();
        arrayList.add("Households");
        arrayList.add("Food");
        arrayList.add("Medicine");
        arrayList.add("Others");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos=arrayList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });


        pieChart = findViewById(R.id.pie);
        pieChart.setVisibility(View.GONE);

        Button visual=(Button)findViewById(R.id.button2);
        visual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(date_string==null) {
                        Toast.makeText(MainActivity.this,"Select a Date",Toast.LENGTH_SHORT).show();
                    }
                      else {
                          pieChart.setVisibility(View.VISIBLE);
                          ArrayList<PieEntry> dataval=new ArrayList<>();

                        String date_array[] = date_string.split("/");
                        String dd = date_array[0];
                        String mm = date_array[1];
                        String yy = date_array[2];
                        final ArrayList<Integer> someInts=new ArrayList<Integer>();
                        for(int i=0;i<4;i++){
                            someInts.add(0+i+1);
                        }

                        DatabaseReference data =FirebaseDatabase.getInstance().getReference();
                        data.child(yy).child(mm).child(dd).child("Cost").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot snap:snapshot.getChildren()){
                                    String parent=snap.getKey();
                                    for(int i=0;i<4;i++){
                                        if(parent.equalsIgnoreCase(arrayList.get(i))){
                                            someInts.set(i,snap.child(parent).getValue(Integer.class));
                                            Toast.makeText(MainActivity.this,"Inside visualise",Toast.LENGTH_SHORT).show();
                                            Toast.makeText(MainActivity.this,date_string+pos+snap.child(parent).getValue(Integer.class),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                          dataval.add(new PieEntry(someInts.get(0),"Households"));
                          dataval.add(new PieEntry(someInts.get(1),"Food"));
                          dataval.add(new PieEntry(someInts.get(2),"Health"));
                          dataval.add(new PieEntry(someInts.get(3),"Others"));


                          PieDataSet pieDataSet= new PieDataSet(dataval,"");
                          pieDataSet.setColors(colorClass);
                          PieData pieData= new PieData(pieDataSet);
                          pieChart.setData(pieData);
                          pieChart.invalidate();
                          pieChart.setDrawEntryLabels(false);
                          pieChart.setCenterTextRadiusPercent(20);
                      }
                }
            });

            Button curr=(Button)findViewById(R.id.button3);
            curr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String date_array[] = date_string.split("/");
                    final String dd = date_array[0];
                    final String mm = date_array[1];
                    final String yy = date_array[2];
                    db = FirebaseDatabase.getInstance().getReference();
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Toast.makeText(MainActivity.this,"Expense for today: Rs "+snapshot.child(yy).child(mm).child(dd).child("Total").getValue(String.class),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });

            final EditText editText=(EditText) findViewById(R.id.editTextNumber);
            editText.setKeyListener(DigitsKeyListener.getInstance(false,true));


            Button add=(Button)findViewById(R.id.button);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    amount= editText.getText().toString();
//                    costInt=Integer.parseInt(amount);
                    if (date_string == null)
                    {
                        Toast.makeText(MainActivity.this,"Select a Date",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(amount.isEmpty()==true){
                            Toast.makeText(MainActivity.this,"No amount entered",Toast.LENGTH_SHORT).show();
                        }
                        else{

                            String date_array[] = date_string.split("/");
                            final String dd = date_array[0];
                            final String mm = date_array[1];
                            final String yy = date_array[2];
                            db1 = FirebaseDatabase.getInstance().getReference();
                            db1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int old;
                                   try {
                                      try{
                                          old = (snapshot.child(yy).child(mm).child(dd).child("Cost").child(pos).getValue(Integer.class));
                                      }
                                      catch (Exception e){
                                          old=0;
                                      }
                                       int newval = old + Integer.parseInt(amount);
                                       db1.child(yy).child(mm).child(dd).child("Cost").child(pos).setValue(newval);

                                       int prevTotal ;
                                       try {
                                           prevTotal=(snapshot.child(yy).child(mm).child(dd).child("Total").getValue(Integer.class));
                                       }
                                       catch(Exception e){
                                           prevTotal=0;
                                       }
                                       int newTotal = prevTotal + Integer.parseInt(amount);
                                       db1.child(yy).child(mm).child(dd).child("Total").setValue(newTotal);
                                       Toast.makeText(MainActivity.this, "Expense Recorded:Rs." + amount, Toast.LENGTH_SHORT).show();
                                   }
                                   catch (Exception exception){
                                       Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            editText.setText("");
                        }

                    }
                }
            });
    }
}