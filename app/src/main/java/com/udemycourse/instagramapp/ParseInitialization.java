package com.udemycourse.instagramapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class ParseInitialization extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*2. PARSE INITIALIZATION WIRING THE PROJECT TO THE PARSE SERVER*/
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("83ca5a61f47bc0045e4773560a44470e445e2a8e")
                .clientKey("89f0bc599508835dbe5f5046bbd34f1f6decaf67")
                .server("http://18.191.2.14/parse/")
                .build()
        );
        /* this is a way to get info from the users even if you dont have an app that
        has a login form with this you will*/
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
