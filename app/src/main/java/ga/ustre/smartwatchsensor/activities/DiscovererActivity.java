package ga.ustre.smartwatchsensor.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import ga.ustre.smartwatchsensor.R;
import ga.ustre.smartwatchsensor.UDPDiscovery;
import ga.ustre.smartwatchsensor.webvisservices.DiscoveryServicesDefinitions;
import utility.Parameters;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DiscovererActivity extends WearableActivity implements UDPDiscovery.Callbacks {
    private static String TAG = "discovererA";
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;


    private boolean mVisible;


    private UDPDiscovery discovery;
    private String address;
    private DiscoveryServicesDefinitions definitions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: created");
        setContentView(R.layout.activity_discoverer);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onPostCreate: ");
        super.onPostCreate(savedInstanceState);
        discovery= new UDPDiscovery(this);
        discovery.SendDiscovery();
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;


    }

    @Override
    public void onAddressDiscovered(String address, DiscoveryServicesDefinitions defs) {
        Log.d(TAG, "onAddressDiscovered: ");
        this.address=address;
        this.definitions=defs;
        Intent i = new Intent(this,FirstActivity.class);
        i.putExtra(Parameters.IP_ADDRESS_PARAMETER,address);
        i.putExtra(Parameters.HTTP_PORT_PARAMETER,defs.getServiceByKey("http").getPort());
        finish();
        startActivity(i);

    }

    @Override
    public void onProgressUpdate(String message) {
        TextView tvProgress= (TextView) findViewById(R.id.progressText);
        tvProgress.setText(message);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        if (discovery!=null){
            discovery.restartSearch();
        }
    }
}
