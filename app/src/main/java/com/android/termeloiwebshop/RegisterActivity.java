package com.android.termeloiwebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 781;
    private static final int VASARLO_SECRET_KEY = 456;
    private static final int ELADO_SECRET_KEY = 145;

    private FirebaseAuth authUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseFirestore myFirestore;
    private CollectionReference myItems;

    private Persons person;


    EditText emailET;
    EditText jelszoET;
    EditText jelszoAgainET;
    EditText nevET;
    EditText iranyitoszamET;
    EditText varosEt;
    EditText hazszamET;
    RadioGroup userTypeRG;
    TextView szuletesiDatumTW;

    Calendar calendar;
    DatePickerDialog datePicker;

    private SharedPreferences pref;

    private ArrayList<ShoppingItem> shopItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 598) {
            finish();
        }
        emailET = findViewById(R.id.regEmail);
        jelszoET = findViewById(R.id.regJelszo);
        jelszoAgainET = findViewById(R.id.regJelszoAgain);
        iranyitoszamET = findViewById(R.id.regIranyitoSzam);
        nevET = findViewById(R.id.regNev);
        varosEt = findViewById(R.id.regVaros);
        hazszamET = findViewById(R.id.regHazszam);
        szuletesiDatumTW = findViewById(R.id.regSzuletesiDatum);
        userTypeRG = findViewById(R.id.userType);
        userTypeRG.check(R.id.vasarloRB);

        pref = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = pref.getString("userName", "");

        emailET.setText(userName);

        authUser = FirebaseAuth.getInstance();
    }

    public void registration(View view) {
        String email = emailET.getText().toString();
        String jelszo = jelszoET.getText().toString();
        String jelszoAgain = jelszoAgainET.getText().toString();
        String nev = nevET.getText().toString();
        String iranyitoszam = iranyitoszamET.getText().toString();
        String varos = varosEt.getText().toString();
        String hazszam = hazszamET.getText().toString();
        String szuletesiDatum = szuletesiDatumTW.getText().toString();


        RadioButton userTypeRB = userTypeRG.findViewById(userTypeRG.getCheckedRadioButtonId());
        String userType = userTypeRB.getText().toString();


        if (email.isEmpty() || jelszo.isEmpty() || jelszoAgain.isEmpty() || nev.isEmpty() ||
                iranyitoszam.isEmpty() || varos.isEmpty() || hazszam.isEmpty()
                || szuletesiDatum.equals("Születési dátum")) {
            Log.e(LOG_TAG, "Nem töltötte ki az összes mezőt!");
            return;
        }

        if (!jelszo.equals(jelszoAgain)) {
            Log.e(LOG_TAG, "Nem egyeznek meg a beírt jelszavak!");
            return;
        }

        authUser.createUserWithEmailAndPassword(email, jelszo).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres regisztráció!");

                    person = new Persons(nev, szuletesiDatum, iranyitoszam, varos, hazszam, email, userType);
                    addUser();
                    if (userType.equals("Vásárló")) {
                        Log.d(LOG_TAG, "Vásáló shop megnyitás!");
                        openShop();
                    } else {
                        Log.d(LOG_TAG, "Eladó shop menyitás!");
                        openEladoShop();
                    }
                } else {
                    Log.d(LOG_TAG, "Nem sikerült a regisztráció!");
                    Toast.makeText(RegisterActivity.this, "Nem sikerült létrehozni a felhasználót: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.i(LOG_TAG, "Regisztralt: " + email + " jelszó: " + jelszo + " jelszó újra: " + jelszoAgain + " név: " + nev +
                " születési dátum: " + szuletesiDatum + " irányítószám: " + iranyitoszam + " varos: " + varos + " házszám: " + hazszam + " e-mail: " + email + " felhasználó típus: " + userType);


    }

    public void openShop() {
        Intent shopIntent = new Intent(this, ShopActivity.class);
        shopIntent.putExtra("SECRET_KEY", VASARLO_SECRET_KEY);
        startActivity(shopIntent);
    }

    public void openEladoShop() {
        Intent eladoShopIntent = new Intent(this, ShopActivity.class);
        eladoShopIntent.putExtra("SECRET_KEY", ELADO_SECRET_KEY);
        startActivity(eladoShopIntent);
    }

    public void megsem(View view) {
        finish();
    }

    public void dateSelect(View view) {
        calendar = Calendar.getInstance();
        int nap = calendar.get(Calendar.DAY_OF_MONTH);
        int honap = calendar.get(Calendar.MONTH);
        int ev = calendar.get(Calendar.YEAR);

        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                szuletesiDatumTW.setText(year + "." + month + "." + day);
            }
        }, ev, honap, nap);
        datePicker.show();

    }


    public void addUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("nev", person.getNev());
        user.put("szuletesiDatum", person.getSzuletesiDatum());
        user.put("iranyitoszam", person.getIranyitoszam());
        user.put("varos", person.getVaros());
        user.put("hazszam", person.getHazszam());
        user.put("email", person.getEmail());
        user.put("tipus", person.getTipus());
        user.put("kosar", shopItemList);

        db.collection("users").document(person.getEmail()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(LOG_TAG, "DocumentSnapshot sikeres!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error document írás során!", e);
                    }
                });

    }


}