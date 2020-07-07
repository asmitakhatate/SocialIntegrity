package com.example.socialintegrity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Declaring Twitter loginButton
    TwitterLoginButton loginButtontw;

    private LoginButton loginButtonfb;

    private CircleImageView circleImageView;
    private TextView txtName , txtEmail;


    CallbackManager callbackManager;


    /**
     * @param savedInstanceState - saves instance state
     * onCreate method takes savedInstanceState as parameter and calls it's super method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Initializing twitter instance
        Twitter.initialize(this);  //Make sure that this statement is added before setContentView() method
        setContentView(R.layout.activity_main);

        //Instantiating loginButton
        loginButtontw = (TwitterLoginButton) findViewById(R.id.login_button_twitter);

         /* twitter
          Adding a callback to loginButton
          These statements will execute when loginButton is clicked
         */

       loginButtontw.setCallback(new Callback<TwitterSession>() {
           @Override
           public void success(Result<TwitterSession> result) {

               TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(true, true, false).enqueue(new Callback<User>() {
                   @Override
                   public void success(Result<User> result) {

                       User user = result.data;
                       String profileImage = user.profileImageUrl;
                       String username = user.name;
                       String email = user.email;

                       Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                       intent.putExtra("username",username);
                       intent.putExtra("email",email);
                       intent.putExtra("image_url",profileImage);
                       startActivity(intent);

                   }

                   @Override
                   public void failure(TwitterException exception) {

                   }
               });

           }

           @Override
           public void failure(TwitterException exception) {

           }
       });


        // Initialiazing Facebook
        loginButtonfb = findViewById(R.id.login_button_facebook);
        txtName = findViewById(R.id.profile_name);
        txtEmail = findViewById(R.id.profile_email);
        circleImageView = findViewById(R.id.profile_pic);

        loginButtonfb.setPermissions(Arrays.asList("email", "public_profile"));

        callbackManager = CallbackManager.Factory.create();

        checkLoginStatus();

        //facebook
        loginButtonfb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }



    //This is for Twitter
    /**
     * @param session
     * This method will get username using session and start a new activity where username will be displayed
     */



    //This for Facebook
    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {
            if (currentAccessToken==null)
            {
                txtName.setText("");
                txtEmail.setText("");
                circleImageView.setImageResource(0);
                Toast.makeText(MainActivity.this,"user logged out",Toast.LENGTH_LONG).show();
            }
            else
                loaduserProfile(currentAccessToken);
        }
    };

    private void loaduserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");

                    String image_url = "https://graph.facebook.com/"+id+"/picture?type=large";
                    String username = (first_name+" "+last_name);
                    //String fbName=first_name+" "+last_name;

                    Intent intentfb = new Intent(MainActivity.this,ProfileActivity.class);

                    intentfb.putExtra("username", username);
                    intentfb.putExtra("email", email);
                    intentfb.putExtra("image_url", image_url);
                    startActivity(intentfb);



                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void checkLoginStatus()
    {
        if(AccessToken.getCurrentAccessToken() !=null)
        {
            loaduserProfile(AccessToken.getCurrentAccessToken());
        }
    }


    //On Activity Result Method for Facebook and twitter Both
    /**
     * @param requestCode - we'll set it to REQUEST_CAMERA
     * @param resultCode - this will store the result code
     * @param data - data will store an intent
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //for Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButtontw.onActivityResult(requestCode, resultCode, data);
    }


}