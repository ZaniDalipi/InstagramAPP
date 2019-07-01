package com.udemycourse.instagramapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }
/*1. OKAY SO THIS METHOD IS EXECUTED AFTER THE ACTIVITY HAS STARTED(HAS THE FOCUS)
 * 1.1 ITS A GOOD PLACE TO CHECK FOR THE MAIN MENU IF THE USER HAS GIVEN PERMISSION
  * 1.2 ALSO HERE WE WANT TO GET THE PHOTOS USING THE MAPMENU ICON ON ACTION BAR
  * 1.3 ALSO WE ARE SAVING THAT PHOTO THAT THE USER HAS SELECTED TO A BYTE ARRAY OUTPUT STREAM AND WE GIVE IT TO PARSE BY CREATING NEW TABLE(CLASS)
  * 1.4 THAN WE CREATE A PARSE CLASS TO SAVE THE IMAGE FILE BY ALSO SPECIFYING AS FIELD THE NAME OF THE CURRENT USER SO WE CAN LOOK AFTER WHO SAVED WHAT
  * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("goodWork", "onActivityResult: image is selected ");

/*Down below we have code that is complicated a little bit is a way to save bitmap to the parse server
* 1. we have to create an byteArrayOutputStream so we can than convert that image to byte array and it can flow through in a format that is
* accessible by parse capabilities*/
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("image.png", byteArray);

                ParseObject parseObject = new ParseObject("Image");

                parseObject.put("image", file);
                parseObject.put("username", ParseUser.getCurrentUser().getUsername());

                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(UserListActivity.this, "We've shared your image", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(UserListActivity.this, "sorry to inform you we have some issues to upload the image", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
/*2. HERE WE INFLATE(CONVERTING THE XML TO JAVA OBJECT) THE MENU */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
/*3. HERE WE CHECK FOR PERMISSION AND IF YES WE CAN ACCESS USERS PHOTOS , AND THIS METHOD IS USED WHEN USER HAS CLICKED AN OPTION IN THE MAINMENU BAR*/
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.share_content) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    getPhoto();
                    // and the location problem
                }
            } else if (item.getItemId() == R.id.logout) {
                ParseUser.logOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(this, "Successfully logged out , we wish you come back again :)", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        return super.onOptionsItemSelected(item);
    }


    /*4. HERE WE CREATE A SEPARATE METHOD TO GET THE PHOTOS FROM DEVICE BY USING IMPLICIT INTENT
   4.1 IMPLICIT INTENT(INTENT THAT HAS BEEN DEFINED IMPLICITLY OR WE WANT  IT TO BE ABLE TO FIND ACTIVITIES OUTSIDE OUR APPS THAT ARE COMPATIBLE
   TO THE INTENT THAT WE SPECIFY , EXAMPLE WE CAN HAVE IMPLICIT INTENT THAT
   * IS SEARCHING FOR ACTIVITIES THAT CONTAINS IMAGES LIKE WE HAVE SPECIFIED IN OUR CASE mediastore.images.media.EXTERNAL_CONTENT_URI)*/
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);

    }


/*5. IN THE ON CRETE WE HAVE INITIALIZATION OF THE LIST VIEW AND WE ACCESS THE PARSE USER CLASS FROM PARSE THAN WE GET DATA FROM THAT PARSE USER CLASS
* SO WE CAN GIVE IT TO THE LIST VIEW.
* HERE WE HAVE 2 CIRCUMSTANCES :
* 1. WE WANT TO GET ALL THE FRIENDS IN THE LIST VIEW BUT NOT OUR SELF
* 2. AND WE WANT TO SORT HOW THAT DATA WILL BE DOWNLOADED*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("UserFriends");

        final ListView userListView = findViewById(R.id.usersListView);
        final TextView usernameTextViewStatic = findViewById(R.id.usernameTextViewStatic);

        final ArrayList<String> usersFromParse = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1, usersFromParse);


        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UserListActivity.this , UserFeedActivity.class);
                intent.putExtra("username", usersFromParse.get(i));
                startActivity(intent);
            }
        });

        //Parse.enableLocalDatastore(this);



        /*get data from the parseUser by using parse querry*/
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // getting all the users but not the user that is the current or the logged in one
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        Intent intent = getIntent();
        usernameTextViewStatic.setText(intent.getStringExtra("currentUser"));
        usernameTextViewStatic.animate().rotationBy(360).setDuration(1800);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {
                    for (ParseUser user : objects) {
                        usersFromParse.add(user.getUsername());
                    }
                    userListView.setAdapter(arrayAdapter);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
