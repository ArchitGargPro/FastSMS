package archit.fastsms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import archit.fastsms.DatabseManager.DBHandler;
import archit.fastsms.DatabseManager.Data;
import archit.fastsms.menuFragments.TutorialFragment;

public class MainActivity extends AppCompatActivity {

    Data data;
    DBHandler handler;
    Button save;
    EditText phone, message;
    FloatingActionButton fab;
    Switch type;
    TextView title;
    String typeVal = "0";
    ImageButton browseContact;
    Fragment menuItem;
    ViewPager viewPager;

    // TODO add new user status

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACT = 0;

    static final int PICK_CONTACT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // check for permission to send SMS
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
        //###################################

        save = findViewById(R.id.save);
        phone = findViewById(R.id.enter_phone);
        message = findViewById(R.id.enter_text);
        type = findViewById(R.id.switch1);
        title = findViewById(R.id.title);
        browseContact = findViewById(R.id.browse_contacts);
        handler = new DBHandler(this, null, null, 1);

//        showTutorial();

        getData();
        setData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        browseContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });

        type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    typeVal = "1";  // SMS
                } else {
                    typeVal = "0";  // whatsapp
                }
            }
        });

        fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                send();
                Snackbar.make(view, R.string.snack, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public class ImageAdapter extends PagerAdapter {

        Context mContext;

        ImageAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        private int[] sliderImageId = new int[]{
                // TODO add image resources
                R.drawable.img1, R.drawable.img2, R.drawable.img3,R.drawable.img4, R.drawable.img5, R.drawable.img6
        };

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(sliderImageId[position]);
            container.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public int getCount() {
            return sliderImageId.length;
        }
    }

    public void showTutorial()
    {
        //TODO Show tutorial
        menuItem = new TutorialFragment();

        viewPager = findViewById(R.id.viewPager);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        viewPager.setAdapter(imageAdapter);
    }

    public void pickContact()
    {
        // Check for permission to read contacts
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACT);
            }
        }
        //######################################

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    public void saveData()
    {
        // Save data to database
        if(validate()) {
            Log.i("info", "Saving Data");
            data = new Data(message.getText().toString(), phone.getText().toString(), typeVal);
            handler.save(data);
            Log.i("here", "saved successfully");
            fab.show();
            title.setText(R.string.title_final);
        }
    }

    public void getData()
    {
        // get data from the database
        Log.i("info", "Getting from db");
        data = handler.load();
    }

    public void setData()
    {
        //setting the text edit texts
        phone.setText(data.getPhone());
        message.setText(data.getMessage());
    }

    //####################################################################################################
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent daata) {
        super.onActivityResult(reqCode, resultCode, daata);

        if(reqCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = daata.getData();
                Cursor c =  managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                        phones.moveToFirst();
                        data.setPhone(phones.getString(phones.getColumnIndex("data1")));
                        setData();
                        phones.close();
                    }
//                  String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }
            }
        }
    }

    //####################################################################################################
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

        switch(id)
        {
            case R.id.action_settings : //show settings
                return true;
            case R.id.action_info : //show info fragment
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //####################################################################################################
    public void send()
    {
        if (data.getType().equals("1")) {
            sendSMSMessage();
            onBackPressed();
        } else {
            sendWhatsapp();
        }
    }

    public boolean validate()
    {
        if(!phone.getText().toString().equals("")){
            if(!(message.getText().toString().equals("") || message.getText().toString().equals("\n"))){
                return true;
            } else {
                Toast.makeText(this, "message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "please enter Phone number", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void sendWhatsapp()
    {
        //send whatsapp message
        Intent i = new Intent(Intent.ACTION_VIEW);
        String url = "https://api.whatsapp.com/send?phone=91"+data.getPhone()+"&text="+data.getMessage()+"&source=&data=#";
        i.setData(Uri.parse(url));
        startActivity(i);
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
