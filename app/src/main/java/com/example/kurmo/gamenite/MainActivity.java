package com.example.kurmo.gamenite;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pusher.pushnotifications.PushNotifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity {

    int playerGold = 0;
    int playerLevel = 1;
    int playerExperience;
    int playerLife = 100;
    int playerDefence = 1;
    int playerAttack = 1;
    int progressXP = 0;
    Dialog myDialog;

    Long lastFight;

    boolean isFighting = false;
    boolean firstTimeLoad = false;
    private CountDownTimer countDownTimer;

    private FirebaseAuth auth;

    private User localUser;
    private Level localLevel;

    private TextView winner_text;
    private TextView attack;
    private TextView defence;
    private TextView gold;
    private TextView experience;
    private TextView level;
    private ProgressBar progress;
    private Button myButton, signOut, store;

    private String opponent = "Monkey";

    private DatabaseReference database;
    private FirebaseAuth.AuthStateListener authListener;
    SeekBar.OnSeekBarChangeListener customSeekBarListener;
    private final CountDownLatch loginLatch = new CountDownLatch (1);
    private boolean callbackResults;

    boolean playerTurn = true;

    int opponentLevel = 0;
    int opponentLife = 100;
    int opponentDefence = 0;
    int opponentAttack = 0;


    Random randomNumberGenerator = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstTimeLoad = true;
        myDialog = new Dialog(this);
        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().getUid();
        }

        localUser = new User(user.getUid());

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        PushNotifications.start(getApplicationContext(), "2379b56b-67f4-449f-8993-f22342ead767");
        store = (Button) findViewById(R.id.store);
        signOut = (Button) findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StoreActivity.class));
            }
        });

        gold = (TextView) findViewById(R.id.gold);
        attack = (TextView) findViewById(R.id.attack);
        winner_text = (TextView) findViewById(R.id.winner);
        defence = (TextView) findViewById(R.id.defence);
        database = FirebaseDatabase.getInstance().getReference();
        level = (TextView) findViewById(R.id.level);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        myButton = (Button) findViewById(R.id.rollButton);

//        Equipment equipment = new Equipment(100, "Wooden Knife", 3, 4,0,8);
//        database.child("equipment").child(Integer.toString(equipment.getItemID())).setValue(equipment);
//        Equipment equipment1 = new Equipment(200, "Wooden Platebody", 6, 0,3,6);
//        database.child("equipment").child(Integer.toString(equipment1.getItemID())).setValue(equipment1);
//        Equipment equipment2 = new Equipment(300, "Wooden Platelegs", 5, 0,2,4);
//        database.child("equipment").child(Integer.toString(equipment2.getItemID())).setValue(equipment2);
//        Equipment equipment3 = new Equipment(400, "Wooden Helmet", 4, 0,1,2);
//        database.child("equipment").child(Integer.toString(equipment3.getItemID())).setValue(equipment3);
//        Equipment equipment4 = new Equipment(500, "Wooden Shield", 3, 0,2,4);
//        database.child("equipment").child(Integer.toString(equipment4.getItemID())).setValue(equipment4);
        customSeekBarListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        updateSeekbar(progress);
                        Log.d("app", "seekbar: " + localUser.getSeekbar());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                };
        loadUserData();
    }
    public void ShowPopup(View v) {
        myDialog.setContentView(R.layout.fight_result);
        myDialog.show();
    }
    public void loadUserData(){
        database.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        Log.i("app", "--------UID---------" + auth.getCurrentUser().getUid());
                        User user = dataSnapshot.getValue(User.class);
//                        localUser.setUserID(auth.getCurrentUser().getUid());
                        if (user != null) {
                            localUser.setExperience(user.getExperience());
                            localUser.setLevel(user.getLevel());
                            localUser.setSeekbar(user.getSeekbar());
                            localUser.setGold(user.getGold());
                            localUser.setCountdown(user.getCountdown());
                            localUser.setNumber(user.getNumber());
                            localUser.setAttack(user.getAttack());
                            localUser.setDefence(user.getDefence());
                            localUser.setFighting(user.getFighting());

                            updateData();
                            Log.d("app", "--------EXPERIENCE---------" + localUser.getExperience());
                            Log.d("app", "--------GOLD---------" + localUser.getGold());
                            Log.d("app", "----COUNTDOWN----" + localUser.getCountdown());
                            Log.d("app", "--------LEVEL---------" + localUser.getLevel());
                            Log.d("app", "--------NUMBER---------: " + localUser.getNumber());
                            Log.d("app", "--------IS FIGHTING---------: " + localUser.getFighting());

                            attack.setText(Integer.toString(localUser.getAttack()));
                            defence.setText(Integer.toString(localUser.getDefence()));
                            gold.setText(Integer.toString(localUser.getGold()));
                            if (localLevel != null) {
                                progressXP = (100 * localUser.getExperience()) / localLevel.getXp();
                                progress.setProgress(progressXP);
                            }
                            if (firstTimeLoad) {
                                userLoaded();
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
    public void userLoaded() {
        if (localUser.getCountdown() == null) {
            localUser.setCountdown(1L);
        }
        if (localUser.getNumber() == null) {
            localUser.setNumber(1L);
        }
        firstTimeLoad = false;
        Long currentTime = new Date().getTime();
        Long number = localUser.getNumber();

        Log.d("app", "number " + number);

        if (localUser.getCountdown() != null && localUser.getFighting() && isFighting) {
            if (currentTime - localUser.getCountdown() >= number) {
                countdown(1000L,0L);
            }
            isFighting = currentTime - localUser.getCountdown() <= number;
            Log.d("app", "is this: " + (currentTime - localUser.getCountdown()) + " smaller than this: " + number);
        } else isFighting = false;

        Long elapsedTime = currentTime - localUser.getCountdown();

        Log.d("app", "elapsed time: " + (elapsedTime)/1000 + " isfighting: " + isFighting);
        if (!isFighting && !localUser.getFighting()) {
            myButton.setEnabled(true);
        } else  {
            myButton.setEnabled(false);
            countdown(number - elapsedTime, elapsedTime);
        }
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButton.setEnabled(false);
                isFighting = true;
                Long date = new Date().getTime();
                int number = randomNumberGenerator.nextInt(2100);
                final long countdownLong = number*1000L;
                Log.d("app", "<<<<<<<<<<<<<<NUMBER>>>>>>>>>> " + number + " || " + countdownLong);
                localUser.setCountdown(date);
                localUser.setNumber(countdownLong);
                localUser.setFighting(true);
                updateData();
                countdown(countdownLong, 0L);
            }
        });
    }

    public void countdown(final Long countdownLong, final Long elapsedLong) {
        countDownTimer = new CountDownTimer(countdownLong, 1000) {
            TextView countdown = (TextView) findViewById(R.id.countdown);

            public void onTick(long millisUntilFinished) {
                Long timer = elapsedLong + countdownLong - millisUntilFinished;
                countdown.setText("Fighting... " + (timer / 1000));
            }
            public void onFinish() {
                isFighting = false;
                myButton.setEnabled(true);
                Log.d("app", "mybutton enabled: " + myButton.isEnabled() + " isfighting: " + isFighting);
                countdown.setText("");
                fight();
            }
        }.start();
    }

    public void signOut() {
        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
    public void updateSeekbar(int progress) {
        localUser.setSeekbar(progress);
    }
    public void updateData() {
        database.child("users").child(localUser.getUserId()).setValue(localUser);
    }

    public void fight() {

        final NPC npc = new NPC(opponent);

        database = FirebaseDatabase.getInstance().getReference();

        database.child("npc").child(opponent).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NPC npc_data = dataSnapshot.getValue(NPC.class);
                        npc.setlevel(npc_data.getlevel());
                        npc.setattack(npc_data.getattack());
                        npc.setdefence(npc_data.getdefence());
                        npc.setXp(npc_data.getXp());
                        npc.setgold(npc_data.getgold());
                        Log.d("app", "NPC GOLD: " + npc_data.getgold());
                        calculateWinner(npc);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("app", "loadPost:onCancelled", databaseError.toException());
                    }
                }
        );

        }

    public void addExperience(boolean winner, int experience, int goldAmount) {
        if (winner) {
            localUser.addExperience(experience);
            localUser.addGold(goldAmount);
        } else {
            localUser.addExperience(experience/2);
            localUser.addGold(goldAmount/2);
        }
        Log.d("app", "YOU WON: " + winner);

        if (winner) {
            winner_text.setText("You WON Your last fight!");
        } else winner_text.setText("oh man! You lost Your last fight!");

        calculateLevel();
    }
    public void calculateLevel() {
        localLevel = new Level();
        Log.d("app", "ADDING LEVEL: " + Integer.toString(localUser.getLevel()));
        database.child("levels").child(Integer.toString(localUser.getLevel()+1)).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        Level level = dataSnapshot.getValue(Level.class);
                        Log.d("app", "ALMOST THERE: " + level);
                        if (level != null) {
                            localLevel.setLevel(level.getLevel());
                            localLevel.setXp(level.getXp());
                            Log.d("app", "LOCAL LEVEL: " + localLevel.getLevel());
                            Log.d("app", "LOCAL XP: " + localLevel.getXp());
                            if (localUser.getExperience() >= localLevel.getXp()) {
                                Log.d("app", "TEST: " + localUser.getExperience() + "|" + localLevel.getXp());
                                localUser.addLevel();
                            }
                            progressXP = (100*localUser.getExperience())/localLevel.getXp();
                            progress.setProgress(progressXP);

                            localUser.setFighting(false);
                            updateData();
                            isFighting = false;
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
    public void calculateWinner(NPC npc) {
        playerLife = 100;
        opponentLife = 100;
        int rewardXP = npc.getXp();
        int goldAmount = npc.getgold();
        opponentAttack = npc.getattack();
        Log.d("app", "GOLD: " + npc.getgold() + " || " + npc.getXp() + " || " + npc.getattack());
        opponentDefence = npc.getdefence();
        opponentLevel = npc.getlevel();
        playerAttack = localUser.getAttack();
        playerDefence = localUser.getDefence();
        playerLevel = localUser.getLevel();
        boolean winner = false;
        while (playerLife > 0 && opponentLife > 0) {
            if(playerTurn) {
                int dealtDamage = calculateHit(playerDefence, playerAttack, playerLevel);
                Log.d("app", "PLAYER stats: " + playerDefence + " || " + playerAttack + " || " + playerLevel);
                opponentLife = removeLife(opponentLife, dealtDamage);
                Log.d("app", "player damage: " + dealtDamage);
                Log.d("app", "NPC lifepoints: " + opponentLife);
                playerTurn = false;
                winner = true;
            } else {
                int dealtDamage = calculateHit(opponentDefence, opponentAttack, opponentLevel);
                Log.d("app", "NPC stats: " + opponentDefence + " || " + opponentAttack + " || " + opponentLevel);
                playerLife = removeLife(playerLife, dealtDamage);
                Log.d("app", "NPC damage: " + dealtDamage);
                Log.d("app", "player lifepoints: " + playerLife);
                playerTurn = true;
                winner = false;
            }
        }
        addExperience(winner, rewardXP, goldAmount);
    }

    public int calculateHit(int defence, int attack, int level) {
        double number = (double) randomNumberGenerator.nextInt(attack + 1);

        double defenceF = (double) defence;
        double attackF = (double) attack;
        double levelF = (double) level;

        double dealtDamage = ((100-defenceF+attack)/100)*(number+levelF);

        int dealtInt = (int) dealtDamage;

        return dealtInt;
    }

    public int removeLife(int lifepoints, int dealtDamage) {
        lifepoints -= dealtDamage;
        return lifepoints;
    }

}
