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
import android.widget.LinearLayout;
import android.widget.TextView;


public class StartLine extends Activity implements LocationListener {

    PowerManager.WakeLock wl;
    KeyguardManager.KeyguardLock lock;
    LocationManager locationManager;
    private LatLong presentLocation;
    private LatLong flag1Pos;
    private LatLong flag2Pos;
    private Double AngelToStartLine;
    TextView long1TextView;
    TextView latt1TextView;
    TextView long2TextView;
    TextView latt2TextView;
    TextView distanceToStartLineTextView;
    TextView timeToStartLineTextView;
    TextView distanceToStartLineInCourceTextView;
    TextView courseTextView;
    TextView angelTextView;
    TextView speedTextView;
    TextView speedKnotsTextView;
    double DistanceToLineOld = 0;
    Boolean ShowStartBouy = true;

    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override

    public void onLocationChanged(Location location) {
        presentLocation.longitude = location.getLongitude();
        presentLocation.lattitude = location.getLatitude();
        presentLocation.course = location.getBearing();
        presentLocation.speed = location.getSpeed();
        UpdateTimeAndDistToStartLine();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start_line);

        long1TextView = (TextView) findViewById(R.id.long1TextView);
        latt1TextView = (TextView) findViewById(R.id.latt1TextView);
        long2TextView = (TextView) findViewById(R.id.long2TextView);
        latt2TextView = (TextView) findViewById(R.id.latt2TextView);
        distanceToStartLineTextView = (TextView) findViewById(R.id.distanceToStartLineTextView);
        timeToStartLineTextView = (TextView) findViewById(R.id.timeToStartLineTextView);
        distanceToStartLineInCourceTextView = (TextView) findViewById(R.id.distToStartLineInCourceTextView);
        courseTextView = (TextView) findViewById(R.id.courseTextView);
        speedTextView = (TextView) findViewById(R.id.speedTextView);
        angelTextView = (TextView) findViewById(R.id.angelTextView);
        speedKnotsTextView = (TextView) findViewById(R.id.speedKnotsTextView);

        presentLocation = new LatLong();
        flag1Pos = new LatLong();
        flag2Pos = new LatLong();

        SharedPreferences settings = getSharedPreferences("StartLine", 0);
        flag1Pos.lattitude = Double.longBitsToDouble(settings.getLong("flag1Pos.lattitude", 0));
        flag1Pos.longitude = Double.longBitsToDouble(settings.getLong("flag1Pos.longitude", 0));
        flag2Pos.lattitude = Double.longBitsToDouble(settings.getLong("flag2Pos.lattitude", 0));
        flag2Pos.longitude = Double.longBitsToDouble(settings.getLong("flag2Pos.longitude", 0));
        ShowStartBouy = settings.getBoolean("showStartBouy", true);
        setPosText(long1TextView, latt1TextView, flag1Pos);
        setPosText(long2TextView, latt2TextView, flag2Pos);

        // P1 = På gatan vid Tomas, P2 = Vid trappan, båt =
        //double dist = Distance(57.062151, 12.29309, 57.062238, 12.293413, 57.061768, 12.293418);
        // P2 = På gatan vid Tomas, P1 = Vid trappan, båt =
        //double dist = Distance(57.062238, 12.293413, 57.062151, 12.29309, 57.061768, 12.293418);
        /*double distToLineInCource = DistanceToStartLineInCource(160, dist);
        double timeToStartLineInCourse = TimeToStartLine(distToLineInCource, 3.08);

        distToLineInCource = DistanceToStartLineInCource(340, dist);
        timeToStartLineInCourse = TimeToStartLine(distToLineInCource, 3.08);

        String tid = getDurationString(100);
        tid =  getDurationString(4250);*/
        /*flag1Pos.lattitude = 57.062151;
        flag1Pos.longitude = 12.29309;
        flag2Pos.lattitude = 57.062238;
        flag2Pos.longitude = 12.293413;
        double distToLineInCource = DistanceToStartLineInCource(160, 100);
        distToLineInCource = DistanceToStartLineInCource(170, 100);
        distToLineInCource = DistanceToStartLineInCource(140, 100);
        distToLineInCource = DistanceToStartLineInCource(340, 100);*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        ShowBouy();
    }

    private void ShowBouy() {
        LinearLayout startBuoyLinearLayout = (LinearLayout) findViewById(R.id.startBuoyLinearLayout);
        if(ShowStartBouy)
            startBuoyLinearLayout.setVisibility( View.VISIBLE);
        else
            startBuoyLinearLayout.setVisibility( View.GONE);
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
        editor.putBoolean("showStartBouy", ShowStartBouy);
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
        if(flag1Pos.lattitude > 0 && flag2Pos.lattitude > 0) { // Both flags set
            double dist = Distance(flag1Pos.lattitude, flag1Pos.longitude, flag2Pos.lattitude, flag2Pos.longitude, presentLocation.lattitude, presentLocation.longitude); // I grader
            double distToLineInCource = DistanceToStartLineInCource(presentLocation.course, dist);
            double timeToStartLine = Math.abs(TimeToStartLine(distToLineInCource, presentLocation.speed));

            distanceToStartLineTextView.setText(String.valueOf(Math.round(dist * 10.0) / 10.0));

            if (dist >= DistanceToLineOld) // Going away
            {
                timeToStartLineTextView.setText("NA");
                distanceToStartLineInCourceTextView.setText("NA");
            }
            else
            {
                timeToStartLineTextView.setText(getDurationString((int)timeToStartLine));
                distanceToStartLineInCourceTextView.setText(String.valueOf(Math.abs(Math.round(distToLineInCource * 10.0) / 10.0)));

            }
            DistanceToLineOld = dist;

            speedTextView.setText(String.valueOf(Math.round(presentLocation.speed * 10.0) / 10.0));
            speedKnotsTextView.setText(String.valueOf(Math.round(presentLocation.speed *3.6 / 1.853 * 10.0) / 10.0));
            courseTextView.setText(String.valueOf(Math.round(presentLocation.course)));
            angelTextView.setText(String.valueOf(Math.round(AngelToStartLine)));
        }
    }

    protected static double Bearing(double lat1, double lon1, double lat2,
                                    double lon2){
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    public static double BearingDiff(double a, double b) {
        double maxBearing = Math.max(a, b);
        double minBearing = Math.min(a, b);
        double antiClockwiseDiff = maxBearing - minBearing;
        double clockwiseDiff = minBearing + 360 - maxBearing;
        return Math.min(antiClockwiseDiff, clockwiseDiff);
    }

    private double DistanceToStartLineInCource(double courceIndDegrees, double distToStartLine90)
    {
        double startLineBearing = Bearing(flag1Pos.lattitude, flag1Pos.longitude, flag2Pos.lattitude, flag2Pos.longitude);
        double angelToStartLine = BearingDiff(courceIndDegrees, startLineBearing);
        AngelToStartLine = angelToStartLine > 90 ? 180 - angelToStartLine : angelToStartLine;
        double kursIRadianer = (AngelToStartLine) * 3.14159 / 180.0;
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
        return h * 1000;
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
        if (id == R.id.startBuoy) {
            if(ShowStartBouy)
                ShowStartBouy = false;
            else
                ShowStartBouy = true;
            ShowBouy();
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

