package fi.oulu.mobisocial.kummi_application;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by opoku on 01-May-17.
 */

public class KummiHttpConnection{

    public String postLogin(String address,String jsonData,HashMap<String,String> headers) throws IOException{
        String data="";
        HttpURLConnection urlConnection=null;

        try{
            URL url=new URL(address);
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("cache-control","no-cache");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            if(headers!=null){
                if(urlConnection!=null){
                    for(String key : headers.keySet()){
                        urlConnection.setRequestProperty(key,headers.get(key));
                    }
                }
            }
            //urlConnection.connect();
            OutputStreamWriter wr=new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(jsonData);
            wr.flush();
            int responseCode=urlConnection.getResponseCode();
            Log.d("myOut",String.format("RESPONSE CODE %s",responseCode));
            if(responseCode==HttpURLConnection.HTTP_OK){
                data="OK";
                return data;
            }
            if(responseCode==HttpURLConnection.HTTP_NOT_FOUND){
                data="NOT_FOUND";
                return data;
            }
            return data=urlConnection.getResponseMessage();

        }catch(Exception e){
            Log.d("kummi",e.toString());
        }finally{
            urlConnection.disconnect();
        }
        return data;
    }


    public String readUrl(String address,HashMap<String,String> headers) throws IOException{
        String data="";
        InputStream istream=null;
        HttpURLConnection urlConnection=null;

        try{
            URL url=new URL(address);
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            if(headers!=null){
                if(urlConnection!=null){
                    for(String key : headers.keySet()){
                        urlConnection.setRequestProperty(key,headers.get(key));
                    }
                    urlConnection.setRequestProperty("Accept-Encoding","");
                }
            }
            urlConnection.connect();
            istream=new BufferedInputStream(urlConnection.getInputStream());
            // urlConnection.connect();

            int responseCode=urlConnection.getResponseCode();
            BufferedReader br=new BufferedReader(new InputStreamReader(istream));
            if(responseCode==HttpURLConnection.HTTP_OK){

                StringBuffer sb=new StringBuffer();

                String line="";
                while((line=br.readLine())!=null){
                    sb.append(line);
                }
                data=sb.toString();
            }else{
                data=urlConnection.getResponseMessage();
            }

            br.close();


        }catch(Exception e){
            Log.d("kummi",e.toString());
            e.printStackTrace();
        }finally{
            istream.close();
            urlConnection.disconnect();
        }
        return data;

    }
}
