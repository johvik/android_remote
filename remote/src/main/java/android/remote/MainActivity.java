package android.remote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.remote.connection.ConnectionConfig;
import android.remote.connection.ConnectionThread;
import android.remote.mouse.MouseModel;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.security.GeneralSecurityException;

public class MainActivity extends Activity implements ConnectionThread.ConnectionCallback,
        ControllerFragment.ControllerFragmentListener {
    private static final MouseModel mMouseModel = new MouseModel();
    private static ConnectionThread mConnectionThread = null;
    private ConnectionThread.ConnectionState mConnectionState = ConnectionThread.ConnectionState
            .CLOSED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        // Set default preference values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        if (savedInstanceState == null) {
            // First time
            getFragmentManager().beginTransaction().replace(R.id.container,
                    NotConnectedFragment.newInstance()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectionThread.ConnectionState connectionState = ConnectionThread.ConnectionState.CLOSED;
        if (mConnectionThread != null) {
            mConnectionThread.setConnectionCallback(this);
            connectionState = mConnectionThread.getConnectionState();
        }
        connectedStateChange(connectionState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mConnectionThread != null) {
            mConnectionThread.setConnectionCallback(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Set items visibility according to the state
        MenuItem connectMenuItem = menu.findItem(R.id.action_connect);
        MenuItem disconnectMenuItem = menu.findItem(R.id.action_disconnect);
        MenuItem terminateMenuItem = menu.findItem(R.id.action_terminate);
        if (connectMenuItem != null && disconnectMenuItem != null && terminateMenuItem != null) {
            switch (mConnectionState) {
                case CONNECTED:
                    connectMenuItem.setVisible(false);
                    break;
                case CLOSED:
                    disconnectMenuItem.setVisible(false);
                    terminateMenuItem.setVisible(false);
                    break;
                case PENDING:
                    connectMenuItem.setVisible(false);
                    disconnectMenuItem.setVisible(false);
                    terminateMenuItem.setVisible(false);
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_connect:
                connect();
                return true;
            case R.id.action_disconnect:
                disconnect();
                return true;
            case R.id.action_terminate:
                terminate();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void connectedStateChange(ConnectionThread.ConnectionState newState) {
        if (mConnectionState != newState) {
            ConnectionThread.ConnectionState oldState = mConnectionState;
            mConnectionState = newState;
            if (oldState == ConnectionThread.ConnectionState.PENDING && newState ==
                    ConnectionThread.ConnectionState.CLOSED) {
                // Failed to connect
                Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_LONG).show();
            }
            setProgressBarIndeterminateVisibility(mConnectionState == ConnectionThread
                    .ConnectionState.PENDING);
            invalidateOptionsMenu();
            updateFragment(oldState);
        }
    }

    private void updateFragment(ConnectionThread.ConnectionState oldState) {
        if (mConnectionState == ConnectionThread.ConnectionState.CONNECTED) {
            getFragmentManager().beginTransaction().replace(R.id.container,
                    ControllerFragment.newInstance()).commit();
        } else if (oldState == ConnectionThread.ConnectionState.CONNECTED) {
            getFragmentManager().beginTransaction().replace(R.id.container,
                    NotConnectedFragment.newInstance()).commit();
        }
    }

    private void connect() {
        if (mConnectionState == ConnectionThread.ConnectionState.CLOSED) {
            connectedStateChange(ConnectionThread.ConnectionState.PENDING);
            try {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);
                ConnectionConfig connectionConfig = SettingsFragment.getConnectionConfig
                        (sharedPreferences);
                mConnectionThread = new ConnectionThread(connectionConfig, this);
                mMouseModel.reset(); // Reset the state
            } catch (GeneralSecurityException e) {
                Toast.makeText(this, R.string.connection_config_fail, Toast.LENGTH_LONG).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.connection_config_fail, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void disconnect() {
        if (mConnectionState == ConnectionThread.ConnectionState.CONNECTED) {
            connectedStateChange(ConnectionThread.ConnectionState.CLOSED);
            if (mConnectionThread != null) {
                mConnectionThread.disconnect();
                mConnectionThread = null;
            }
        }
    }

    private void terminate() {
        if (mConnectionState == ConnectionThread.ConnectionState.CONNECTED) {
            mConnectionThread.terminateRequest(true);
        }
    }

    @Override
    public void onConnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectedStateChange(ConnectionThread.ConnectionState.CONNECTED);
            }
        });
    }

    @Override
    public void onDisconnect(Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionThread = null;
                connectedStateChange(ConnectionThread.ConnectionState.CLOSED);
            }
        });
    }

    /**
     * Get the mouse model. This has to remain the same within the lifetime of the activity.
     *
     * @return The mouse model.
     */
    @Override
    public MouseModel getMouseModel() {
        return mMouseModel;
    }

    @Override
    public ConnectionThread getConnectionThread() {
        return mConnectionThread;
    }
}
