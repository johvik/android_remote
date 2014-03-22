package android.remote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.remote.connection.ConnectionConfig;
import android.remote.connection.ConnectionThread;
import android.remote.mouse.MouseController;
import android.remote.mouse.MouseModel;
import android.remote.mouse.MouseView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.GeneralSecurityException;

public class MainActivity extends Activity implements ConnectionThread.ConnectionCallback {
    private static final int SETTINGS_REQUEST = 1;
    private static ConnectionThread mConnectionThread = null;
    private static MouseModel mMouseModel = null;

    private GestureDetector mGestureDetector;
    private TextView mTextView;

    private MouseView mMouseView;
    private MouseController mMouseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set default preference values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mTextView = (TextView) findViewById(R.id.textView);
        // Set up MVC
        mMouseView = (MouseView) findViewById(R.id.mouseView); // View will stay the same
        updateMVC();

        // Connect to server
        connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean connected = false;
        if (mConnectionThread != null) {
            mConnectionThread.setConnectionCallback(this);
            connected = mConnectionThread.isConnected();
        }
        updateVisibility(connected);
        Log.d("MainActivity", "connected: " + connected);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mConnectionThread != null) {
            mConnectionThread.setConnectionCallback(null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateVisibility(boolean connected) {
        if (connected) {
            mTextView.setVisibility(View.GONE);
            mMouseView.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mMouseView.setVisibility(View.GONE);
        }
    }

    private void updateMVC() {
        if (mMouseModel == null) {
            mMouseModel = new MouseModel();
        }
        mMouseController = new MouseController(mMouseModel, mMouseView);
        mMouseView.setMouseModel(mMouseModel);

        mGestureDetector = new GestureDetector(getApplicationContext(), mMouseController);
    }

    private ConnectionConfig getConnectionConfig() throws GeneralSecurityException,
            NumberFormatException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serverAddress = sharedPreferences.getString(SettingsFragment
                .KEY_PREF_SERVER_ADDRESS, "");
        String serverPort = sharedPreferences.getString(SettingsFragment.KEY_PREF_SERVER_PORT, "");
        int port = Integer.parseInt(serverPort);
        String userLogin = sharedPreferences.getString(SettingsFragment.KEY_PREF_LOGIN_USER, "");
        String userPassword = sharedPreferences.getString(SettingsFragment
                .KEY_PREF_LOGIN_PASSWORD, "");
        String rsaModulus = sharedPreferences.getString(SettingsFragment.KEY_PREF_RSA_MODULUS, "");
        String rsaExponent = sharedPreferences.getString(SettingsFragment.KEY_PREF_RSA_EXPONENT,
                "");
        return new ConnectionConfig(rsaModulus, rsaExponent, serverAddress, port, userLogin,
                userPassword);
    }

    private void connect() {
        if (mConnectionThread == null) {
            try {
                ConnectionConfig connectionConfig = getConnectionConfig();
                mConnectionThread = new ConnectionThread(connectionConfig, this);
                mMouseModel.reset(); // Reset the state
            } catch (GeneralSecurityException e) {
                Toast.makeText(this, R.string.connection_config_fail, Toast.LENGTH_LONG).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.connection_config_fail, Toast.LENGTH_LONG).show();
            }
        } else {
            mConnectionThread.setConnectionCallback(this);
        }
        mMouseController.setConnectionThread(mConnectionThread);
    }

    @Override
    public void onConnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateVisibility(true);
            }
        });
    }

    @Override
    public void onDisconnect(Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateVisibility(false);
            }
        });
    }
}
