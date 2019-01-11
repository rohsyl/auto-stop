package ch.hevs.a6452.grp2.autostop.autostop;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.fragments.FragmentAbout;
import ch.hevs.a6452.grp2.autostop.autostop.fragments.FragmentProfile;
import ch.hevs.a6452.grp2.autostop.autostop.fragments.FragmentSettings;
import ch.hevs.a6452.grp2.autostop.autostop.fragments.FragmentStart;
import ch.hevs.a6452.grp2.autostop.autostop.utils.PotostopSession;
import ch.hevs.a6452.grp2.autostop.autostop.viewmodels.MainActivityViewModel;

//TODO : Redirect to the current trip if the status is not "finished"

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    protected TextView lblFullname;

    protected TextView lblEmail;

    private FirebaseAuth mAuth;

    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        //Check if a user is logged in
        if(mAuth.getCurrentUser() == null)
            redirectToLogin();

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        observeViewModel();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FragmentStart fragmentStart = new FragmentStart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentStart).commit();


        View headerView = navigationView.getHeaderView(0);


        lblEmail = headerView.findViewById(R.id.nav_menu_email);
        lblFullname = headerView.findViewById(R.id.nav_menu_fullname);

        //Check if profile is already set
        Bundle extra = getIntent().getExtras();
        //If not : open profile Fragment
        if(extra != null) {
            FragmentProfile fragmentProfile = new FragmentProfile();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentProfile).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_trip) {
            fragmentClass = FragmentStart.class;
        } else if (id == R.id.nav_profile) {
            fragmentClass = FragmentProfile.class;
        } else if (id == R.id.nav_settings) {
            fragmentClass = FragmentSettings.class;
        } else if (id == R.id.nav_about) {
            fragmentClass = FragmentAbout.class;
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            redirectToLogin();
            return true;
        }

        // try to instance the fragment
        try {
            fragment = (Fragment) (fragmentClass != null ? fragmentClass.newInstance() : null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // and display it
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // finally close the drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Redirect to the login activity
     */
    private void redirectToLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    /**
     * Define the observer
     */
    private void observeViewModel(){
        mViewModel.getEmail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                lblEmail.setText(s);
            }
        });
        mViewModel.getFullname().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                lblFullname.setText(s);
            }
        });
    }

    /**
     * Handle permission result
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        //If no permission, display warning message
        if (requestCode != PotostopSession.PERMISSIONS_LOCALIZATION_REQUEST ||
                grantResults.length == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.noGPSgranted, Toast.LENGTH_SHORT).show();
            return;
        }

        if(requestCode != PotostopSession.PERMISSIONS_SMS_REQUEST ||
                grantResults.length == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, R.string.noSMSgranted, Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
