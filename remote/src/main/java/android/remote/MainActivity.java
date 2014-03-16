package android.remote;

import android.os.Bundle;
import android.remote.mousebutton.MouseButtonController;
import android.remote.mousebutton.MouseButtonModel;
import android.remote.mousebutton.MouseButtonView;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up MVC
        {
            // Left
            MouseButtonModel mouseButtonModel = new MouseButtonModel();
            MouseButtonView mouseButtonView = (MouseButtonView) findViewById(R.id
                    .leftMouseButtonView);
            MouseButtonController mouseButtonController = new MouseButtonController
                    (mouseButtonModel);
            mouseButtonView.setMouseButtonModel(mouseButtonModel);
            mouseButtonView.setOnClickListener(mouseButtonController);
            mouseButtonView.setOnLongClickListener(mouseButtonController);
        }
        {
            // Right
            MouseButtonModel mouseButtonModel = new MouseButtonModel();
            MouseButtonView mouseButtonView = (MouseButtonView) findViewById(R.id
                    .rightMouseButtonView);
            MouseButtonController mouseButtonController = new MouseButtonController
                    (mouseButtonModel);
            mouseButtonView.setMouseButtonModel(mouseButtonModel);
            mouseButtonView.setOnClickListener(mouseButtonController);
            mouseButtonView.setOnLongClickListener(mouseButtonController);
        }
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
