package com.mul_alexautoprogramm.bulletinboardjavaversion.accounthelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mul_alexautoprogramm.bulletinboardjavaversion.MainActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.R;

public class AccountHelper {
    private MainActivity mainActivity;
    private FirebaseAuth mAuth;
    //Google Sign In Client
    private GoogleSignInClient googleSignInClient;
    public static final int GOOGLE_SIGN_IN_CODE = 10;
    public static final int GOOGLE_SIGN_IN_LINK_CODE = 15;
    private String tempEmail, tempPassword;

    public AccountHelper(MainActivity mainActivity, FirebaseAuth myAuth) {
        this.mainActivity = mainActivity;
        this.mAuth = myAuth;
        googleAccountManager();
    }

    //Sign In with Google
    public void signInUpGoogle(int code) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        mainActivity.startActivityForResult(signInIntent, code);

    }

    //Google
    private void googleAccountManager() {

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(mainActivity.getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(mainActivity, googleSignInOptions);


    }

    //Google
    public void signInFireBaseGoogle(String idToken, int index) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //getMail
                    Toast.makeText(mainActivity, "LogInWithGoogleDone", Toast.LENGTH_SHORT).show();
                    if(index == 1) linkEmailAndPassword(tempEmail,tempPassword);
                    mainActivity.getUserData();
                } else {
                    //Toast write Error sign in
                    Toast.makeText(mainActivity, "ErrorLogInGoogle", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //signUp by Email
    public void signUp(String email, String password) {

        if (!email.equals("") && !password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                if (mAuth.getCurrentUser() != null) {

                                    FirebaseUser userCurrent = mAuth.getCurrentUser();
                                    sendEmailVerification(userCurrent);

                                }
                                mainActivity.getUserData();

                            } else {
                                // If sign in fails, display a message to the user.
                                FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) task.getException();
                                if (exception == null) return;
                                if (exception.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {

                                    linkEmailAndPassword(email,password);

                                }


                            }
                        }
                    });
        } else {
            Toast.makeText(mainActivity, "Email or password empty", Toast.LENGTH_SHORT).show();
        }
    }

    //signIn by Email
    public void signIn(String email, String password) {
        if (!email.equals("") && !password.equals("")) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mainActivity.getUserData();


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("MyLogMain", "signInWithCustomToken:failure", task.getException());
                                Toast.makeText(mainActivity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else {
            Toast.makeText(mainActivity, "Email or password empty", Toast.LENGTH_SHORT).show();
        }
    }

    //signOut
    public void signOut() {

        mAuth.signOut();
        googleSignInClient.signOut();
        mainActivity.getUserData();


    }

    private void sendEmailVerification(FirebaseUser user) {

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    showAlertDialog(R.string.alert_title, R.string.mail_verified);

                }
            }
        });

    }

    //Dialog
    public void showAlertDialog(int resourceText, int messageDialog) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(resourceText);
        builder.setMessage(messageDialog);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //No need to add code here
                //When you click on the OK button, the Alert dialog window will close automatically
            }
        });

        builder.create();
        builder.show();

    }

    //Dialog Link
    public void showAlertDialogSingInWithLink(int resourceText, int messageDialog) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(resourceText);
        builder.setMessage(messageDialog);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signInUpGoogle(GOOGLE_SIGN_IN_LINK_CODE);
            }
        });

        builder.create();
        builder.show();

    }

    //Dialog Not Verified
    public void showAlertDialogNotVerified(int resourceText, int messageDialog) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(resourceText);
        builder.setMessage(messageDialog);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOut();
            }
        });

        builder.setNegativeButton(R.string.send_email_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mAuth.getCurrentUser() != null){

                    sendEmailVerification(mAuth.getCurrentUser());


                }
                signOut();

            }
        });



        builder.create();
        builder.show();

    }

    private void linkEmailAndPassword(String email, String password) {

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(mainActivity, R.string.account_linked, Toast.LENGTH_SHORT).show();
                                if (task.getResult() == null) return;
                                mainActivity.getUserData();


                            } else {

                                Toast.makeText(mainActivity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


        }else {
            Log.d("MyLog", "Please Sign In in your Google Account");
            tempEmail = email;
            tempPassword = password;
            showAlertDialogSingInWithLink(R.string.alert_title, R.string.sign_link_message);

        }
    }
}
