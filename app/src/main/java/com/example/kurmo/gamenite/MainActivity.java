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
    int playerDefence = 5;
    int playerAttack = 5;
    int progressXP = 0;

    private FirebaseAuth auth;


    private TextView experience;
    private TextView level;
    private ProgressBar progress;
    private Button myButton, signOut;

    private DatabaseReference database;
    private FirebaseAuth.AuthStateListener authListener;

    private ValueEventListener postListener;

    boolean playerTurn = true;

    int opponentLevel = 3;
    int opponentLife = 100;
    int opponentDefence = 8;
    int opponentAttack = 7;


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


        database = FirebaseDatabase.getInstance().getReference();

        database.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        User user = dataSnapshot.getValue(User.class);
                        playerExperience = user.getExperience();
                        Log.d("app", "xp" + user.getExperience());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("app", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                }
        );

        experience = (TextView) findViewById(R.id.points);
        level = (TextView) findViewById(R.id.level);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        level.setText("Level: " + playerLevel);
        experience.setText("Experience: " + playerExperience);


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

    private void getUser() {

    }


    public void fight() {
        int number = randomNumberGenerator.nextInt(46);
        final long countdownLong = number*1000L;

        new CountDownTimer(countdownLong, 1000) {
            TextView countdown = (TextView) findViewById(R.id.countdown);

            public void onTick(long millisUntilFinished) {
                countdown.setText(countdownLong + " Fighting... " + ((countdownLong - millisUntilFinished) / 1000));
            }
            public void onFinish() {
                countdown.setText("");
                calculateWinner();
                update();
                myButton.setEnabled(true);
            }
        }.start();
    }

    public void update() {
        level.setText("Level: " + playerLevel);
        experience.setText("Experience: " + playerExperience);
        progressXP = (100*playerExperience)/1000;
        progress.setProgress(progressXP);
    }

    public void addExperience(boolean winner, int experience) {
        if (winner) {
            playerExperience += experience;
        } else {
            playerExperience += experience/2;
        }
        calculateLevel();
    }

    public void calculateLevel() {
        if (playerExperience > 1000) {
            addLevel();
            playerExperience -= 1000;
        }
    }

    public void addLevel() {
        playerLevel++;
    }

    public void calculateWinner() {
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
