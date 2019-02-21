package us.raddev.txtfwd;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	TextView userIdTextView;
    EditText sessionPwdEditText;
    CheckBox isForwardOnCheckbox;
    CheckBox isCapturingOnCheckBox;
    ListView numberListView;
    Button deleteButton;
    Button refreshListsButton;
    private Spinner spinner;
	
	private ArrayList<Message> listViewItems = new ArrayList<Message>();
    private ArrayList<Message> spinnerItems = new ArrayList<Message>();
    private ArrayAdapter<Message> numberAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseApp.initializeApp(this.getApplicationContext());
        sessionPwdEditText = (EditText) findViewById(R.id.sessionPwdEditText);
        userIdTextView = (TextView) findViewById(R.id.userIdTextView);
        isForwardOnCheckbox = (CheckBox) findViewById(R.id.isForwardOnCheckBox);
        isCapturingOnCheckBox = (CheckBox) findViewById(R.id.isCapturingOnCheckBox);
        numberListView = (ListView) findViewById(R.id.numberListView);
        spinner = (Spinner) findViewById(R.id.numberSpinner);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        refreshListsButton = (Button) findViewById(R.id.refreshListsButton);

        //myAdapter.setViewBinder(new MyViewBinder());

        numberAdapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, listViewItems);
        numberListView.setAdapter(numberAdapter);
        final ArrayAdapter<Message> spinnerAdapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, spinnerItems);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the spinnerAdapter to the spinner
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.add(new Message("no filter")); // add no filter item so user can choose to filter on none
        spinnerAdapter.notifyDataSetChanged();

        if (MsgReceiver.mainUser == null) {
            MsgReceiver.mainUser = new FBUser(getApplicationContext());
        }

        userIdTextView.setText(MsgReceiver.mainUser.getUserId());
        Log.d("MainActivity", "forwardOn : " + String.valueOf(MsgReceiver.mainUser.getIsForwardOn()));
        isForwardOnCheckbox.setChecked(MsgReceiver.mainUser.getIsForwardOn());
        isCapturingOnCheckBox.setChecked(MsgReceiver.mainUser.getIsNumberCaptureOn());

        initializeLists(spinnerAdapter);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    isForwardOnCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "checked?");
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    Log.d("MainActivity", "isChecked : " + String.valueOf(isForwardOnCheckbox.isChecked()));
                    MsgReceiver.mainUser.setIsForwardOn(true);
                } else {
                    Log.d("MainActivity", "isChecked (else) : " + String.valueOf(isForwardOnCheckbox.isChecked()));
                    MsgReceiver.mainUser.setIsForwardOn(false);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message item = (Message) spinner.getSelectedItem();
                String itemText = item.get_originatingNumber();
                if (itemText != "no filter") {
                    updateNumberFilter(item.get_capturedNumberId(), false);
                    spinnerAdapter.remove(item);
                    spinnerAdapter.notifyDataSetChanged();
                }
            }

        });

        refreshListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerAdapter.clear();
                spinnerAdapter.add(new Message("no filter"));
                numberAdapter.clear();
                spinnerAdapter.notifyDataSetChanged();
                numberAdapter.notifyDataSetChanged();
                initializeLists(spinnerAdapter);

            }
        });

        isCapturingOnCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "checked? numberCapture");
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    Log.d("MainActivity", "isChecked : " + String.valueOf(isCapturingOnCheckBox.isChecked()));
                    MsgReceiver.mainUser.setIsNumberCaptureOn(true);
                } else {
                    Log.d("MainActivity", "isChecked (else) : " + String.valueOf(isCapturingOnCheckBox.isChecked()));
                    MsgReceiver.mainUser.setIsNumberCaptureOn(false);
                }
            }
        });

        numberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("MainActivity", "item clicked");
                Message m = (Message) numberListView.getItemAtPosition(position);
                updateNumberFilter(m.get_capturedNumberId(), true);
                Log.d("MainActivity", "number : " + m.get_originatingNumber());
                if (!spinnerItems.contains(m)) {
                    spinnerAdapter.add(m);
                    spinnerAdapter.notifyDataSetChanged();
                } else {
                    Log.d("MainActivity", "item already in list");
                }
                ;
            }
        });
    }

    private void updateNumberFilter(int capturedNumberId, boolean isActive) {
        Log.d("MainActivity", "capturedNumberId : " + capturedNumberId);
        TxtFwdDatabaseHelper dbh = new TxtFwdDatabaseHelper(this);

        Log.d("MainActivity", "Saving number to database...");
        SQLiteDatabase db = dbh.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TxtFwdNumberDatabaseContract.TxtFwdTableDef.COLUMN_NAME_FILTER_ON,
                isActive ? 1 : 0);
        long updatedRowId = db.update(
                TxtFwdNumberDatabaseContract.TxtFwdTableDef.TABLE_NAME,
                values,
                "_id = " + String.valueOf(capturedNumberId),
                null);
        Log.d("MainActivity", "updatedRowId : " + String.valueOf(updatedRowId));
    }

    private void initializeLists(ArrayAdapter<Message> spinnerAdapter) {
        TxtFwdDatabaseHelper dbh = new TxtFwdDatabaseHelper(this);

        Log.d("MainActivity", "querying database");
        SQLiteDatabase db = dbh.getReadableDatabase();

        try {
            Cursor c = null;
            String query = "select * from capturedNumber";
            Log.d("MainActivity", query);
            c = db.rawQuery(query, null);

            while (c.moveToNext()) {
                String contactNumber = c.getString(c.getColumnIndex("contactNumber"));
                int capturedNumberId = c.getInt(c.getColumnIndex("_id"));
                int isFilterOn = c.getInt(c.getColumnIndex("isFilterOn"));
                Log.d("MainActivity", "text from query : " + contactNumber);
                Message m = new Message(contactNumber, capturedNumberId);

                numberAdapter.add(m);
                if (isFilterOn == 1) {
                    spinnerAdapter.add(m);
                }
            }
            c.close();
            numberAdapter.notifyDataSetChanged();
            //adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d("MainActivity", e.getMessage());
        }
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
        if (id == R.id.action_saveSessionPwd) {

            MsgReceiver.mainUser.setSessionPassword(sessionPwdEditText.getText().toString());
        }

        return super.onOptionsItemSelected(item);
    }
}
