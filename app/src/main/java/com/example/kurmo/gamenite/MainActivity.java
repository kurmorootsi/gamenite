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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity {

    int playerGold = 0;
    int playerLevel = 1;
    int playerExperience;
    int playerLife = 100;
    int playerDefence = 1;
    int playerAttack = 1;
    int progressXP = 0;

    private FirebaseAuth auth;

    private User localUser;
    private TextView experience;
    private TextView level;
    private ProgressBar progress;
    private Button myButton, signOut;

    private DatabaseReference database;
    private FirebaseAuth.AuthStateListener authListener;
    SeekBar.OnSeekBarChangeListener customSeekBarListener;

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

        signOut = (Button) findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        experience = (TextView) findViewById(R.id.points);
        database = FirebaseDatabase.getInstance().getReference();

        database.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        User user = dataSnapshot.getValue(User.class);
//                        localUser.setUserID(auth.getCurrentUser().getUid());
                        if (user != null) {
                            localUser.setexperience(user.getexperience());
                            localUser.setlevel(user.getlevel());
                            localUser.setseekbar(user.getseekbar());
                            localUser.setgold(user.getgold());
                        }

                        Log.d("app", "xp: " + localUser.getexperience());

                        experience.setText("Experience: " + localUser.getexperience());
                        level.setText("Level: " + localUser.getlevel());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("app", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                }
        );
        customSeekBarListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        updateSeekbar(progress);
                        Log.d("app", "seekbar: " + localUser.getseekbar());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                };
        level = (TextView) findViewById(R.id.level);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        myButton = (Button) findViewById(R.id.rollButton);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButton.setEnabled(false);
                fight();
            }
        });
    }

    public void signOut() {
        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
    public void updateSeekbar(int progress) {
        localUser.setseekbar(progress);
    }
    public void fight() {
        int number = randomNumberGenerator.nextInt(46);
        final long countdownLong = number*1000L;

        final NPC npc = new NPC("Wolf");

        database = FirebaseDatabase.getInstance().getReference();

        database.child("npc").child("Wolf").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NPC npc_data = dataSnapshot.getValue(NPC.class);
                        npc.setlevel(npc_data.getlevel());
                        npc.setAttack(npc_data.getAttack());
                        npc.setDefence(npc_data.getDefence());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("app", "loadPost:onCancelled", databaseError.toException());
                    }
                }
        );

        new CountDownTimer(countdownLong, 1000) {
            TextView countdown = (TextView) findViewById(R.id.countdown);

            public void onTick(long millisUntilFinished) {
                countdown.setText(countdownLong/1000 + " Fighting... " + ((countdownLong - millisUntilFinished) / 1000));
            }
            public void onFinish() {
                countdown.setText("");
                calculateWinner(npc);
                myButton.setEnabled(true);
            }
        }.start();
    }

    public void addExperience(boolean winner, int experience) {
        if (winner) {
            localUser.addexperience(experience);
        } else {
            localUser.addexperience(experience/2);
        }
        progressXP = (100*localUser.getexperience())/1000;
        progress.setProgress(progressXP);
        calculateLevel();
    }
    public void addGold(boolean winner, NPC npc) {
        if (winner) {
            localUser.addgold(npc.getgold());

        } else {
            localUser.addgold(npc.getgold()/2);
        }
    }
    public void calculateLevel() {
        if (localUser.getexperience() > 1000) {
            localUser.addlevel();
            localUser.addexperience(-1000);
        }
    }
    public void calculateWinner(NPC npc) {
        playerLife = 100;
        opponentLife = 100;
        opponentAttack = npc.getAttack();
        opponentDefence = npc.getDefence();
        opponentLevel = npc.getlevel();
        boolean winner = false;
        while (playerLife > 0 && opponentLife > 0) {
            if(playerTurn) {
                int dealtDamage = calculateHit(playerDefence, playerAttack, playerLevel);
                opponentLife = removeLife(opponentLife, dealtDamage);
                Log.d("app", "player damage: " + dealtDamage);
                Log.d("app", "NPC lifepoints: " + opponentLife);
                playerTurn = false;
                winner = true;
            } else {
                int dealtDamage = calculateHit(opponentDefence, opponentAttack, opponentLevel);
                playerLife = removeLife(playerLife, dealtDamage);
                Log.d("app", "NPC damage: " + dealtDamage);
                Log.d("app", "player lifepoints: " + playerLife);
                playerTurn = true;
                winner = false;
            }
        }
        addExperience(winner, 150);
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
