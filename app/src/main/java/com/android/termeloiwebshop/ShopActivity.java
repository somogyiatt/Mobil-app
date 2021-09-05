package com.android.termeloiwebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopActivity.class.getName();
    private FirebaseUser user;


    private RecyclerView shopReV;
    private ArrayList<ShoppingItem> shopItemList;
    private ShoppingItemAdapter shopAdapter;


    private  FrameLayout redCircle;
    private TextView countTextView;

    private  int gridNum = 1;
    private int cartItems = 0;
    private boolean viewRow = true;

    private FirebaseFirestore myFirestore;
    private CollectionReference myItems;
    private CollectionReference mUsers;

    private Persons person;
    private ArrayList<ShoppingItem> kosar = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private  String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        user = FirebaseAuth.getInstance().getCurrentUser();
        int secret_key = getIntent().getIntExtra("SECRET_KEY",0);

        if(user != null && secret_key == 456){
            Log.d(LOG_TAG , "Valid bejelentkezett felhasználó használja az oldalt!");
        } else {
            Log.d(LOG_TAG , "Nem bejelentkezett felhasználó használja az oldalt!");
            finish();
        }


        shopReV = findViewById(R.id.shopRV);
        shopReV.setLayoutManager(new GridLayoutManager(this, gridNum));
        shopItemList = new ArrayList<>();

        shopAdapter = new ShoppingItemAdapter(this, shopItemList);
        shopReV.setAdapter(shopAdapter);

        myFirestore = FirebaseFirestore.getInstance();
        myItems = myFirestore.collection("items");
        mUsers = myFirestore.collection("users");

        queryData();
        readDb(user.getEmail());

    }

    private void queryData(){

         shopItemList.clear();

        myItems.orderBy("name").limit(20).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                ShoppingItem item = document.toObject(ShoppingItem.class);
                shopItemList.add(item);
            }

            if(shopItemList.size() == 0){
                dataInit();
                queryData();
            }
            shopAdapter.notifyDataSetChanged();
        });



    }

    private void dataInit() {
        TypedArray itemsImageRes = getResources().obtainTypedArray(R.array.shopItemImages);
        TypedArray itemsRate = getResources().obtainTypedArray(R.array.shopItemRates);
        String[] itemsName = getResources().getStringArray(R.array.shopItemsNames);
        String[] itemsInfo = getResources().getStringArray(R.array.shopItemsDesc);
        String[] itemsPrice = getResources().getStringArray(R.array.shpItemPrice);


        for(int i=0;i < itemsName.length; i++){
            myItems.add(new ShoppingItem(itemsName[i], itemsInfo[i],itemsPrice[i],itemsRate.getFloat(i,0),itemsImageRes.getResourceId(i, 0)));
        }

        itemsImageRes.recycle();


    }
    /* ----------------------- */




/*    -------------------------------     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searcBar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                shopAdapter.getFilter().filter(s);

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutBTN:
                Log.d(LOG_TAG, "Kijelentkezésre kattintott!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.settings:
                Log.d(LOG_TAG, "Beállításokra kattintott!");
                //TODO
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Kosárra kattintott!");
                return true;
            case R.id.viewMode:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.view_mode, 1);
                } else {
                    changeSpanCount(item, R.drawable.rows, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) shopReV.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon() {
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }
        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
    }


    public void readDb(String email) {

        mUsers.whereEqualTo("email", email).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Persons> item =  queryDocumentSnapshots.toObjects(Persons.class);
                        person = item.get(0);
                        kosar = person.getKosar();

                        Log.d(LOG_TAG, "data read: "  + person.getNev() +  " " + person.getEmail());

                });
    }

    public void addItem(ShoppingItem item){
        if(kosar != null) {
            if (kosar.contains(item)) {
                for (ShoppingItem elem : kosar) {
                    if (elem.getName().equals(item.getName())) {
                        elem.setDarab(elem.getDarab() + 1);
                    }
                }
            }
        }
        if(item != null) {
            kosar.add(item);
            person.setKosar(kosar);
            kosarDataUpload();

        }
    }

    public void addUser(){
        Map<String, Object> user = new HashMap<>();
        user.put("nev", person.getNev());
        user.put("szuletesiDatum", person.getSzuletesiDatum());
        user.put("iranyitoszam", person.getIranyitoszam());
        user.put("varos", person.getVaros());
        user.put("hazszam", person.getHazszam());
        user.put("email", person.getEmail());
        user.put("tipus", person.getTipus());
        user.put("kosar",shopItemList);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOG_TAG, "Sikeresen hozzá lett adva ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Nem sikerült hozzáadni", e);
                    }
                });
    }



    public void deleteItem(ShoppingItem item){
        if(kosar.contains(item)){
            for(ShoppingItem elem : kosar){
                if(elem.getName().equals(item.getName())){
                    if(elem.getDarab()>=1){
                        elem.setDarab(elem.getDarab()-1);
                    } else {
                        kosar.remove(item);
                    }
                }

            }
        }
        person.setKosar(kosar);
        kosarDataUpload();
    }


 public void kosarDataUpload(){
     DocumentReference kosarRef = db.collection("users").document(person.getEmail());

     kosarRef
             .update("kosar", person.getKosar())
             .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                     Log.d(LOG_TAG, "DocumentSnapshot successfully updated!");
                 }
             })
             .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Log.w(LOG_TAG, "Error updating document", e);
                 }
             });


 }



}