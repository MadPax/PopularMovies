package com.example.maximilianvoss.popularmoviesmv.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ruedigervoss on 17/04/16.
 */
public class PopularMoviesAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private PopularMoviesAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PopularMoviesAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
