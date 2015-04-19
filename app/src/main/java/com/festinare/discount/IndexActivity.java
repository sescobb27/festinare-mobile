package com.festinare.discount;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import java.util.*;

import com.festinare.discount.auth.LoginActivity;


public class IndexActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                Intent loginIntent = new Intent( getApplicationContext(), LoginActivity.class );
                startActivity( loginIntent );
                finish();

            }
        };

        Timer timerOnTask = new Timer();
        timerOnTask.schedule(task, 5000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.index_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
