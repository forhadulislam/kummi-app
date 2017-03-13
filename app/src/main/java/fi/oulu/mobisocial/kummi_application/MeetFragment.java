package fi.oulu.mobisocial.kummi_application;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.view.SupportMenuInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by opoku on 13-Mar-17.
 */

public class MeetFragment extends Fragment implements  OnMapReadyCallback {
    public SupportMapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment_meet for this fragment

        return inflater.inflate(R.layout.fragment_meet, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        locationManager=(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        super.onActivityCreated(savedInstanceState);
        mapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }




    //Meet Methods

    private GoogleMap meetMap;
    private LocationManager locationManager;
    public static final int GEOLOCATION_PERMISSION_CODE = 1234;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        meetMap = googleMap;
        ZoomToLastKnowLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GEOLOCATION_PERMISSION_CODE) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getContext(), "Meet requires Locations permission", Toast.LENGTH_LONG).show();

            }
        }

    }

    public void AskLocationPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,

        }, GEOLOCATION_PERMISSION_CODE);
    }

    public void ZoomToLastKnowLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                AskLocationPermissions();
                return;
            }

            Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            meetMap.setMyLocationEnabled(true);
            //do what you want with last location
            if (last!=null){
                ZoomToLocation(last.getLatitude(), last.getLongitude());
            }
            



        } catch (Exception e) {

        }
    }

    public void ZoomToLocation(double lat, double longg) {
        meetMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longg), 15));
    }

}
