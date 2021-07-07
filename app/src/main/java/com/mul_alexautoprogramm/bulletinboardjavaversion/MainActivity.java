package com.mul_alexautoprogramm.bulletinboardjavaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.PostAdapterRcView;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }



    @Override
    protected void onStart() {
        super.onStart();
        getUserData();

    }

    //OnClickEdit
    public void onClickEdit(View view){

        Intent i = new Intent(MainActivity.this, EditActivity.class);
        startActivity(i);


    }

    private void getUserData(){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userEmailTitleHeader.setText(currentUser.getEmail());
        }else{
            userEmailTitleHeader.setText(R.string.signInOrSignUp);
        }

    }

    private void init(){
        setOnItemClickCustom();
        rcView = findViewById(R.id.rcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        //test!!!
        List<NewPost> arrayTestPost = new ArrayList<>();
        NewPost newPost = new NewPost();

        newPost.setTitle("Mercedes");
        newPost.setPrice("100");
        newPost.setTel_numb("222333444");
        newPost.setDesc("This is mercedes TEST");

        arrayTestPost.add(newPost);
        arrayTestPost.add(newPost);
        arrayTestPost.add(newPost);
        arrayTestPost.add(newPost);
        arrayTestPost.add(newPost);
        arrayTestPost.add(newPost);
        arrayTestPost.add(newPost);
        arrayTestPost.add(newPost);

        dbManager = new DbManager();
        dbManager.getDataFromDb("Personal computers");

        postAdapterRcView = new PostAdapterRcView(arrayTestPost,this, onItemClickCustom);
        rcView.setAdapter(postAdapterRcView);

        nav_view = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
       //add button menu  in drawerLayout from activity_main
        toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.toggle_open, R.string.toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        nav_view.setNavigationItemSelectedListener(this);
        userEmailTitleHeader = nav_view.getHeaderView(0).findViewById(R.id.tvEmail);

        mAuth = FirebaseAuth.getInstance();




    }

    private void setOnItemClickCustom(){
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
                Toast.makeText(this, "Preset id_my_ads", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_cars:
                Toast.makeText(this, "Preset id_cars", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_pc_ads:
                Toast.makeText(this, "Preset pc", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_smartphone_ads:
                Toast.makeText(this, "Preset id_smartphone", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_appliances_ads:
                Toast.makeText(this, "Preset id_appliances", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_sign_up:
                Toast.makeText(this, "Preset id_sign_up", Toast.LENGTH_SHORT).show();
                signUpInDialogInflate(R.string.sign_up, R.string.sign_up_button, 0);
                break;
            case R.id.id_sign_in:
                Toast.makeText(this, "Preset id_sign_in", Toast.LENGTH_SHORT).show();
                signUpInDialogInflate(R.string.sign_in, R.string.sign_in_button, 1);
                break;
            case R.id.id_sign_out:
                signOut();
                Toast.makeText(this, "Preset id_sign_out", Toast.LENGTH_SHORT).show();
                break;
        }

            return true;
    }

    private void signUpInDialogInflate(int title, int buttonTitle, int index) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.allert_sign_up_layout, null);
        dialogBuilder.setView(dialogView);

        TextView titleAlert = dialogView.findViewById(R.id.tvAlertTitle);
        titleAlert.setText(title);

        EditText edEmail = dialogView.findViewById(R.id.edEmail);
        EditText edPassword = dialogView.findViewById(R.id.edPassword);

        Button button_title = dialogView.findViewById(R.id.btSignUp);
        button_title.setText(buttonTitle);
        //OnCLick btTitle
        button_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index == 0){
                    signUp(edEmail.getText().toString(), edPassword.getText().toString());

                }else{
                    signIn(edEmail.getText().toString(), edPassword.getText().toString());

                }
                dialog.dismiss();
            }
        });

        dialog = dialogBuilder.create();
        dialog.show();


    }
    //signUp
    private void signUp(String email, String password) {

        if (!email.equals("") && !password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                getUserData();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("MyLogMain", "signInWithCustomToken:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }else {
            Toast.makeText(this, "Email or password empty", Toast.LENGTH_SHORT).show();
        }
    }
    //signIn
    private void  signIn(String email, String password){
        if(!email.equals("") && !password.equals("")) {


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                               getUserData();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("MyLogMain", "signInWithCustomToken:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }else {
            Toast.makeText(this, "Email or password empty", Toast.LENGTH_SHORT).show();
        }
    }
    //signOut
    private void signOut(){
        mAuth.signOut();
        getUserData();

    }

}