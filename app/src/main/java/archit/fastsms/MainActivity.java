package archit.fastsms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import archit.fastsms.DatabseManager.DBHandler;
import archit.fastsms.DatabseManager.Data;

public class MainActivity extends AppCompatActivity {

    Data data;
    DBHandler handler;
    Button save;
    EditText phone, message;
    FloatingActionButton fab;
    Switch type;
    TextView title;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        save = findViewById(R.id.save);
        phone = findViewById(R.id.enter_phone);
        message = findViewById(R.id.enter_text);
        type = findViewById(R.id.switch1);
        title = findViewById(R.id.title);
        handler = new DBHandler(this, null, null, 1);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
                Snackbar.make(view, R.string.snack, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void saveData()
    {
        // Save data to database
        Log.i("info", "Saving Data");
        data = new Data(message.getText().toString(),phone.getText().toString(), "1" );
        handler.save(data);
        Log.i("here", "saved successfully");
        fab.show();
        title.setText(R.string.title_final);
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

    //####################################################################################################
    public void send()
    {
        if(data.getType().equals("1")) {
            sendSMSMessage();
        } else {
            sendWhatsapp();
        }
    }

    public void sendWhatsapp()
    {
        //TODO send whatsapp message
    }

    protected void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(data.getPhone(), null, data.getMessage(), null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
    }

    //####################################################################################################

//    To disable an activity
//    PackageManager pm = mContext.getPackageManager();
//    pm.setComponentEnabledSetting(new ComponentName("com.xyzapp", "com.xyzapp.MainActivity"),
//    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);

}
