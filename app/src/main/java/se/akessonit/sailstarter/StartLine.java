package se.akessonit.sailstarter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class StartLine extends Activity implements LocationListener {

    PowerManager.WakeLock wl;
    KeyguardManager.KeyguardLock lock;
    LocationManager locationManager;
    private LatLong presentLocation;
    private LatLong flag1Pos;
    private LatLong flag2Pos;
    TextView long1TextView;
    TextView latt1TextView;
    TextView long2TextView;
    TextView latt2TextView;
    TextView distanceToStartLineTextView;
    TextView timeToStartLineTextView;
    TextView distanceToStartLineInCourceTextView;
    TextView courseTextView;
    TextView speedTextView;
    TextView speedKnotsTextView;

    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override

    public void onLocationChanged(Location location) {
        presentLocation.longitude = location.getLatitude();
        presentLocation.lattitude = location.getLongitude();
        presentLocation.course = location.getBearing();
        presentLocation.speed = location.getSpeed();
        UpdateTimeAndDistToStartLine();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_line);

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        long1TextView = (TextView) findViewById(R.id.long1TextView);
        latt1TextView = (TextView) findViewById(R.id.latt1TextView);
        long2TextView = (TextView) findViewById(R.id.long2TextView);
        latt2TextView = (TextView) findViewById(R.id.latt2TextView);
        distanceToStartLineTextView = (TextView) findViewById(R.id.distanceToStartLineTextView);
        timeToStartLineTextView = (TextView) findViewById(R.id.timeToStartLineTextView);
        distanceToStartLineInCourceTextView = (TextView) findViewById(R.id.distToStartLineInCourceTextView);
        courseTextView = (TextView) findViewById(R.id.courseTextView);
        speedTextView = (TextView) findViewById(R.id.speedTextView);
        speedKnotsTextView = (TextView) findViewById(R.id.speedKnotsTextView);

        presentLocation = new LatLong();
        flag1Pos = new LatLong();
        flag2Pos = new LatLong();

        SharedPreferences settings = getSharedPreferences("StartLine", 0);
        flag1Pos.lattitude = Double.longBitsToDouble(settings.getLong("flag1Pos.lattitude", 0));
        flag1Pos.longitude = Double.longBitsToDouble(settings.getLong("flag1Pos.longitude", 0));
        flag2Pos.lattitude = Double.longBitsToDouble(settings.getLong("flag2Pos.lattitude", 0));
        flag2Pos.longitude = Double.longBitsToDouble(settings.getLong("flag2Pos.longitude", 0));
        setPosText(long1TextView, latt1TextView, flag1Pos);
        setPosText(long2TextView, latt2TextView, flag2Pos);

        // P1 = svartskär, P2 = piren, båt = 4,7 metarn
        /*double dist = Distance(57.060889, 12.251611, 57.056917, 12.267639, 57.062028, 12.262944);
        double distToLineInCource = DistanceToStartLineInCource(220, dist);
        double timeToStartLineInCourse = TimeToStartLine(distToLineInCource, 3.08);

        distToLineInCource = DistanceToStartLineInCource(160, dist);
        timeToStartLineInCourse = TimeToStartLine(distToLineInCource, 3.08);

        distToLineInCource = DistanceToStartLineInCource(260, dist);
        timeToStartLineInCourse = TimeToStartLine(distToLineInCource, 3.08);
        String tid = getDurationString(100);
        tid =  getDurationString(4250);*/

        // P1 = På gatan vid Tomas, P2 = Vid trappan, båt =
        /*double dist = Distance(57.062151, 12.29309, 57.062238, 12.293413, 57.061768, 12.293418);
        double distToLineInCource = DistanceToStartLineInCource(160, dist);
        double timeToStartLineInCourse = TimeToStartLine(distToLineInCource, 3.08);

        distToLineInCource = DistanceToStartLineInCource(340, dist);
        timeToStartLineInCourse = TimeToStartLine(distToLineInCource, 3.08);

        String tid = getDurationString(100);
        tid =  getDurationString(4250);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences("StartLine", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("flag1Pos.lattitude", Double.doubleToRawLongBits(flag1Pos.lattitude));
        editor.putLong("flag1Pos.longitude", Double.doubleToRawLongBits(flag1Pos.longitude));
        editor.putLong("flag2Pos.lattitude", Double.doubleToRawLongBits(flag2Pos.lattitude));
        editor.putLong("flag2Pos.longitude", Double.doubleToRawLongBits(flag2Pos.longitude));
        editor.commit();
        wl.release();
        lock.reenableKeyguard();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    private void UpdateTimeAndDistToStartLine(){
        if(flag1Pos.lattitude > 0 && flag2Pos.lattitude > 0)
        { // Both flags set
            double dist = Distance(flag1Pos.lattitude, flag1Pos.longitude, flag2Pos.lattitude, flag2Pos.longitude, presentLocation.lattitude, presentLocation.longitude); // I grader
            double distToLineInCource = DistanceToStartLineInCource(presentLocation.course, dist);
            double timeToStartLine = Math.abs(TimeToStartLine(distToLineInCource, presentLocation.speed));

            distanceToStartLineTextView.setText(String.valueOf(dist));
            timeToStartLineTextView.setText(getDurationString((int)timeToStartLine));
            distanceToStartLineInCourceTextView.setText(String.valueOf(Math.abs(Math.round(distToLineInCource * 10.0) / 10.0)));
            speedTextView.setText(String.valueOf(Math.round(presentLocation.speed * 10.0) / 10.0));
            speedKnotsTextView.setText(String.valueOf(Math.round(presentLocation.speed *3.6 / 1.853 * 10.0) / 10.0));
            courseTextView.setText(String.valueOf(Math.round(presentLocation.course)));
        }
    }

    private double DistanceToStartLineInCource(double courceIndDegrees, double distToStartLine90)
    {
        double startLineBearing = rad2deg(Math.atan( deltaFiLong / deltaFiLat ));
        if(deltaFiLong > 0 && deltaFiLat < 0) // Andra kvadranten
            startLineBearing += 200;
        if(deltaFiLong < 0 && deltaFiLat < 0) // Tredje kvadranten
            startLineBearing += 200;
        if(deltaFiLong < 0 && deltaFiLat > 0) // Fjärde kvadranten
            startLineBearing += 400;

        double kursIRadianer = (courceIndDegrees - startLineBearing) * 3.14159 / 180.0;
        return distToStartLine90 / Math.sin(kursIRadianer);
    }

    private double  TimeToStartLine(double distToStartLineInCource, double speedInMperS)
    {
        if(speedInMperS == 0)
            return 0;
        return distToStartLineInCource / speedInMperS;
    }

    double deltaFiLat;
    double deltaFiLong;

    private double Distance(double lat1, double lon1, double lat2, double lon2, double latBåt, double longBåt) {
        // P1 och P2
        deltaFiLat = deg2rad(lat2) - deg2rad(lat1);
        deltaFiLong = deg2rad(lon2) - deg2rad(lon1);
        // a=SIN(G5/2)*SIN(G5/2)+COS(F5)*COS(F11)*SIN(H5/2)*SIN(H5/2)
        double a = Math.sin(deltaFiLat/2) * Math.sin(deltaFiLat/2) + Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.sin(deltaFiLong/2)*Math.sin(deltaFiLong/2);
        double rotenUrA = Math.sqrt(a);
        double rotenUr1minusA = Math.sqrt(1-a);
        // c = 2*ATAN(J5/K5)
        double c = 2*  Math.atan(rotenUrA/rotenUr1minusA);
        double dBojar = 6371 * c;

        // Båten
        double deltaFiLatBåtP1 = deg2rad(latBåt) - deg2rad(lat1);
        double deltaFiLongBåtP1 = deg2rad(longBåt) - deg2rad(lon1);
        double deltaFiLatBåtP2 = deg2rad(latBåt) - deg2rad(lat2);
        double deltaFiLongBåtP2 = deg2rad(longBåt) - deg2rad(lon2);
        // a=SIN(G18/2)*SIN(G18/2)+COS(F5)*COS(F19)*SIN(H18/2)*SIN(H18/2)
        double aBåtP1 = Math.sin(deltaFiLatBåtP1/2) * Math.sin(deltaFiLatBåtP1/2) + Math.cos(deg2rad(lat1))*Math.cos(deg2rad(latBåt))*Math.sin(deltaFiLongBåtP1/2)*Math.sin(deltaFiLongBåtP1/2);
        double aBåtP2 = Math.sin(deltaFiLatBåtP2/2) * Math.sin(deltaFiLatBåtP2/2) + Math.cos(deg2rad(lat2))*Math.cos(deg2rad(latBåt))*Math.sin(deltaFiLongBåtP2/2)*Math.sin(deltaFiLongBåtP2/2);
        double rotenUrABåtP1 = Math.sqrt(aBåtP1);
        double rotenUrABåtP2 = Math.sqrt(aBåtP2);
        double rotenUr1minusABåtP1 = Math.sqrt(1-rotenUrABåtP1);
        double rotenUr1minusABåtP2 = Math.sqrt(1-rotenUrABåtP2);
        double cBåtP1 = 2 *  Math.atan(rotenUrABåtP1/rotenUr1minusABåtP1);
        double cBåtP2 = 2 *  Math.atan(rotenUrABåtP2/rotenUr1minusABåtP2);
        double dP1TillBåt = 6371 * cBåtP1;
        double dP2TillBåt = 6371 * cBåtP2;
        // Vinkel v1 (radianer) = ACOS((M18*M18+M5*M5-M20*M20)/(2*M18*M5))
        double VinkelV1Radianer = Math.acos((dP1TillBåt*dP1TillBåt+dBojar*dBojar-dP2TillBåt*dP2TillBåt)/(2*dP1TillBåt*dBojar));
        double h = dP1TillBåt * Math.sin(VinkelV1Radianer);
        h = Math.round(h * 10000.0) / 10.0;
        return h;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.starter) {
            Intent startPage = new Intent(this, Starter.class);
            startActivity(startPage);
            return true;
        }
        if (id == R.id.test) {
            Intent testPage = new Intent(this, StartPage.class);
            startActivity(testPage);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pos1buttonClick(View v){
        setPosText(long1TextView, latt1TextView, presentLocation);
        flag1Pos.lattitude = presentLocation.lattitude;
        flag1Pos.longitude = presentLocation.longitude;
    }

    public void pos2buttonClick(View v){
        setPosText(long2TextView, latt2TextView, presentLocation);
        flag2Pos.lattitude = presentLocation.lattitude;
        flag2Pos.longitude = presentLocation.longitude;
    }

    public void setPosText(TextView viewLong, TextView viewLat , LatLong pos)
    {
        viewLong.setText(Location.convert(pos.longitude, Location.FORMAT_SECONDS));
        viewLat.setText(Location.convert(pos.lattitude, Location.FORMAT_SECONDS));
    }


    private String getDurationString(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }
    private String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    private class LatLong{
        private double longitude;
        private double lattitude;
        private double course;
        private double speed;
    }
}

