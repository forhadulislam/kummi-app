package fi.oulu.mobisocial.kummi_application;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity{
    private Button signUpButton;
    private EditText usernameEditText, passwordEditText, firstNameEditText, othernamesEditText, emailEditText;

    private String providedUserName, providedPassword, providedFirstName, providedOtherName, providedEmail, providedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText=(EditText)findViewById(R.id.usernameText);
        passwordEditText=(EditText)findViewById(R.id.passwordText);
        firstNameEditText=(EditText)findViewById(R.id.firstnameText);
        othernamesEditText=(EditText)findViewById(R.id.otherNamesText);
        emailEditText=(EditText)findViewById(R.id.emailText);
        signUpButton=(Button)findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                providedEmail=emailEditText.getText().toString().trim();
                providedUserName=usernameEditText.getText().toString().trim();
                providedPassword=passwordEditText.getText().toString().trim();
                providedFirstName=firstNameEditText.getText().toString().trim();
                providedOtherName=othernamesEditText.getText().toString().trim();
                providedUserId=UUID.randomUUID().toString().replaceAll("-","");

                if(providedFirstName.isEmpty()||providedEmail.isEmpty()||providedOtherName.isEmpty()||providedUserName.isEmpty()||providedPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"All fields are required",Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(providedFirstName,providedOtherName,providedUserName,providedPassword,providedEmail,providedUserId);
                }

            }
        });

    }

    private void registerUser(String firstname,String otherNames,String username,String password,String email,String userId){
        RegisterUserTask dataProvider=new RegisterUserTask();


        try{
            JSONObject params=new JSONObject();
            params.put("username",username);
            params.put("password",password);
            params.put("firstname",firstname);
            params.put("otherNames",otherNames);
            params.put("email",email);
            params.put("_id",userId);
            params.put("active",true);



            dataProvider.execute(params.toString());


        }catch(JSONException e){
            e.printStackTrace();
        }

    }
    private  class  RegisterUserTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings){
            String data="";
            KummiHttpConnection request=new KummiHttpConnection();
            try{
                HashMap<String,String> headers=new HashMap<>();
                headers.put("x-apiKey",MainActivity.REST_DB_API_KEY);
                //data=request.postLogin(MainActivity.REST_DB_USERS_URL,strings[0],headers);
                data=request.postLogin(MainActivity.C9_DB_USERS_POST_URL,strings[0],null);
            }catch(IOException e){
                e.printStackTrace();
            }
            return  data;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(s=="OK"){
                MainActivity.saveToSharedPreference(MainActivity.USERNAME_KEY,providedUserName);
                MainActivity.saveToSharedPreference(MainActivity.PASSWORD_KEY,providedPassword);
                MainActivity.saveToSharedPreference(MainActivity.FIRST_NAME_KEY,providedFirstName);
                MainActivity.saveToSharedPreference(MainActivity.OTHERNAME_KEY,providedOtherName);
                MainActivity.saveToSharedPreference(MainActivity.EMAIL_KEY,providedEmail);
                MainActivity.saveToSharedPreference(MainActivity.USER_ID_KEY,providedUserId);
                Toast.makeText(RegisterActivity.this,"User registered",Toast.LENGTH_LONG).show();
                MainActivity.USER_LOGGED_IN=true;
                setContentView(R.layout.activity_login);
                finish();
            }else{
                Toast.makeText(RegisterActivity.this,s,Toast.LENGTH_LONG).show();
            }
        }
    }
}
