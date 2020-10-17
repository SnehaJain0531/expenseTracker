package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.DigitsKeyListener;
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
Double cost=0.0;
Double amt=0.0;
private DatePickerDialog.OnDateSetListener mDateSetListener;
PieChart pieChart;
int[] colorClass= new int[]{Color.RED,Color.CYAN,Color.MAGENTA,Color.GREEN};
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
                          //read the values from firebase
                          //if a date has no entry toast msg

                          dataval.add(new PieEntry(15,"House"));
                          dataval.add(new PieEntry(30,"Food"));
                          dataval.add(new PieEntry(25,"Health"));
                          dataval.add(new PieEntry(30,"Others"));


                          PieDataSet pieDataSet= new PieDataSet(dataval,"");
                          pieDataSet.setColors(colorClass);
                          PieData pieData= new PieData(pieDataSet);
                          pieChart.setData(pieData);
                          pieChart.invalidate();
                          pieChart.setDrawEntryLabels(true);
                          pieChart.setCenterTextRadiusPercent(20);
                      }
                }
            });

            Button curr=(Button)findViewById(R.id.button3);
            curr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this,"Expense for today: Rs "+cost,Toast.LENGTH_LONG).show();
                }
            });

            final EditText editText=(EditText) findViewById(R.id.editTextNumber);
            editText.setKeyListener(DigitsKeyListener.getInstance(false,true)); // positive decimals numbers.
           // amt=  Double.parseDouble(editText.getText().toString());


            Button add=(Button)findViewById(R.id.button);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (date_string == null)  //&& if expense amount is empy
                    {
                        Toast.makeText(MainActivity.this,"Select a Date",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(editText.getText()==null){ //or amount =0.0
                            Toast.makeText(MainActivity.this,"No amount entered",Toast.LENGTH_SHORT).show();
                        }
                        else{

                            Toast.makeText(MainActivity.this, "Expense Recorder:Rs."+amt, Toast.LENGTH_SHORT).show();
                            //update cost, write/update firebase - under category and daycost
                              //     cost=cost+amt;
                            editText.setText("");

                            //last amt =0.0 //to revert back to 0 for next proper addition
                        }

                    }
                }
            });
    }
}