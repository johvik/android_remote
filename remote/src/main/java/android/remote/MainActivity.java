package android.remote;

import android.os.Bundle;
import android.remote.mouse.MouseController;
import android.remote.mouse.MouseModel;
import android.remote.mouse.MouseView;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up MVC
        MouseModel mouseModel = new MouseModel();
        MouseView mouseView = (MouseView) findViewById(R.id.mouseView);
        MouseController mouseController = new MouseController(mouseModel);
        mouseView.setMouseButtonModel(mouseModel);
        mouseView.setOnClickListener(mouseController);
        mouseView.setOnLongClickListener(mouseController);
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
