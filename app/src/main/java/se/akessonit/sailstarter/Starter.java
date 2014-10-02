package se.akessonit.sailstarter;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.format.Time;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class Starter extends Activity {
    private SharedPreferences mPrefs;
    private int hour;
    private int min;
    private int sec;

    TextView hTextView;
    final Handler handler = new Handler();
    Timer t = new Timer();
    TimerTask mTimerTask;
    NumberPicker numberPickerHour;
    NumberPicker numberPickerMin;
    NumberPicker numberPickerSec;

    PowerManager.WakeLock wl;
    KeyguardManager.KeyguardLock lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_starter);

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        hour = settings.getInt("hour", 0);
        min = settings.getInt("min", 0);
        sec = settings.getInt("sec", 0);

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

        hTextView = (TextView) findViewById(R.id.timeview);

        numberPickerHour = (NumberPicker) findViewById(R.id.numberpickerHour);
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setValue(hour);
        numberPickerHour.setWrapSelectorWheel(true);

        numberPickerMin = (NumberPicker) findViewById(R.id.numberpickerMin);
        numberPickerMin.setMaxValue(59);
        numberPickerMin.setMinValue(0);
        numberPickerMin.setValue(min);
        numberPickerMin.setWrapSelectorWheel(true);

        numberPickerSec = (NumberPicker) findViewById(R.id.numberpickerSec);
        numberPickerSec.setMaxValue(59);
        numberPickerSec.setMinValue(0);
        numberPickerSec.setValue(sec);
        numberPickerSec.setWrapSelectorWheel(true);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // Screen veritical
        if(size.y > size.x) {
            numberPickerHour.setVisibility(View.VISIBLE);
            numberPickerMin.setVisibility(View.VISIBLE);
            numberPickerSec.setVisibility(View.VISIBLE);
            hTextView.setTextSize(70);
        }
        else{ // Screen horisontal
            numberPickerHour.setVisibility(View.GONE);
            numberPickerMin.setVisibility(View.GONE);
            numberPickerSec.setVisibility(View.GONE);
            hTextView.setTextSize(130);
        }

        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        SetTime();
                    }
                });
            }
        };
        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTask, 0, 1000);  //
    }

    public void SetTime() {
        int startHour = numberPickerHour.getValue();
        int startMinute = numberPickerMin.getValue();
        int startSec = numberPickerSec.getValue();

        Time timeNow = new Time();
        timeNow.setToNow();
        String startTimeString = startHour + ":" + startMinute + ":" + startSec;
        String nowTimeString = timeNow.hour + ":" + timeNow.minute + ":" + timeNow.second;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date startDate = (Date) formatter.parse(startTimeString);
            Date nowDate = (Date) formatter.parse(nowTimeString);
            Calendar cal = Calendar.getInstance();

            long differens = startDate.getTime() - nowDate.getTime();
            if(differens < 5000 && differens > -100) {
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.shortbeep);
                mediaPlayer.start();
            }

            Boolean beforeStart = startDate.after(nowDate);
            if(beforeStart){
                cal.setTime(startDate);
                cal.add(Calendar.HOUR, -timeNow.hour);
                cal.add(Calendar.MINUTE, -timeNow.minute);
                cal.add(Calendar.SECOND, -timeNow.second);
                hTextView.setTextColor(Color.RED);
            }
            else {
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(startDate);
                cal.setTime(nowDate);
                cal.add(Calendar.HOUR, -cal2.get(Calendar.HOUR_OF_DAY));
                cal.add(Calendar.MINUTE, -cal2.get(Calendar.MINUTE));
                cal.add(Calendar.SECOND, -cal2.get(Calendar.SECOND));
                hTextView.setTextColor(Color.BLACK);
            }
            hTextView.setText(formatter.format(cal.getTime()));
        } catch (ParseException e) {
        }
    }

    @Override
    protected void onStop() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("hour", numberPickerHour.getValue());
        editor.putInt("min", numberPickerMin.getValue());
        editor.putInt("sec", numberPickerSec.getValue());
        editor.commit();
        wl.release();
        lock.reenableKeyguard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.starter, menu);
        //getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.startLine) {
            Intent startLinePage = new Intent(this, StartLine.class);
            startActivity(startLinePage);
            return true;
        }
        if (id == R.id.test) {
            Intent testPage = new Intent(this, StartPage.class);
            startActivity(testPage);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
