package fi.oulu.mobisocial.kummi_application;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.SupportMenuInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static java.security.AccessController.getContext;

/**
 * Created by opoku on 13-Mar-17.
 */

public class MeetFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public SupportMapFragment mapFragment;
    public GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the fragment_meet for this fragment

        return inflater.inflate(R.layout.fragment_meet,container,false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        locationManager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        super.onActivityCreated(savedInstanceState);
        mapFragment=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(mGoogleApiClient==null){
            mGoogleApiClient=new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();

    }
    //Meet Methods

    private GoogleMap meetMap;
    private LocationManager locationManager;
    public HashMap<Integer,LatLng> students;
    public static final int GEOLOCATION_PERMISSION_CODE=1234;

    @Override
    public void onMapReady(GoogleMap googleMap){
        students=new HashMap<Integer,LatLng>();

        meetMap=googleMap;
        EnaleMapControls(meetMap);
        mGoogleApiClient.connect();
        meetMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng){
                addMarker(latLng);
                students.put(students.size(),latLng);
            }
        });
        meetMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener(){
            @Override
            public void onMapLongClick(LatLng latLng){
                //Todo: Add some dialog to list all online students
                DialogInterface.OnClickListener listener=
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog,int which){
                                LatLng studentToMeet=students.get(which);
                                Location lastKnowLocation=GetLastKnowLocation();
                                MapDataProvider dataProvider=new MapDataProvider(meetMap);
                                String url=dataProvider.getMapsApiDirectionsUrl(new LatLng(lastKnowLocation.getLatitude(),lastKnowLocation.getLongitude()),studentToMeet);
                                dataProvider.getMapDirectionsReader().execute(url);
                            }
                        };

                String[] studentName=new String[students.size()];
                for(int i=0;i<students.size();i++){
                    LatLng latLong=students.get(i);
                    studentName[i]=String.format("%s, %s",latLng.latitude,latLng.longitude);
                }
                AlertDialog dialog=new AlertDialog.Builder(getContext())
                        .setTitle("Select who to meet")
                        .setItems(studentName,listener)
                        .show();
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==GEOLOCATION_PERMISSION_CODE){
            if(grantResults.length==1
                    &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                // We can now safely use the API we requested access to
            }else{
                // Permission was denied or request was cancelled
                Toast.makeText(getContext(),"Meet requires Locations permission",Toast.LENGTH_LONG).show();

            }
        }

    }

    public void AskLocationPermissions(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,

        },GEOLOCATION_PERMISSION_CODE);
    }

    public void addMarker(LatLng point){
        LatLng mapCenter=point;

        meetMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter,13));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.

       Marker marker= meetMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.common_google_signin_btn_icon_dark))
                .position(mapCenter)
                .flat(true)
                .title("Some Student")
                .snippet("Student Location")
                .rotation(245));
        marker.showInfoWindow();
        ZoomToLocation(point);

    }

    public void EnaleMapControls(GoogleMap gMap){

        if(gMap!=null){
            gMap.getUiSettings().setMapToolbarEnabled(true);
            gMap.getUiSettings().setZoomControlsEnabled(true);
            gMap.getUiSettings().setIndoorLevelPickerEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);
            gMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        }
    }

    public void ZoomToLastKnowLocation(){
        try{
            if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
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
            meetMap.setMyLocationEnabled(true);
            Location last=GetLastKnowLocation();



            //do what you want with last location
            if(last!=null){
                ZoomToLocation(last.getLatitude(),last.getLongitude());
            }


        }catch(Exception e){

        }
    }

    public  Location GetLastKnowLocation(){
        try{
            if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                AskLocationPermissions();
                return null;
            }

            Location last=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            return  last;

        }catch(Exception e){
            return  null;
        }
    }

    public void ZoomToLocation(double lat,double longg){
        LatLng mapCenter=new LatLng(lat,longg);
        CameraPosition cameraPosition=CameraPosition.builder()
                .target(mapCenter)
                .zoom(13)
                .bearing(90)
                .build();
        meetMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000,null);
    }

    public void ZoomToLocation(LatLng point){
        ZoomToLocation(point.latitude,point.longitude);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle){
        ZoomToLastKnowLocation();
    }

    @Override
    public void onConnectionSuspended(int i){

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){

    }
}


