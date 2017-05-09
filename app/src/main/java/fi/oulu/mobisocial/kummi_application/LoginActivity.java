package fi.oulu.mobisocial.kummi_application;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity{

    private Button loginButton;
    private EditText usernameEditText, passwordEditText, firstNameEditText, othernamesEditText, emailEditText;
    private TextView gotoRegisterTextView;
    private String providedUserName, providedPassword, providedFirstName, providedOtherName, providedEmail, providedUserId;
    private RequestQueue mRequestQueue;

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        if (Build.VERSION.SDK_INT < 16) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//        View decorView = getWindow().getDecorView();
//// Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//// Remember that you should never show the action bar if the
//// status bar is hidden, so hide that too if necessary.
//       getActionBar().hide();
        //actionBar.hide();
        loginButton=(Button)findViewById(R.id.loginButton);
        gotoRegisterTextView =(TextView)findViewById(R.id.gotoRegister);

        usernameEditText=(EditText)findViewById(R.id.usernameText);
        passwordEditText=(EditText)findViewById(R.id.passwordText);


        gotoRegisterTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String username=usernameEditText.getText().toString();
                String password=passwordEditText.getText().toString();
//
                providedPassword=password;
                providedUserName=username;
//

                if(!username.isEmpty()&&!password.isEmpty()){
                    String savedUsername=MainActivity.retrieveFromSharePreference(MainActivity.USERNAME_KEY);
                    String savedPassword=MainActivity.retrieveFromSharePreference(MainActivity.PASSWORD_KEY);

                    if(!savedUsername.isEmpty()||!savedPassword.isEmpty()){
                        //existing username and password mismatch
                        if(!username.trim().equals(savedUsername)||!password.trim().equals(savedPassword)){
                            Toast.makeText(LoginActivity.this,"Username or Password combination does not match.. Please try again",Toast.LENGTH_LONG).show();
                        }else if(username.trim().equals(savedUsername)&&password.trim().equals(savedPassword)){
                            //week login, check both username and password
                            MainActivity.USER_LOGGED_IN=true;
                            setContentView(R.layout.activity_main);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this,"Username or Password combination does not match.. Please try again",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        //share preference data is not found. fallback to online data.
                        loginUser(username,password);
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"Username or Password cannot be empty",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    protected void onResume(){
        super.onResume();
        checkLoggedInUser();
    }

    private void checkLoggedInUser(){

        if(MainActivity.USER_LOGGED_IN){
            setContentView(R.layout.activity_main);
            finish();
            return;
        }


    }

    /**
     * Logs in user
     * Check online for existing of username and password
     * Sync down into applications shared preference the other account info of users
     * @param username
     * @param password
     */
    private void loginUser(String username,String password){
        JsonArrayRequest request;
         String url=MainActivity.REST_DB_USERS_LOGIN_URL.replace("<<username>>",username);
         url=url.replace("<<password>>",password);
        request=new JsonArrayRequest(url,new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response){
                MainActivity.saveToSharedPreference(MainActivity.USERNAME_KEY,providedUserName);
                MainActivity.saveToSharedPreference(MainActivity.PASSWORD_KEY,providedPassword);
                Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                //Todo: sync down other user information from online db
                try{
                    HashMap<String,String> onlineData=new JSONParser().passLoginUser(response);
                    MainActivity.saveToSharedPreference(MainActivity.USER_ID_KEY,onlineData.get("_id"));
                    MainActivity.saveToSharedPreference(MainActivity.EMAIL_KEY,onlineData.get("email"));
                    MainActivity.saveToSharedPreference(MainActivity.FIRST_NAME_KEY,onlineData.get("firstname"));
                    MainActivity.saveToSharedPreference(MainActivity.OTHERNAME_KEY,onlineData.get("otherNames"));
                }catch(JSONException e){

                }
                MainActivity.USER_LOGGED_IN=true;
                setContentView(R.layout.activity_main);

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                String errorMessage=error.getMessage();
                Toast.makeText(LoginActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }){

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("x-apiKey",MainActivity.REST_DB_API_KEY);
                //headers.put("cache-control","no-cache");

                return headers;
            }

        };

        getRequestQueue().add(request);

        /*LoginDataProvider dataProvider=new LoginDataProvider(MainActivity.REST_DB_LOGIN);
        try{
            JSONObject params=new JSONObject();
            params.put("username",username);
            params.put("password",password);

            dataProvider.execute(params.toString());


        }catch(JSONException e){
            e.printStackTrace();
        }*/
    }


    private class LoginDataProvider extends AsyncTask<String,Void,String>{

        private String action;

        public LoginDataProvider(String action){
            this.action=action;
        }

        @Override
        protected String doInBackground(String... strings){
            String data="";
            KummiHttpConnection request=new KummiHttpConnection();
            switch(action){
                case MainActivity.REST_DB_REGISTER:

                    try{
                        HashMap<String,String> headers=new HashMap<>();
                        headers.put("x-apiKey",MainActivity.REST_DB_API_KEY);
                        //data=request.postLogin(MainActivity.REST_DB_USERS_URL,strings[0],headers);
                        data=request.postLogin(MainActivity.C9_DB_USERS_POST_URL,strings[0],null);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    break;
                case MainActivity.REST_DB_LOGIN:
                    try{
                        HashMap<String,String> headers=new HashMap<>();
                        headers.put("x-apiKey",MainActivity.REST_DB_API_KEY);
                        //data=request.postLogin(MainActivity.REST_DB_USERS_URL,strings[0],headers);
                        data=request.postLogin(MainActivity.C9_DB_USERS_GET_URL,strings[0],null);
                    }catch(IOException e){

                    }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            switch(action){
                case MainActivity.REST_DB_REGISTER:
                    if(s=="OK"){
                        MainActivity.saveToSharedPreference(MainActivity.USERNAME_KEY,providedUserName);
                        MainActivity.saveToSharedPreference(MainActivity.PASSWORD_KEY,providedPassword);
                        MainActivity.saveToSharedPreference(MainActivity.FIRST_NAME_KEY,providedFirstName);
                        MainActivity.saveToSharedPreference(MainActivity.OTHERNAME_KEY,providedOtherName);
                        MainActivity.saveToSharedPreference(MainActivity.EMAIL_KEY,providedEmail);
                        MainActivity.saveToSharedPreference(MainActivity.USER_ID_KEY,providedUserId);
                        Toast.makeText(LoginActivity.this,"User registered",Toast.LENGTH_LONG).show();
                        setContentView(R.layout.activity_main);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,s,Toast.LENGTH_LONG).show();
                    }

                    break;
                case MainActivity.REST_DB_LOGIN:
                    if(s=="OK"){
                        MainActivity.saveToSharedPreference(MainActivity.USERNAME_KEY,providedUserName);
                        MainActivity.saveToSharedPreference(MainActivity.PASSWORD_KEY,providedPassword);
                        Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                        MainActivity.USER_LOGGED_IN=true;
                        setContentView(R.layout.activity_main);
                        //Todo: sync down other user information from online db
                        finish();
                    }else if(s.equals("NOT_FOUND")){
                        Toast.makeText(LoginActivity.this,"User Account not found. Have you registered?",Toast.LENGTH_LONG).show();
                    }

                    else{
                        Toast.makeText(LoginActivity.this,s,Toast.LENGTH_LONG).show();
                    }
            }

        }
    }
}
