package com.example.kurmo.gamenite;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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

    Long lastFight;

    boolean isFighting = false;
    boolean firstTimeLoad = false;
    private CountDownTimer countDownTimer;

    private FirebaseAuth auth;

    private User localUser;
    private TextView attack;
    private TextView defence;
    private TextView gold;
    private TextView experience;
    private TextView level;
    private ProgressBar progress;
    private Button myButton, signOut;

    private DatabaseReference database;
    private FirebaseAuth.AuthStateListener authListener;
    SeekBar.OnSeekBarChangeListener customSeekBarListener;
    private final CountDownLatch loginLatch = new CountDownLatch (1);
    private boolean callbackResults;

    private ValueEventListener postListener;

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
        signOut = (Button) findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        gold = (TextView) findViewById(R.id.gold);
        attack = (TextView) findViewById(R.id.attack);
        defence = (TextView) findViewById(R.id.defence);
        database = FirebaseDatabase.getInstance().getReference();
        level = (TextView) findViewById(R.id.level);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        myButton = (Button) findViewById(R.id.rollButton);
        Log.i("app", "tereee");
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
    public void loadUserData(){
        database.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        Log.i("app", "----------------- sss" + auth.getCurrentUser().getUid());
                        User user = dataSnapshot.getValue(User.class);
//                        localUser.setUserID(auth.getCurrentUser().getUid());
                        Log.i("app", "----------------- TERE");
                        if (user != null) {
                            localUser.setExperience(user.getExperience());
                            localUser.setLevel(user.getLevel());
                            localUser.setSeekbar(user.getSeekbar());
                            localUser.setGold(user.getGold());
                            localUser.setCountdown(user.getCountdown());
                            localUser.setNumber(user.getNumber());
                            localUser.setAttack(user.getAttack());
                            localUser.setDefence(user.getDefence());
                            updateData();
                            Log.d("app", "userloaded before: " + localUser.isLoaded);
                            Log.d("app", "-----------------" + localUser.getExperience());
                            Log.d("app", "-----------------" + localUser.getGold());
                            Log.d("app", "----sss----" + localUser.getCountdown());
                            Log.d("app", "xp: " + localUser.getExperience());
                            Log.d("app", "---------number-------: " + localUser.getNumber());

                            attack.setText(Integer.toString(localUser.getExperience()));
                            defence.setText(Integer.toString(localUser.getLevel()));
                            gold.setText(Integer.toString(localUser.getGold()));

                            Log.d("app", "GG");
                            progressXP = (100*localUser.getExperience())/1000;
                            progress.setProgress(progressXP);
                            if (firstTimeLoad) {
                                Log.d("app", "FIRST TIME LOAD: " + firstTimeLoad);
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
        firstTimeLoad = false;
        Log.d("app", "userloaded after: " + localUser.isLoaded);
        Long currentTime = new Date().getTime();
        Long number = localUser.getNumber();

        Log.d("app", "number " + number);

        if (currentTime - localUser.getCountdown() <= number) isFighting = true;
        else isFighting = false;
        Long elapsedTime = currentTime - localUser.getCountdown();

        Log.d("app", "elapsed time: " + (elapsedTime)/1000 + " isfighting: " + isFighting);
        if (!isFighting) {
            myButton.setEnabled(true);

        } else  {
            myButton.setEnabled(false);

            countdown(number - elapsedTime, elapsedTime);
        }
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButton.setEnabled(false);
                Log.d("app", "tere");

                isFighting = true;
                Long date = new Date().getTime();
                int number = randomNumberGenerator.nextInt(2100);
                final long countdownLong = number*1000L;
                Log.d("app", "<<<<<<<<<<<<<<number>>>>>>>>>> " + number + " || " + countdownLong);
                localUser.setCountdown(date);
                localUser.setNumber(countdownLong);
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

        final NPC npc = new NPC("Wolf");

        database = FirebaseDatabase.getInstance().getReference();

        database.child("npc").child("Wolf").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NPC npc_data = dataSnapshot.getValue(NPC.class);
                        npc.setlevel(npc_data.getlevel());
                        npc.setattack(npc_data.getattack());
                        npc.setdefence(npc_data.getdefence());
                        Log.d("app", "NPC ATTACK: " + npc.getattack());
                        calculateWinner(npc);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("app", "loadPost:onCancelled", databaseError.toException());
                    }
                }
        );

        }

    public void addExperience(boolean winner, int experience) {
        if (winner) {
            localUser.addExperience(experience);
        } else {
            localUser.addExperience(experience/2);
        }
        progressXP = (100*localUser.getExperience())/1000;
        progress.setProgress(progressXP);
        calculateLevel();
    }
    public void addGold(boolean winner, NPC npc) {
        if (winner) {
            localUser.addGold(npc.getgold());

        } else {
            localUser.addGold(npc.getgold()/2);
        }
    }
    public void calculateLevel() {
        if (localUser.getExperience() > 1000) {
            localUser.addLevel();
            localUser.addExperience(-1000);
        }
    }
    public void calculateWinner(NPC npc) {
        playerLife = 100;
        opponentLife = 100;
        opponentAttack = npc.getattack();
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
        addExperience(winner, 750);
        addGold(winner, npc);
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
