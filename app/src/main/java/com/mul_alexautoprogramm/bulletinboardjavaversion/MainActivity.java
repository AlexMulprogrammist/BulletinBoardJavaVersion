package com.mul_alexautoprogramm.bulletinboardjavaversion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mul_alexautoprogramm.bulletinboardjavaversion.accounthelper.AccountHelper;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.DataSender;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.PostAdapterRcView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView nav_view;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private TextView userEmailTitleHeader;
    private AlertDialog dialog;
    private Toolbar toolbar;
    private PostAdapterRcView.onItemClickCustom onItemClickCustom;
    private RecyclerView rcView;
    private PostAdapterRcView postAdapterRcView;
    private DbManager dbManager;
    private DataSender dataSender;
    public static String MAUTH = "";
    private String currentCategory = "Cars";
    private final int EDIT_RESULT = 12;
    private AdView adView;
    private AccountHelper accountHelper;
    //Google Sign In Client
    private GoogleSignInClient googleSignInClient;
    private ImageView imUserPhoto;
    public static final int GOOGLE_SIGN_IN_CODE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MyLog", "OnCreate");
        setContentView(R.layout.activity_main);
        addAds();
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Picasso.get().load(account.getPhotoUrl()).into(imUserPhoto);

        }
        if (adView != null) {

            adView.resume();

        }
        Log.d("MyLog", "OnResume");
        if (currentCategory.equals("my_ads")) {

            dbManager.getMyAdsDataFromDb(mAuth.getUid());

        } else {

            dbManager.getDataFromDb(currentCategory);

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {

            adView.pause();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {

            adView.destroy();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case EDIT_RESULT:
                if (resultCode == RESULT_OK && data != null) {

                    currentCategory = data.getStringExtra("cat");

                }
                break;
            case AccountHelper.GOOGLE_SIGN_IN_CODE:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {

                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        Picasso.get().load(account.getPhotoUrl()).into(imUserPhoto);

                        accountHelper.signInFireBaseGoogle(account.getIdToken(), 0);

                    }


                } catch (ApiException e) {

                    e.printStackTrace();

                }
                break;

            case AccountHelper.GOOGLE_SIGN_IN_LINK_CODE:
                Task<GoogleSignInAccount> task2 = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {

                    GoogleSignInAccount account = task2.getResult(ApiException.class);
                    if (account != null) {
                        accountHelper.signInFireBaseGoogle(account.getIdToken(), 1);
                    }


                } catch (ApiException e) {

                    e.printStackTrace();

                }
                break;

        }
    }

    public void getUserData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmailTitleHeader.setText(currentUser.getEmail());
            MAUTH = mAuth.getUid();
        } else {
            userEmailTitleHeader.setText(R.string.signInOrSignUp);
            MAUTH = "";
        }

    }

    private void init() {
        setOnItemClickCustom();
        rcView = findViewById(R.id.rcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        //test!!!
        List<NewPost> arrayPost = new ArrayList<>();
        postAdapterRcView = new PostAdapterRcView(arrayPost, this, onItemClickCustom);
        getDataDB();
        dbManager = new DbManager(dataSender, this);
        postAdapterRcView.setDbManager(dbManager);

        rcView.setAdapter(postAdapterRcView);

        nav_view = findViewById(R.id.nav_view);
        imUserPhoto = nav_view.getHeaderView(0).findViewById(R.id.imFotoUser);

        Menu menu = nav_view.getMenu();

        MenuItem categoryAccount = menu.findItem(R.id.account_category_menu);
        SpannableString spannableString = new SpannableString(categoryAccount.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spannableString.length(), 0);
        categoryAccount.setTitle(spannableString);

        MenuItem categoryAds = menu.findItem(R.id.ads_category_id);
        SpannableString spannableStringAds = new SpannableString(categoryAds.getTitle());
        spannableStringAds.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spannableStringAds.length(), 0);
        categoryAds.setTitle(spannableStringAds);

        drawerLayout = findViewById(R.id.drawer_layout);
        //add button menu  in drawerLayout from activity_main
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        onToolbarItemClick();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.toggle_open, R.string.toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        nav_view.setNavigationItemSelectedListener(this);
        userEmailTitleHeader = nav_view.getHeaderView(0).findViewById(R.id.tvEmail);

        mAuth = FirebaseAuth.getInstance();
        accountHelper = new AccountHelper(this, mAuth);


    }

    private void getDataDB() {

        dataSender = new DataSender() {
            @Override
            public void onDataRecived(List<NewPost> newPostList) {

                Collections.reverse(newPostList);
                postAdapterRcView.updateAdapter(newPostList);

            }
        };

    }

    private void setOnItemClickCustom() {
        onItemClickCustom = new PostAdapterRcView.onItemClickCustom() {
            @Override
            public void onItemSelected(int position) {

            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.id_my_ads:
                currentCategory = "my_ads";
                dbManager.getMyAdsDataFromDb(mAuth.getUid());
                break;
            case R.id.id_cars:
                currentCategory = "Cars";
                dbManager.getDataFromDb("Cars");
                break;
            case R.id.id_pc_ads:
                currentCategory = "Personal computers";
                dbManager.getDataFromDb("Personal computers");
                break;
            case R.id.id_smartphone_ads:
                currentCategory = "Smartphone";
                dbManager.getDataFromDb("Smartphone");
                break;
            case R.id.id_appliances_ads:
                currentCategory = "Appliances";
                dbManager.getDataFromDb("Appliances");
                break;
            case R.id.id_sign_up:

                signUpInDialogInflate(R.string.sign_up, R.string.sign_up_button, R.string.google_sign_up, 0);
                break;
            case R.id.id_sign_in:

                signUpInDialogInflate(R.string.sign_in, R.string.sign_in_button, R.string.google_sign_in, 1);
                break;
            case R.id.id_sign_out:
                accountHelper.signOut();
                imUserPhoto.setImageResource(android.R.color.transparent);

                break;
        }

        return true;
    }

    private void signUpInDialogInflate(int title, int buttonTitle, int bt_signInGoogle_Title, int index) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.allert_sign_up_layout, null);
        dialogBuilder.setView(dialogView);

        TextView titleAlert = dialogView.findViewById(R.id.tvAlertTitle);
        titleAlert.setText(title);

        EditText edEmail = dialogView.findViewById(R.id.edEmail);
        EditText edPassword = dialogView.findViewById(R.id.edPassword);

        Button button_title_email = dialogView.findViewById(R.id.btSignUp);
        SignInButton button_google_sign_in = dialogView.findViewById(R.id.btSignGoogle);
        Button btForgetPassword = dialogView.findViewById(R.id.btForgetPassword);

        switch (index) {

            case 0:
                btForgetPassword.setVisibility(View.GONE);
                break;
            case 1:
                btForgetPassword.setVisibility(View.VISIBLE);
                break;

        }

        button_title_email.setText(buttonTitle);
        //OnCLick btTitle
        button_title_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    accountHelper.signUp(edEmail.getText().toString(), edPassword.getText().toString());

                } else {
                    accountHelper.signIn(edEmail.getText().toString(), edPassword.getText().toString());


                }
                dialog.dismiss();
            }
        });

        //Google bt Onclick SignIn
        button_google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuth.getCurrentUser() != null) {

                    Toast.makeText(MainActivity.this, "You are already signed in", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;

                } else {

                    accountHelper.signInUpGoogle(AccountHelper.GOOGLE_SIGN_IN_CODE);

                }

                dialog.dismiss();

            }
        });

        // Button Forget Password
        btForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edPassword.isShown()) {
                    button_google_sign_in.setVisibility(View.GONE);
                    button_title_email.setVisibility(View.GONE);
                    edPassword.setVisibility(View.GONE);
                    titleAlert.setText(R.string.message_recover_password);
                    btForgetPassword.setText(R.string.send_password);
                } else {
                    if (!edEmail.getText().toString().equals("")) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(edEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(MainActivity.this, R.string.check_your_email, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                } else {

                                    Toast.makeText(MainActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    } else {

                        Toast.makeText(MainActivity.this, R.string.error_enter_your_email, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


        dialog = dialogBuilder.create();
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        }
        dialog.show();


    }

    //banner load
    private void addAds() {

        MobileAds.initialize(this);
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    //Onclick ItemClick Toolbar
    private void onToolbarItemClick(){

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.newAd){

                    if (mAuth.getCurrentUser() != null) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            Intent i = new Intent(MainActivity.this, EditActivity.class);
                            startActivityForResult(i, EDIT_RESULT);
                        } else {

                            accountHelper.showAlertDialogNotVerified(R.string.alert_title, R.string.mail_not_verified);

                        }
                    }

                }
                return false;
            }
        });

    }

}