package fi.oulu.mobisocial.kummi_application;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.service.carrier.CarrierService;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class MainActivity extends FragmentActivity {
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectMenuItem(item);
                return true;
            }
        });


        //Meet initialisations
    }



    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    private void selectMenuItem(MenuItem item) {
        Fragment frag = null;
        SupportMapFragment mapFrag = null;
        switch (item.getItemId()) {
            case R.id.bottom_nav_meet:
                MeetFragment meetFragment = new  MeetFragment();
                EnableApplicationFeature(meetFragment);
                break;
            case R.id.bottom_nav_ask:
                AskFragment askFragment=new AskFragment();
                EnableApplicationFeature(askFragment);
                break;
            case R.id.bottom_nav_do:
                DoFragment doFragment=new DoFragment();
                EnableApplicationFeature(doFragment);

        }

        // uncheck the other items.
        for (int i = 0; i < bottomNavigation.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigation.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }
        updateToolbarText(item.getTitle());
    }

    public void EnableApplicationFeature(Fragment frag) {
        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.container, frag, frag.getTag());
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void updateToolbarText(CharSequence text) {
        android.app.ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }




}
