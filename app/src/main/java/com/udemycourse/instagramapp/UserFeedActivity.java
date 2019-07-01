package com.udemycourse.instagramapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    private TextView mTextMessage;
    ConstraintLayout constraintLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(ParseUser.getCurrentUser().getUsername());
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        constraintLayout = findViewById(R.id.constLayout);


        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        setTitle(username + "'s Story");

        /*HEY GIVE ME ALL THE IMAGE OBJECTS FROM CLASS IMAGE*/
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
        ParseQuery query1 = ParseQuery.getQuery("gameof");
        /*FROM USER THAT HAS THE USERNAME FROM THE INTENT AND SORT THEM */
        query.whereEqualTo("username", username);
        query.orderByDescending("createdAt");
        // SEARCH THE PARSE SERVER
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0){
                    // SEARCH FOR THE FIELD(COLUMN) THAT HAS NAME IMAGE THAT IS DONE BY CREATING AND PARSEFILE OBJECT
                    for (ParseObject object: objects) {
                        ParseFile file = (ParseFile) object.get("image");
                        // AFTER FINIDING THE COLUMN WE WANT TO GET DATA (OR DOWNLOAD)
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e == null && data != null){

                                    /*TO DOWNLOAD THAT IMAGE AND SHOW WE NEED TO CREATE A BITMAP WITCH WILL BE DECODED TO BYTE ARRAY CUZ THATS THE FORMAT THAT
                                    WE ACCESS THAT DATA OR THE FORMAT THAT THESE ARE SAVED HERE WE CONVERT IT TO A BITMAP */
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length,null);
                                    // HERE WE CREATE AN IMAGEVIEW AND PROGRAMICALLY WE ALIGN IT TO THE SCREEN AND WE SET THE IMAGE THAT WE'VE DOWNLOADED FROM THE PARSE SERVER
                                    ImageView imageView = new ImageView(getApplicationContext());

                                    //ADD TO THE LAYOUT
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));
                                    // here we set the bitmap to the imageview
                                    imageView.setImageBitmap(bitmap);
                                    constraintLayout.addView(imageView);
                                }
                            }
                        });
                    }
                }
            }
        });
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
