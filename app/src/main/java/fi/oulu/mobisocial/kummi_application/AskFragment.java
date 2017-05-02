package fi.oulu.mobisocial.kummi_application;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Ask;

public class AskFragment extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private List<HashMap<String,String>> messageDataSet;
    private AskRecyclerViewAdaptor askRecyclerViewAdaptor;
    private ImageView sendMessageButton;
    private TextView textMessage;
    public static final String MESSAGE_TYPE_INCOMING="incommingMessage";
    public static final String MESSAGE_TYPE_OUTGOING="outgoingMessage";

    private EditText type_message_text;
    private String currentMessage;

    Ask askMessage;
    private RequestQueue MyRequestQueue;
    private StringRequest MyStringRequest;
    List<HashMap<String,String>> dummyData;
    public AskFragment(){
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
       // askRecyclerViewAdaptor=new AskRecyclerViewAdaptor(dummyData());

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_ask,container,false);
        // Inflate the layout for this fragment
        recyclerView=(RecyclerView)view.findViewById(R.id.ask_recycler_view);
        recyclerViewLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        ArrayList<HashMap<String,String>> dataset=new ArrayList<>();
        messageDataSet=dataset;


        // Message box
        type_message_text = (EditText)view.findViewById(R.id.type_message_text);

        final int[] count={0};
        sendMessageButton=(ImageView)view.findViewById(R.id.type_message_send);
        textMessage=(TextView)view.findViewById(R.id.type_message_text);

        textMessage.requestFocus();

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textMessage, InputMethodManager.SHOW_IMPLICIT);
        getOnlineData();
        sendMessageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                currentMessage = type_message_text.getText().toString().trim();
                type_message_text.setText("");

                askMessage = new Ask("123123", "nothing much");
                RequestQueue MyRequestQueue = null;
                StringRequest MyStringRequest = null;
                if (currentMessage != null && !currentMessage.isEmpty()) {
                    String data = "";
                    KummiHttpConnection request = new KummiHttpConnection();


                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("x-apiKey", MainActivity.REST_DB_API_KEY);
                    //data=request.postLogin(MainActivity.REST_DB_USERS_URL,strings[0],headers);
                    Log.d("Message ", "onClick: " + currentMessage);


                    /* New API Code */
                    MyRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

                    //String url = "https://kummi-ad21.restdb.io/rest/ask";
                    String url = "https://ruby-on-rails-rest-api-isadi.c9users.io/asks.json";
                    MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //This code is executed if the server responds, whether or not the response contains data.
                            //The String 'response' contains the server's response.
                            Log.d("Response : ", response);

                            getOnlineData();

                        }
                    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //This code is executed if there is an error.
                            Log.d("Error : ", error.toString());
                        }
                    })

                    {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            String mApiKey = "aae81c5685e95a5cc268116b0a6bb0353033f";
                            //headers.put("content-type", "application/json");
                            //headers.put("x-apikey", mApiKey);
                            headers.put("cache-control", "no-cache");
                            return headers;
                        }

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();
                            params.put("ask[user_id]", "1");
                            params.put("ask[message]", currentMessage);
                            return params;

                        }
                    };
                    MyRequestQueue.add(MyStringRequest);
                }
           }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }


//    private void demola(int entry){
//        List<HashMap<String,String>> dataset=dummyData();
//        HashMap<String,String> message=dataset.get(entry%dataset.size());
//        askRecyclerViewAdaptor.insertItem(message);
//    }

    /*private class AskDataProvider extends AsyncTask<String,Void,String> {

        private String action;
        public String askUrl = "https://kummi-ad21.restdb.io/rest/ask";

        public AskDataProvider(String action){
            this.action=action;
        }

        @Override
        protected String doInBackground(String... strings){
            String data="";
            KummiHttpConnection request=new KummiHttpConnection();
            try{
                HashMap<String,String> headers=new HashMap<>();
                headers.put("x-apiKey",MainActivity.REST_DB_API_KEY);
                //data=request.postLogin(MainActivity.REST_DB_USERS_URL,strings[0],headers);
                data=request.readUrl(askUrl,headers);
            }catch(IOException e){
                e.printStackTrace();
            }


            return data;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            MainActivity.saveToSharedPreference(USER_ID,providedUserName);
            MainActivity.saveToSharedPreference(MainActivity.PASSWORD_KEY,providedPassword);

        }
    }*/

    private void getOnlineData(){

        dummyData=new ArrayList<>();
        HashMap<String,String> message=new HashMap<>();
        final String serverResponse;

        /* New API Code */
        MyRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        //String url = "https://kummi-ad21.restdb.io/rest/ask";
        String url = "https://ruby-on-rails-rest-api-isadi.c9users.io/asks.json";
        final HashMap<String, String> finalMessage = new HashMap<>();

        MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Log.d("Response : ", response.toString());

                try {
                    JSONArray jsonarr = new JSONArray(response);

                    for(int i = 0; i < jsonarr.length(); i++){

                        JSONObject jsonobj = jsonarr.getJSONObject(i);

                        // get message
                        String message = jsonobj.getString("message");
                        // get username
                        JSONObject userJson = jsonobj.getJSONObject("user");
                        String username = userJson.getString("username");

                       // Log.d("Message : ", message.toString());
                       // Log.d("userJson : ", userJson.toString());

                        HashMap<String,String> cmessage=new HashMap<>();
                        cmessage.put("message",message);
                        cmessage.put("messageType",MESSAGE_TYPE_OUTGOING);
                        dummyData.add(cmessage);

                    }
                    askRecyclerViewAdaptor=new AskRecyclerViewAdaptor(dummyData);
                    recyclerView.setAdapter(askRecyclerViewAdaptor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Log.d("Error : ", error.toString());
            }
        })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //String mApiKey = "aae81c5685e95a5cc268116b0a6bb0353033f";
                //headers.put("content-type", "application/json");
                //headers.put("x-apikey", mApiKey);
                headers.put("cache-control", "no-cache");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                return params;

            }
        };
        MyRequestQueue.add(MyStringRequest);
         /* New API Code Ends */

        // message.put("messageType",MESSAGE_TYPE_INCOMING);
        // message.put("messageType",MESSAGE_TYPE_OUTGOING);

    }

    @Override
    public void onDetach(){
        super.onDetach();
    }


}
