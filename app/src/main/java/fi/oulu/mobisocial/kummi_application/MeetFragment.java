package fi.oulu.mobisocial.kummi_application;

//<editor-fold desc="Imports">

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.icu.text.SimpleDateFormat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URLEncoder;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//</editor-fold>

/**
 * Created by opoku on 13-Mar-17.
 */

public class MeetFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public SupportMapFragment mapFragment;
    public GoogleApiClient mGoogleApiClient;
    private ImageButton bookmarksButton;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the fragment_meet for this fragment
        return inflater.inflate(R.layout.fragment_meet,container,false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){

        super.onActivityCreated(savedInstanceState);
        locationManager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        bookmarksButton=(ImageButton)getView().findViewById(R.id.bookmarksButton);
        mapFragment=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        if(mGoogleApiClient==null){
            mGoogleApiClient=new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        bookmarksButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showStudentsDialoag();
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        AskLocationPermissions();
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
    public List<HashMap<String,String>> studentList;
    public static final int GEOLOCATION_PERMISSION_CODE=1234;

    @Override
    public void onMapReady(GoogleMap googleMap){
        students=new HashMap<Integer,LatLng>();
        studentList=new ArrayList<>();
        meetMap=googleMap;
        EnaleMapControls(meetMap);
        mGoogleApiClient.connect();
        meetMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng){
                //addMarker(latLng);
                //students.put(students.size(),latLng);
            }
        });
        //<editor-fold desc="Map Event Listeners">
        meetMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener(){
            @Override
            public void onMapLongClick(LatLng latLng){

                showStudentsDialoag();
            }

        });
        //</editor-fold>

        loadStudentsData();

    }

    private void showStudentsDialoag(){
        DialogInterface.OnClickListener listener=
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){

                        HashMap<String,String> student=studentList.get(which);
                        RestDBDataReader datareader=new RestDBDataReader(MainActivity.REST_DB_READ_USER_LOCATION);
                        String studentName=String.format("%s, %s",student.get("firstname"),student.get("otherNames"));
                        datareader.setStudent(studentName);
                        String url=MainActivity.REST_DB_USER_LOCATION_URL.replace("<UserId>",student.get("_id"));
                        datareader.execute(url);

                    }
                };

        String[] studentName=new String[studentList.size()];
        for(int i=0;i<studentList.size();i++){
            // LatLng latLong=students.get(i);
            HashMap<String,String> user=studentList.get(i);
            studentName[i]=String.format("%s, %s",user.get("firstname"),user.get("otherNames"));
        }
        AlertDialog dialog=new AlertDialog.Builder(getContext())
                .setTitle("Select who to meet")
                .setItems(studentName,listener)
                .show();
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

    public void addMarker(LatLng point,String title,String snippet){
        Marker marker=addMarker(point);
        if(title!=null)
            marker.setTitle(title);
        if(snippet!=null)
            marker.setSnippet(snippet);
        marker.showInfoWindow();
    }

    public Marker addMarker(LatLng point){
        LatLng mapCenter=point;

        meetMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter,13));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.

        Marker marker=meetMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.common_google_signin_btn_icon_dark))
                .position(mapCenter)
                .flat(true)
                .rotation(245));
        marker.showInfoWindow();
        ZoomToLocation(point);
        return marker;
    }


    public void loadStudentsData(){

        RestDBDataReader dataReader=new RestDBDataReader(MainActivity.REST_DB_READ_USERS);
        dataReader.execute(MainActivity.REST_DB_USERS_URL);

    }

    public void getStudentLocation(String url){
        RestDBDataReader dataReader=new RestDBDataReader(MainActivity.REST_DB_READ_USER_LOCATION);
        dataReader.execute(url);
    }

    //<editor-fold desc="Utilities">
    @RequiresApi(api=Build.VERSION_CODES.N)
    public static Date parseDateTime(String dateString){
        if(dateString==null) return null;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");

        //new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        //DateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ"); if(dateString.contains("T")) dateString=dateString.replace('T',' '); if(dateString.contains("Z")) dateString=dateString.replace("Z","+0000"); else dateString=dateString.substring(0,dateString.lastIndexOf(':'))+dateString.substring(dateString.lastIndexOf(':')+1);
        try{
            String date=dateString.replaceAll("T"," ");

            return simpleDateFormat.parse(date);
        }catch(ParseException e){
            Log.e("Kummi","Could not parse datetime: "+dateString);
            return null;
        }
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

    public void showBookMarks(){
        try{
            if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                AskLocationPermissions();
                return;
            }

        }catch(Exception e){

        }
    }
//    public  void  showBookMarks(List){
//
//    }
    public Location GetLastKnowLocation(){
        try{
            if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){


                AskLocationPermissions();
                return null;
            }

            //Todo: Check whether the location service  is actually enabled on the device

            Location last=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            return last;

        }catch(Exception e){
            return null;
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
    //</editor-fold>

    //<editor-fold desc="GoogleClientApi overrides">
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
    //</editor-fold>

    //<editor-fold desc="Data Providers">
    private class RestDBDataReader extends AsyncTask<String,String,String>{

        private String action="";
        private String student="";

        public String getStudent(){
            return student;
        }

        public void setStudent(String student){
            this.student=student;
        }

        public RestDBDataReader(String action){
            this.action=action;
        }

        @Override
        protected String doInBackground(String... url){
            String data="";
            try{
                HashMap<String,String> headers=new HashMap<>();
                headers.put("x-apiKey",MainActivity.REST_DB_API_KEY);
                KummiHttpConnection http=new KummiHttpConnection();
                data=http.readUrl(url[0],headers);
            }catch(Exception e){
                // TODO: handle exception
                Log.d("Kummi",e.toString());
            }
            return data;
        }


        @RequiresApi(api=Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String data){
            super.onPostExecute(data);

            try{

                JSONParser parser=new JSONParser();
                switch(action){
                    case MainActivity.REST_DB_READ_USERS:
                        JSONArray array=null;
                        List<HashMap<String,String>> users=null;
                        array=new JSONArray(data);
                        studentList=parser.parseUsers(array);
                        break;
                    case MainActivity.REST_DB_READ_USER_LOCATION:
                        JSONArray lo=new JSONArray(data);
                        HashMap<String,String> location=parser.parseLocation(lo);
                        if(location.size()>0){
                            LatLng latLong=new LatLng(Double.valueOf(location.get("latitude")),Double.valueOf(location.get("longitude")));
                            Date date=parseDateTime(location.get("timeStamp"));

                            String prettyTime="";
                            if(date!=null) prettyTime=(new PrettyTime(date)).toString();
                            addMarker(latLong,getStudent(),prettyTime);
                            Location lastKnowLocation=GetLastKnowLocation();
                            ReadMapDirections dataProvider=new ReadMapDirections();
                            String mapUrl=dataProvider.getMapsApiDirectionsUrl(new LatLng(lastKnowLocation.getLatitude(),lastKnowLocation.getLongitude()),latLong);
                            dataProvider.execute(mapUrl);
                            return;
                        }
                        String progress=String.format("No Last Know location for %s",getStudent());

                        try{
                            Toast.makeText(getActivity(),progress,Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case MainActivity.REST_DB_BOOKMARKS:
                        List<HashMap<String,String>> bookmarks=null;
                        bookmarks=parser.parseUsers(new JSONArray(data));

                        break;
                }

            }catch(JSONException e){
                e.printStackTrace();
                Log.d("kummi",e.toString());
            }
        }


    }

    private class ReadMapDirections extends AsyncTask<String,Void,String>{
        public String getMapsApiDirectionsUrl(LatLng origin,LatLng dest){
            // Origin of route
            String str_origin="origin="+origin.latitude+","+origin.longitude;

            // Destination of route
            String str_dest="destination="+dest.latitude+","+dest.longitude;


            // Sensor enabled
            String sensor="sensor=false";

            // Building the parameters to the web service
            String parameters=str_origin+"&"+str_dest+"&"+sensor;

            // Output format
            String output="json";

            // Building the url to the web service
            String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


            return url;

        }

        @Override
        protected String doInBackground(String... url){
            // TODO Auto-generated method stub
            String data="";
            try{
                KummiHttpConnection http=new KummiHttpConnection();
                data=http.readUrl(url[0],null);
            }catch(Exception e){
                // TODO: handle exception
                Log.d("Kummi",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }




    private class JSONParser{
        public List<List<HashMap<String,String>>> parsePath(JSONObject jObject){
            List<List<HashMap<String,String>>> routes=new ArrayList<>();
            JSONArray jRoutes=null;
            JSONArray jLegs=null;
            JSONArray jSteps=null;
            try{
                jRoutes=jObject.getJSONArray("routes");
                for(int i=0;i<jRoutes.length();i++){
                    jLegs=((JSONObject)jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String,String>> path=new ArrayList<HashMap<String,String>>();
                    for(int j=0;j<jLegs.length();j++){
                        jSteps=((JSONObject)jLegs.get(j)).getJSONArray("steps");
                        for(int k=0;k<jSteps.length();k++){
                            String polyline="";
                            polyline=(String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list=decodePoly(polyline);
                            for(int l=0;l<list.size();l++){
                                HashMap<String,String> hm=new HashMap<String,String>();
                                hm.put("lat",
                                        Double.toString(((LatLng)list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng)list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }

                }

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;

        }

        private List<LatLng> decodePoly(String encoded){
            List<LatLng> poly=new ArrayList<LatLng>();
            int index=0, len=encoded.length();
            int lat=0, lng=0;

            while(index<len){
                int b, shift=0, result=0;
                do{
                    b=encoded.charAt(index++)-63;
                    result|=(b&0x1f)<<shift;
                    shift+=5;
                }while(b>=0x20);
                int dlat=((result&1)!=0?~(result>>1):(result>>1));
                lat+=dlat;

                shift=0;
                result=0;
                do{
                    b=encoded.charAt(index++)-63;
                    result|=(b&0x1f)<<shift;
                    shift+=5;
                }while(b>=0x20);
                int dlng=((result&1)!=0?~(result>>1):(result>>1));
                lng+=dlng;

                LatLng p=new LatLng((((double)lat/1E5)),
                        (((double)lng/1E5)));
                poly.add(p);
            }
            return poly;
        }

        public List<HashMap<String,String>> parseUsers(JSONArray result) throws JSONException{
            List<HashMap<String,String>> users=new ArrayList<>();


            for(int i=0;i<result.length();i++){
                HashMap<String,String> user=new HashMap<>();
                JSONObject userEntry=result.getJSONObject(i);
                user.put("_id",userEntry.getString("_id"));
                user.put("email",userEntry.getString("email"));
                user.put("password",userEntry.getString("password"));
                user.put("active",userEntry.getString("active"));
                user.put("firstname",userEntry.getString("firstname"));
                user.put("otherNames",userEntry.getString("otherNames"));
                user.put("username",userEntry.getString("username"));
                users.add(user);
            }
            Log.d("kummi",users.toString());
            return users;
        }

        public HashMap<String,String> parseLocation(JSONArray lo) throws JSONException{
            HashMap<String,String> location=new HashMap<>();
            if(lo.length()>0){
                JSONObject entry=lo.getJSONObject(0);
                location.put("_id",entry.getString("_id"));
                location.put("latitude",entry.getString("latitude"));
                location.put("longitude",entry.getString("longitude"));
                location.put("timeStamp",entry.getString("timeStamp"));

            }
            return location;
        }
        public List<HashMap<String,String>> parseBookMarks(JSONArray lo) throws JSONException{
            List<HashMap<String,String>> locations=new ArrayList<>();


            for(int i=0;i<lo.length();i++){
                HashMap<String,String> location=new HashMap<>();
                JSONObject locationEntry=lo.getJSONObject(i);
                location.put("_id",locationEntry.getString("_id"));
                location.put("name",locationEntry.getString("name"));
                location.put("description",locationEntry.getString("description"));
                location.put("latitude",locationEntry.getString("latitude"));
                location.put("longitude",locationEntry.getString("longitude"));

                locations.add(location);
            }

            return locations;
        }
    }

    private class ParserTask extends AsyncTask<String,Integer,Object>{
        @Override
        protected Object doInBackground(String... data){
            // TODO Auto-generated method stub
            JSONObject jObject;

            try{
                jObject=new JSONObject(data[0]);
                JSONParser parser=new JSONParser();

                List<List<HashMap<String,String>>> routes=null;
                routes=parser.parsePath(jObject);
                return routes;


            }catch(Exception e){
                e.printStackTrace();
                Log.d("Kummi",e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(Object result){
            ArrayList<LatLng> points=null;
            PolylineOptions polyLineOptions=null;
            List<List<HashMap<String,String>>> routes=(List<List<HashMap<String,String>>>)result;
            // traversing through routes
            for(int i=0;i<routes.size();i++){
                points=new ArrayList<LatLng>();
                polyLineOptions=new PolylineOptions();
                List<HashMap<String,String>> path=routes.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point=path.get(j);

                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));
                    LatLng position=new LatLng(lat,lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(4);
                polyLineOptions.color(Color.BLUE);
            }

            if(polyLineOptions.getPoints().size()>0){
                meetMap.addPolyline(polyLineOptions);
            }

        }
    }
    //</editor-fold>

}


