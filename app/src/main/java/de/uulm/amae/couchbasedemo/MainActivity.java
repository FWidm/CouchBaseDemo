package de.uulm.amae.couchbasedemo;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.*;
import com.couchbase.lite.android.AndroidContext;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import de.uulm.amae.couchbasedemo.db.CouchBaseManager;


public class MainActivity extends Activity {
    public final String TAG = "CBDemoActivity>Main";
    public static final String DB_NAME = "users";

    private EditText inEmail, inPassword;
    private User currentUser;
    // A Database is a container and a namespace for documents, a scope for queries, and the source and target of replication. Databases are represented by the Database class.
    private Database users;
    private CouchBaseManager cbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set gui elements
        inEmail = (EditText) findViewById(R.id.eEmail);
        inPassword = (EditText) findViewById(R.id.ePassword);
        //default val for user
        currentUser = null;
        cbm=new CouchBaseManager();
        try {
            cbm.initializeManagerInstance(new AndroidContext(this));
            users = cbm.getDatabaseInstance(DB_NAME);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //initialize database with initial login
        Map<String,Object> content=new HashMap<>();
        content.put("email","t@t.com");
        content.put("password","12345");

        String docId=cbm.createDocument(users,content);

        Log.d(TAG,"Document id for credentials is: "+docId);

        Document document=cbm.retrieveDocumentByID(users,docId);

        Log.d(TAG,"Reading doc: "+document.getProperties());

    }

    /**
     * Handles Button Clicks
     *
     * @param v
     */
    public void onClick(View v) {

        if (v.getClass() == Button.class) {
            Button b = (Button) v;
            String email = inEmail.getText().toString();
            String passwd = inPassword.getText().toString();
            if (email.trim().equalsIgnoreCase("")) {
                inEmail.setError("This field can not be blank and must contain a valid email!");
                return;
            }
            if (passwd.trim().equalsIgnoreCase("")) {
                inPassword.setError("This field can not be blank and must be at least 4 characters!");
                return;
            }
            currentUser = new User(email, passwd);

            String loggingIn = getResources().getString(R.string.logging_in);
            Toast toast = Toast.makeText(getApplicationContext(), loggingIn + currentUser, Toast.LENGTH_LONG);
            toast.show();
        }

    }


}