package com.mul_alexautoprogramm.bulletinboardjavaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init(){

        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        //test FireBase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

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
                signUpInDialogInflate(R.string.sign_up, R.string.sign_up_button);
                break;
            case R.id.id_sign_in:
                Toast.makeText(this, "Preset id_sign_in", Toast.LENGTH_SHORT).show();
                signUpInDialogInflate(R.string.sign_in, R.string.sign_in_button);
                break;
            case R.id.id_sign_out:
                Toast.makeText(this, "Preset id_sign_out", Toast.LENGTH_SHORT).show();
                break;
        }

            return true;
    }

    private void signUpInDialogInflate(int title, int buttonTitle) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.allert_sign_up_layout, null);
        dialogBuilder.setView(dialogView);

        TextView titleAlert = dialogView.findViewById(R.id.tvAlertTitle);
        titleAlert.setText(title);

        Button button_title = dialogView.findViewById(R.id.btSignUp);
        button_title.setText(buttonTitle);
        //OnCLick btTitle
        button_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

    }
}