package com.android.termeloiwebshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 598;
    private static final int VASARLO_SECRET_KEY = 456;
    private static final int ELADO_SECRET_KEY = 145;
    private static final int SIGN_KEY = 842;

    EditText usernameET;
    EditText passwordET;

    private SharedPreferences pref;

    private FirebaseAuth authUser;
    private GoogleSignInClient googleSignClient;

    private CollectionReference mUsers;
    private FirebaseFirestore myFirestore;
    private Persons person;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = findViewById(R.id.felhasznalonevET);
        passwordET = findViewById(R.id.jelszoET);

        pref = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        authUser = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_KEY) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "Firebase bejelentkezés google fiókkal: " + acc.getId());
            } catch (ApiException e) {
                Log.w(LOG_TAG, " Google bejelentkezési hiba! " + e);
            }

        }
    }

    private void FirebaseGoogleLogin(String token) {
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        authUser.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres bejelentkezés google fiókkal");
                    openShop();
                } else {
                    Log.d(LOG_TAG, "Nem sikerült bejelentkezni google fiókkal! " + task.getException().getMessage());
                }
            }
        });

    }

    public void login(View view) {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        myFirestore = FirebaseFirestore.getInstance();
        mUsers = myFirestore.collection("users");

        if (username.isEmpty() || password.isEmpty()) {
            Log.d(LOG_TAG, "Nincs kitöltve minden mező!");
            return;
        }
        authUser.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres bejelentkezés (email+jelszo)!");

                    readDb(username);

                } else {
                    Log.d(LOG_TAG, "Nem sikerült bejelentkezni (email+jelszo)");
                    Toast.makeText(MainActivity.this, "Nem sikerült bejelentkezni: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void readDb(String email) {

        mUsers.whereEqualTo("email", email).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Persons> item = queryDocumentSnapshots.toObjects(Persons.class);
            person = item.get(0);
            if (person.getTipus().equals("Vásárló")) {
                Log.d(LOG_TAG, "Open vásárló shop!  ");
                openShop();
            }
            if(person.getTipus().equals("Eladó")) {
                Log.d(LOG_TAG, "Open eladó shop! ");
                openEladoShop();
            }
            Log.d(LOG_TAG, person.getNev() + " " + person.getEmail() + " " + person.getTipus());

        });
    }


    public void register(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(registerIntent);

    }


    public void loginWithGoogleAcc(View view) {
        Intent googleIntent = googleSignClient.getSignInIntent();
        startActivityForResult(googleIntent, SIGN_KEY);
    }


    public void openShop() {
        Intent shopIntent = new Intent(this, ShopActivity.class);
        shopIntent.putExtra("SECRET_KEY", VASARLO_SECRET_KEY);
        startActivity(shopIntent);
    }

    public void openEladoShop() {
        Intent eladoShopIntent = new Intent(this, EladoActivity.class);
        eladoShopIntent.putExtra("SECRET_KEY", ELADO_SECRET_KEY);
        startActivity(eladoShopIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userName", usernameET.getText().toString());
        editor.apply();
    }

}

