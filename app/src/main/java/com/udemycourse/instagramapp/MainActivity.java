package com.udemycourse.instagramapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , View.OnKeyListener{

    private static final String TAG ="debug";
    TextView userNameStaticTextView;
    EditText usernamePlainText;
    EditText passwordPlainText;
    Button buttonSignUp;
    TextView loginTextView;
    ImageView logoImageView;
    ConstraintLayout layout;
    Boolean signUpModeActive = true;


    /*Setting Bitnami application password to 'RKrGEtN7ylEs'(the default application username is 'user')     */

/*1. HERE WE ARE CREATING AN INTENT SO WHEN WE SIGNUP / LOGIN TO GET US TO THE USER LIST ACTIVITY
* 1.1 AND WE USE THIS METHOD IN MULTIPLE PLACES WHEN WE SIGNUP AND WHEN WE LOGIN */
    public void showUserList(){
        Intent intent = new Intent(getApplicationContext() , UserListActivity.class);
        intent.putExtra("currentUser", ParseUser.getCurrentUser().getUsername());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameStaticTextView = findViewById(R.id.textView);
        usernamePlainText = findViewById(R.id.usernamePlainTextId);
        passwordPlainText = findViewById(R.id.passwordPlainTextId);
        buttonSignUp = findViewById(R.id.signUpButtonId);
        logoImageView = findViewById(R.id.imageViewTop);
        layout = findViewById(R.id.backgroundLayout);
        loginTextView = findViewById(R.id.logInTextViewId);

        /*here we are setting onClickListener to the textView so we can do some jobs*/
        loginTextView.setOnClickListener(this);
        passwordPlainText.setOnKeyListener(this);
        logoImageView.setOnClickListener(this);
        layout.setOnClickListener(this);

    }
    /*3. HERE WE ARE USING THIS ONCLICK WHEN WE WANT TO LISTEN FOR CLICKS IN VIEW/GUI ELEMENTS*/
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.logInTextViewId){

            if (signUpModeActive){
                signUpModeActive = false;
                buttonSignUp.setText("LogIn");
                loginTextView.setText("or , Sign Up");

            } else {
                signUpModeActive = true;
                buttonSignUp.setText("Sign Up");
                loginTextView.setText("or , Login");
                //ParseUser.logOut();
                //userNameStaticTextView.setText("");
            }
        }else if(view.getId() == R.id.imageViewTop || view.getId() == R.id.backgroundLayout){
            /*this will trigger only if the user click either the imageViews that we have or anywhere in the layout outside the fields that we have will hide the keyboard*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

/*4. HERE WE HAVE THE MAIN PART OF THIS CLASS AND IT IS USED TO SAVE DATA TO PARSE DEPENDING ON THE STATE WE ARE BY USING THE 3 STEP
* IF WE ARE IN SIGN UP MODE WE CREATE A NEW USER AND WE SAVE THAT DATA DEPENDING ON WHAT THE GUI COMPONENTS HOLD ELSE IF WE ARE IN
* LOGIN MODE WE CHECK THE PARSE SERVER BY USING THE LOGININBACKGROUND() AND IT QUERIES THE DATASET AND THAN GIVES US THE DATA IF
* THE USER IS AVALIABLE OR NOT*/
    public void signUpLogInClicked(View view) {

        if (usernamePlainText.getText().toString().matches("") || passwordPlainText.getText().toString().matches("")) {
            Toast.makeText(this, "Please provide a username or password", Toast.LENGTH_SHORT).show();
        } else {
            if (signUpModeActive) {
                ParseUser user = new ParseUser();
                user.setUsername(usernamePlainText.getText().toString());
                user.setPassword(passwordPlainText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            String deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
                            Log.d("my_app","DEVICE TOKEN: " + deviceToken);  // --> returns null string

                            Log.i("Singup", "Success");
                            Toast.makeText(MainActivity.this, "well done you have created a new account and automatically login in the Application , if you want to logout and login with another account change the mode to login", Toast.LENGTH_LONG).show();
                            showUserList();
                        } else {
                            Toast.makeText(MainActivity.this, "username may be in use by someone else or you please chose another username", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
            } else {
// if the signup didnt work this is a login situation
                ParseUser.logInInBackground(usernamePlainText.getText().toString(), passwordPlainText.getText().toString(), new LogInCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user != null && e == null){
                            Toast.makeText(MainActivity.this, "You successfully logged in : " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG).show();
                            showUserList();
                        }else{
                            Toast.makeText(MainActivity.this, "sorry for interrupting you but you username or password doesn't match or your account doesn't exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

    }

/*HERE AFTER IMPLEMENTING THE ONKEYPRESSED WE WANT TO DO SOMETHING WHEN A KEY ON THE KEYBOARD IS PRESSED AND WE CALL THE SIGNUP/LOGIN METHOD */
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        /*we want to execute this code when a key is pressed
        * there we have 3 parameters : 1 is the view , 2 is what is the key and 3 is the event that is supposed to happen*/

        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            signUpLogInClicked(view);
        }
        return false;
    }
}
