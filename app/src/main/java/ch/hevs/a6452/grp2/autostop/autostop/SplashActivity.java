package ch.hevs.a6452.grp2.autostop.autostop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread t = new Thread( this );
        t.start();
    }

    @Override
    public void run() {
        loading();
        switchToMainActivity();
    }

    private void loading()
    {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void switchToMainActivity()
    {
        Intent i = new Intent( this, MainActivity.class );
        startActivity( i );
        finish();
    }


}
