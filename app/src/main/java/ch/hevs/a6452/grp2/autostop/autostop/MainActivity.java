package ch.hevs.a6452.grp2.autostop.autostop;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
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
import ch.hevs.a6452.grp2.autostop.autostop.Fragments.FragmentAbout;
import ch.hevs.a6452.grp2.autostop.autostop.Fragments.FragmentProfile;
import ch.hevs.a6452.grp2.autostop.autostop.Fragments.FragmentSettings;
import ch.hevs.a6452.grp2.autostop.autostop.Fragments.FragmentStart;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.PotostopSession;
import ch.hevs.a6452.grp2.autostop.autostop.ViewModels.MainActivityViewModel;
import ch.hevs.a6452.grp2.autostop.autostop.ViewModels.ProfileViewModel;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int PERMISSIONS_REQUEST = 100;

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

        checkGps();

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        try {
            fragment = (Fragment) (fragmentClass != null ? fragmentClass.newInstance() : null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void redirectToLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }


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


    private void checkGps(){
        //Check if permission is not granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        //If no permission, display warning message
        if (requestCode != PERMISSIONS_REQUEST || grantResults.length == 1
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, R.string.noGPSgranted, Toast.LENGTH_SHORT).show();
        }
    }


}
