package webinar.pubnub.insitu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.flic.lib.FlicBroadcastReceiverFlags;
import io.flic.lib.FlicButton;
import io.flic.lib.FlicManager;
import io.flic.lib.FlicManagerInitializedCallback;
import webinar.pubnub.insitu.fragments.ExplorationFragment;
import webinar.pubnub.insitu.fragments.HomeFragment;
import webinar.pubnub.insitu.managers.SettingsManager;
import webinar.pubnub.insitu.maps.HeatmapsDemoActivity;
import webinar.pubnub.insitu.model.Settings;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,HomeFragment.OnHomeInteractionListener,ExplorationFragment.OnExplorationInteractionListener {

    private static final String TAG = "MainActivity";
    static MainActivity mainActivity;

    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.viewpagertab)
    SmartTabLayout viewPagerTab;
    Intent serviceIntent;
    FlicButton button;
    HashMap<String, Class<? extends Fragment>> fragmentTitleMap;

    public static MainActivity getInstance() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;

        ButterKnife.bind(mainActivity);

        setupLayout();
        startBackgroundService();
        setupTabs();
    }
    FragmentPagerItemAdapter adapter;
    private void setupTabs() {
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Home", HomeFragment.class)
                .add("Explore", ExplorationFragment.class)
                .create());

        viewPager.setAdapter(adapter);

        viewPagerTab.setViewPager(viewPager);
    }

    private void setupLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void startBackgroundService() {
        serviceIntent = new Intent(this, BackgroundService.class);
        if (!Utils.isMyServiceRunning(BackgroundService.class, this)) {
            startService(serviceIntent);
        } else {
            Log.i(TAG, "Service running");
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.manage_symptoms) {
            // Handle the camera action
        } else if (id == R.id.symptoms_on_map) {
            startActivity(new Intent(this, HeatmapsDemoActivity.class));
        } else if (id == R.id.raw_history) {

        } else if (id == R.id.app_settings) {
            startActivity(new Intent(mainActivity, SettingsActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        FlicManager.getInstance(this, new FlicManagerInitializedCallback() {
            @Override
            public void onInitialized(FlicManager manager) {
                button = manager.completeGrabButton(requestCode, resultCode, data);
                if (button != null) {
                    button.registerListenForBroadcast(FlicBroadcastReceiverFlags.UP_OR_DOWN | FlicBroadcastReceiverFlags.REMOVED);
//                    button.setActiveMode(false);
                    Toast.makeText(MainActivity.this, "Grabbed a button", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "button id" + button.getButtonId());
                    Log.i(TAG, "button connection status" + button.getConnectionStatus());

                } else {
                    Toast.makeText(MainActivity.this, "Did not grab any button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public FlicButton getButton() {
        return button;
    }


    @Override
    public void OnHomeInteraction(Uri uri) {

    }

    @Override
    public void OnExplorationInteraction(Uri uri) {

    }
}
