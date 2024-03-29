package archit.fastsms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import archit.fastsms.DatabseManager.DBHandler;
import archit.fastsms.DatabseManager.Data;

public class SendSMS extends AppCompatActivity {
    DBHandler dbHandler;
    Data data;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        dbHandler = new DBHandler(this, null, null, 1);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                Log.i("Permission Status", "Permission granted");
            } else {
                Log.i("Permission Status", "Requesting Permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        getData();
        send();
    }

    public void getData()
    {
        // get data from the database

        Log.i("info", "Getting from db");
        data = dbHandler.load();
    }

    public void send()
    {
        if(data.getType().equals("1")) {
            Log.i("info", "sending sms");
            sendSMSMessage();
            onBackPressed();
        } else {
            sendWhatsapp();
        }
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

    public void sendWhatsapp()
    {
        // send Whatsapp message
        Intent i = new Intent(Intent.ACTION_VIEW);
        String url = "https://api.whatsapp.com/send?phone=91"+data.getPhone()+"&text="+data.getMessage()+"&source=&data=#";
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
