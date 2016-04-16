package se.akessonit.sailstarter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


public class StartPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start_page);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    public void starterButtonClicked(View view)
    {
        Intent startPage = new Intent(this, Starter.class);
        startActivity(startPage);
    }

    public void starterLineButtonClicked(View view)
    {
        Intent startLinePage = new Intent(this, StartLine.class);
        startActivity(startLinePage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.startSettings) {
            Intent startLinePage = new Intent(this, Settings.class);
            startActivity(startLinePage);
            return true;
        }
        if (id == R.id.startManual) {
            Intent startLinePage = new Intent(this, Settings.class);
            startActivity(startLinePage);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
