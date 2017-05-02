package fi.oulu.mobisocial.kummi_application;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maboelei on 02/05/2017.
 */

public class JSONParser {
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

    public List<HashMap<String,String>> parseUsers(JSONArray result) throws JSONException {
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
