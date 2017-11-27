package app.moodle.moodle;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import app.moodle.moodle.auth.Authenticator;
import app.moodle.moodle.auth.AuthenticatorActivity;
import app.moodle.moodle.models.Course;
import app.moodle.moodle.services.MoodleService;
import app.moodle.moodle.ui.CourseAdapter;
import app.moodle.moodle.ui.RecyclerListView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String API_BASE_URL = "http://moodle.app/";

    private AccountManager mAccountManager;
    private Account mAccount;

    private OkHttpClient mHttpClient;
    private MoodleService mService;
    private String mToken;

    private CourseAdapter mCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check for user account
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(getString(R.string.app_account_type));
        if (accounts.length > 0 && accounts[0] != null) {
            mAccount = accounts[0];
            mToken = mAccountManager.peekAuthToken(mAccount, "full_access");
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navigationHeader = navigationView.getHeaderView(0);

        // populate user profile
        ImageView profilePicture = (ImageView) navigationHeader.findViewById(R.id.profilePicture);
        String avatarUrl = mAccountManager.getUserData(mAccount, Authenticator.KEY_AVATAR_URL);
        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(this).load(avatarUrl).into(profilePicture);
        }
        TextView profileFullname = (TextView) navigationHeader.findViewById(R.id.profileFullname);
        profileFullname.setText(mAccountManager.getUserData(mAccount, Authenticator.KEY_FULLNAME));
        TextView profileEmail = (TextView) navigationHeader.findViewById(R.id.profileEmail);
        profileEmail.setText(mAccountManager.getUserData(mAccount, Authenticator.KEY_EMAIL));
        profileEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        // Prepare Retrofit
        mHttpClient = new OkHttpClient.Builder().build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.client(mHttpClient).build();

        mService = retrofit.create(MoodleService.class);

        // List course
        mCourseAdapter = new CourseAdapter(this);
        LinearLayout emptyView = (LinearLayout) findViewById(android.R.id.empty);
        RecyclerListView accountListView = (RecyclerListView) findViewById(R.id.recycler_view);
        accountListView.setEmptyView(emptyView);
        accountListView.setLayoutManager(new LinearLayoutManager(this));
        accountListView.setAdapter(mCourseAdapter);
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

        if (id == R.id.nav_camera) {
            listCourses();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * User logout
     */
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout_title);
        builder.setMessage(R.string.logout_message);
        builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAccountManager.removeAccount(mAccount, null, null);

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * Get all the courses
     */
    private void listCourses() {
        Call<List<Course>> callListCourses = mService.listCourses(new HashMap<String, String>() {{
            put("wstoken", mToken);
            put("wsfunction", "core_course_get_courses");
            put("moodlewsrestformat", "json");
        }});
        callListCourses.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                List<Course> courses = response.body();
                mCourseAdapter.updateCourses(courses);
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Log.e("ListCourses", t.getMessage());
            }
        });
    }
}
