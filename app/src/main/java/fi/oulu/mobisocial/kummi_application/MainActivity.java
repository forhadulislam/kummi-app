package fi.oulu.mobisocial.kummi_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;


public class MainActivity extends FragmentActivity{
    public static  boolean USER_LOGGED_IN=false;
    private BottomNavigationView bottomNavigation;

    public static final String USERNAME_KEY = "USERNAME_KEY";
    public static final String PASSWORD_KEY = "PASSWORD_KEY";
    public static final String USER_ID_KEY = "USER_ID_KEY";
    public static final String FIRST_NAME_KEY = "FIRSTNAME_KEY";
    public static final String OTHERNAME_KEY = "OTHERNAME_KEY";
    public static final String EMAIL_KEY = "EMAIL_KEY";

    public static final String REST_DB_API_KEY="aae81c5685e95a5cc268116b0a6bb0353033f";
    public static final String REST_DB_USERS_URL="https://kummi-ad21.restdb.io/rest/users";
    public static final String C9_DB_USERS_POST_URL="https://ruby-on-rails-rest-api-isadi.c9users.io/users";
    public static final String C9_DB_USERS_GET_URL="https://ruby-on-rails-rest-api-isadi.c9users.io/users/login";
    public static final String REST_DB_READ_USER_LOCATION="restdbuserLocationRead";
    public static final String REST_DB_READ_USERS="restdbusersRead";
    public static final String REST_DB_REGISTER="restdbregister";
    public static final String REST_DB_LOGIN="restdblogin";
    public static final String REST_DB_BOOKMARKS="bookmarks";
    public static final String REST_DB_USER_LOCATION_URL="https://kummi-ad21.restdb.io/rest/users/<UserId>/locations?sort=timeStamp&dir=-1&max=1";
    public static SharedPreferences appSharePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation=(BottomNavigationView)findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                selectMenuItem(item);
                return true;
            }
        });
        appSharePreference = PreferenceManager.getDefaultSharedPreferences(this);


    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!USER_LOGGED_IN){
            ShowLoginActivity();
        }

    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment){
        super.onAttachFragment(fragment);
    }

    private void selectMenuItem(MenuItem item){
        Fragment frag=null;
        SupportMapFragment mapFrag=null;
        switch(item.getItemId()){
            case R.id.bottom_nav_meet:
                MeetFragment meetFragment=new MeetFragment();
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
        for(int i=0;i<bottomNavigation.getMenu().size();i++){
            MenuItem menuItem=bottomNavigation.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId()==item.getItemId());
        }
        updateToolbarText(item.getTitle());
    }

    public void EnableApplicationFeature(Fragment frag){
        if(frag!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.container,frag,frag.getTag());
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void updateToolbarText(CharSequence text){
        android.app.ActionBar actionBar=getActionBar();
        if(actionBar!=null){
            actionBar.setTitle(text);
        }
    }

    private void ShowLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
    public static void saveToSharedPreference(String key, String value) {

        appSharePreference.edit().putString(key.toString(), value.toString()).apply();
    }

    public static String retrieveFromSharePreference(String key) {

        return appSharePreference.getString(key, "");
    }


}
