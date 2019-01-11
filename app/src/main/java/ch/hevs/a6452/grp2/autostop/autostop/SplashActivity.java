package ch.hevs.a6452.grp2.autostop.autostop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// This activity displays a splash screen to give a graphical feedback to the user when the app is started
public class SplashActivity extends AppCompatActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // The class itself is a Runnable, called immediately at creation
        Thread t = new Thread( this );
        t.start();
    }

    @Override
    public void run() {

        // Loading of the app
        loading();

        // Once the app is loaded, we switch to the main activity
        switchToMainActivity();
    }

    // Loads the app
    private void loading()
    {
        // For now, the app doesn't require loading anything

        try {
            // The thread sleeps for 1 second to make the app logo visible long enough
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Switches from the current activity to MainActivity
    private void switchToMainActivity()
    {
        Intent i = new Intent( this, MainActivity.class );
        startActivity( i );
        finish();
    }


}
