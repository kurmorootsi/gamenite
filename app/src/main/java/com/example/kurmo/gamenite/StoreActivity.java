package com.example.kurmo.gamenite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StoreActivity extends AppCompatActivity{

    private FirebaseAuth auth;
    private User localUser;
    private Button back_button, knife, chestplate;

    private DatabaseReference database;
    private FirebaseAuth.AuthStateListener authListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("app", "gg: " + auth.getCurrentUser().getUid());
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().getUid();

        }
        database = FirebaseDatabase.getInstance().getReference();
        localUser = new User(user.getUid());

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(StoreActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        knife = (Button) findViewById(R.id.knife);
        chestplate = (Button) findViewById(R.id.chestplate);

        chestplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClass(200);
            }
        });

        knife.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClass(100);
            }
        });

        back_button = (Button) findViewById(R.id.fight);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoreActivity.this, MainActivity.class));
            }
        });
        database.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(
                 new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         Log.i("app", "--------UID---------" + auth.getCurrentUser().getUid());
                         User user = dataSnapshot.getValue(User.class);
                         if (user != null) {
                             localUser.setLevel(user.getLevel());
                             localUser.setGold(user.getGold());
                             localUser.setAttack(user.getAttack());
                             localUser.setDefence(user.getDefence());
                             Log.d("app", "--------GOLD---------" + localUser.getGold());
                             Log.d("app", "--------LEVEL---------" + localUser.getLevel());
                         }
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {
                         // Getting Post failed, log a message
                         Log.w("app", "loadPost:onCancelled", databaseError.toException());
                         // ...
                     }
                 }
         );


    }
    public void registerClass(int id) {
        final Equipment chosenEquipment = new Equipment(id);
        database.child("equipment").child(Integer.toString(id)).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("app", "--------UID---------" + auth.getCurrentUser().getUid());
                        Equipment equipment = dataSnapshot.getValue(Equipment.class);
                        if (equipment != null) {
                            chosenEquipment.setName(equipment.getName());
                            chosenEquipment.setLevel(equipment.getLevel());
                            chosenEquipment.setGold(equipment.getGold());
                            chosenEquipment.setAttack(equipment.getAttack());
                            chosenEquipment.setDefence(equipment.getDefence());
                            Log.d("app", "--------NAME---------" + chosenEquipment.getName());
                            Log.d("app", "--------ITEM-ID---------" + chosenEquipment.getItemID());
                            Log.d("app", "--------GOLD---------" + chosenEquipment.getGold());
                            Log.d("app", "--------LEVEL---------" + chosenEquipment.getLevel());
                            Log.d("app", "--------ATTACK---------" + chosenEquipment.getAttack());
                            Log.d("app", "--------DEFENCE---------" + chosenEquipment.getDefence());
                            if (checkLevel(equipment) && checkGold(equipment)) {
                                purchaseItem(equipment);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("app", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                }
        );
    }
    public boolean checkLevel(Equipment equipment) {
        return localUser.getLevel() >= equipment.getLevel();
    }
    public boolean checkGold(Equipment equipment) {
        return localUser.getGold() >= equipment.getGold();
    }
    public void updateData() {
        database.child("users").child(localUser.getUserId()).setValue(localUser);
    }
    public void purchaseItem(Equipment equipment) {
        localUser.addAttack(equipment.getAttack());
        localUser.addDefence(equipment.getDefence());
        localUser.addGold(-equipment.getGold());
        updateData();
    }

}
