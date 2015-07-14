package project0.karlnelson.com.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button streamerBtn = (Button) findViewById(R.id.btn_spotify_streamer);
        streamerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getString(R.string.launch_message_start)
                        + streamerBtn.getText().toString().toLowerCase()
                        + getString(R.string.launch_message_end);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            }
        });

        final Button scoresBtn = (Button) findViewById(R.id.btn_scores_app);
        scoresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getString(R.string.launch_message_start)
                        + scoresBtn.getText().toString().toLowerCase()
                        + getString(R.string.launch_message_end);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            }
        });

        final Button libraryBtn = (Button) findViewById(R.id.btn_library_app);
        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getString(R.string.launch_message_start)
                        + libraryBtn.getText().toString().toLowerCase()
                        + getString(R.string.launch_message_end);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            }
        });

        final Button buildItBiggerBtn = (Button) findViewById(R.id.btn_build_it_bigger);
        buildItBiggerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getString(R.string.launch_message_start)
                        + buildItBiggerBtn.getText().toString().toLowerCase()
                        + getString(R.string.launch_message_end);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            }
        });

        final Button readerBtn = (Button) findViewById(R.id.btn_xyz_reader);
        readerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getString(R.string.launch_message_start)
                        + readerBtn.getText().toString().toLowerCase()
                        + getString(R.string.launch_message_end);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            }
        });

        final Button capstoneBtn = (Button) findViewById(R.id.btn_capstone_my_app);
        capstoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getString(R.string.launch_message_start)
                        + getString(R.string.btn_capstone_message)
                        + getString(R.string.launch_message_end);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
