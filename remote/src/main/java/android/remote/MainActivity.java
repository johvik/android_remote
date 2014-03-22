package android.remote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import remote.api.Packet;

public class MainActivity extends Activity implements ConnectionThread.ConnectionCallback {
    private static final int SETTINGS_REQUEST = 1;
    private static final PublicKey PUBLIC_KEY;
    private static ConnectionThread mConnectionThread = null;
    private static MouseModel mMouseModel = null;

    static {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(Packet.SECURE_ALGORITHM_NAME);
            PUBLIC_KEY = keyFactory.generatePublic(new RSAPublicKeySpec(new BigInteger
                    ("17900686349803467590407245497610841405893141742927480160766655641001643580949901588158213700757400243511399818032964045586804501690342252265077481219185014214485728888374353054867518160655689641162726661338612703391368857746173833159760174216202862248980976194010563424574451874170453371678511207001722865543248215144863905978088386755385411854597812508970216959842220973905251129721112911591153536589090732972687970967506798247834219077803226207940508788612927323394535382450773297536624362247704847265263055828980255806746011533620347268535899476726425157302619617157397833253014470180463182871088753627587219586431"), new BigInteger("65537")
            ));
        } catch (GeneralSecurityException e) {
            throw new Error(e);
        }
    }

    private GestureDetector mGestureDetector;
    private TextView mTextView;

    private MouseView mMouseView;
    private MouseController mMouseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private void connect() {
        if (mConnectionThread == null) {
            mConnectionThread = new ConnectionThread(PUBLIC_KEY, "192.168.1.106", 9456, "test",
                    "test", this);
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
