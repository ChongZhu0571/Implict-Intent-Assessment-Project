package com.chong.implictintentassessment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.material.timepicker.TimeFormat;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Button bt_start_date;
    Button bt_end_date;
    Button bt_start_time;
    Button bt_end_time;
    Button bt_capture;
    EditText txt_title;
    EditText txt_description;
    EditText txt_email;
    ImageView photo;
    private static final int photo_id = 1;

    int date_dialog = 0;
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_title = (EditText)findViewById(R.id.txt_title);
        txt_description = (EditText)findViewById(R.id.txt_description);
        txt_email = (EditText)findViewById(R.id.txt_email);
        bt_start_date = findViewById(R.id.bt_start_date);
        bt_end_date = findViewById(R.id.bt_end_date);
        bt_start_time = findViewById(R.id.bt_start_time);
        bt_end_time = findViewById(R.id.bt_end_time);
        bt_capture = findViewById(R.id.bt_capture);
        photo = findViewById(R.id.iv_captured);

        //Let VM ignores the file URI exposure
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


    public void start_date_picker(View view){
        bt_start_date= (Button)findViewById(R.id.bt_start_date);
        date_dialog = 1;
        openDialog();


    }


    public void end_date_picker(View view){

        date_dialog = 2;
        openDialog();
    }

    public void openDialog(){
        DatePickerFragment datepickDialog = new DatePickerFragment();
        datepickDialog.show(getSupportFragmentManager(), "Start Date");
    }
    public void onDateSet(DatePicker view, int year, int month,int dayOfMonth){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String dateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        if(date_dialog == 1){
            bt_start_date.setText(dateString);
        }
        if(date_dialog == 2){
            bt_end_date.setText(dateString);
        }



    }



    public void start_time_picker(View view) {
        bt_start_time= (Button)findViewById(R.id.bt_start_time);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(minute<10){
                    bt_start_time.setText(hourOfDay + ":0" + minute);
                }
                else {

                    bt_start_time.setText(hourOfDay + ":" + minute);
                }
            }
        },hour,minute,android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }



public void end_time_picker(View view) {
        bt_end_time= (Button)findViewById(R.id.bt_end_time);
    TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(minute<10){
                bt_end_time.setText(hourOfDay + ":0" + minute);
            }else {

                bt_end_time.setText(hourOfDay + ":" + minute);
            }
        }
    },hour,minute,android.text.format.DateFormat.is24HourFormat(this));
    timePickerDialog.show();
    }

//All Day Checkbox Clicked
    public void allday_clicked(View view) {
        boolean checked = ((CheckBox) findViewById(R.id.bt_allday)).isChecked();
        if(checked){

            bt_start_time.setVisibility(View.INVISIBLE);
            bt_end_time.setVisibility(View.INVISIBLE);
        }
        else {
            bt_start_time.setVisibility(View.VISIBLE);
            bt_end_time.setVisibility(View.VISIBLE);
        }
    }
//Add Event Clicked
    public void addEvent(View view) throws ParseException {
        String type = "vnd.android.cursor.item/event";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm");
        Date startDate = sdf.parse(bt_start_date.getText().toString() + " "+bt_start_time.getText().toString());
        Date endDate = sdf.parse(bt_end_date.getText().toString() + " "+ bt_end_time.getText().toString());
        long beginDateTime = startDate.getTime();
        long endDateTime  = endDate.getTime();
        Button bt_addEvent = (Button)findViewById(R.id.bt_addEvent);
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType(type);
        intent.putExtra(CalendarContract.Events.TITLE,txt_title.getText().toString());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,beginDateTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endDateTime);
        intent.putExtra(CalendarContract.Events.ALL_DAY,((CheckBox) findViewById(R.id.bt_allday)).isChecked());
        intent.putExtra(CalendarContract.Events.DESCRIPTION,txt_description.getText().toString());
        intent.putExtra(Intent.EXTRA_EMAIL,txt_email.getText().toString());
        startActivity(intent);
    }

    public void capture_clicked(View view) {
        //Camera intent
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String timeStamp = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm").format(new Date());
        //Folder intent
        File photoFolder = new File(Environment.getExternalStorageDirectory(),"Android Assessment Folder");
        photoFolder.mkdirs();
        //name photo and store it.
        File photoFile = new File(photoFolder,"_"+timeStamp+".jpg");
        Uri photoSaved = Uri.fromFile(photoFile);
        //execute camera and send data.
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoSaved);
        startActivityForResult(intent,photo_id);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_id  && resultCode == RESULT_OK) {
            Bitmap p = (Bitmap) data.getExtras().get("data");   //get data
            photo.setImageBitmap(p);                            //display photo
        }

    }
}