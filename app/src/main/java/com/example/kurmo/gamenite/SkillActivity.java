package com.example.kurmo.gamenite;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class SkillActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private User localUser;
    private Button back_button, log;
    private TextView wc_log, wcountdown;
    boolean firstTimeLoad = false;
    boolean isFighting = false;
    private CountDownTimer countDownTimer;
    private DatabaseReference database;
    private FirebaseAuth.AuthStateListener authListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstTimeLoad = true;
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
                    startActivity(new Intent(SkillActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        log = (Button) findViewById(R.id.log);
        back_button = (Button) findViewById(R.id.fight);
        wc_log= (TextView) findViewById(R.id.logs);
        wcountdown= (TextView) findViewById(R.id.wcountdown);

        database.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("app", "--------UID---------" + auth.getCurrentUser().getUid());
                        User user = dataSnapshot.getValue(User.class);
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
                            localUser.setWclog(user.getWclog());
                            localUser.setWclevel(user.getWclevel());
                            Log.d("app", "--------GOLD---------" + localUser.getGold());
                            Log.d("app", "--------LEVEL---------" + localUser.getLevel());
                        }
                        if (firstTimeLoad) {
                            userLoaded();
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
        int last_opp = localUser.getLast_opp();

        Log.d("app", "number " + number);

        if (localUser.getCountdown() != null && localUser.getFighting() && isFighting) {
            if (currentTime - localUser.getCountdown() >= number) {
                countdown(1000L,0L, last_opp);
            }
            isFighting = currentTime - localUser.getCountdown() <= number;
            Log.d("app", "is this: " + (currentTime - localUser.getCountdown()) + " smaller than this: " + number);
        } else isFighting = false;

        Long elapsedTime = currentTime - localUser.getCountdown();

        Log.d("app", "elapsed time: " + (elapsedTime)/1000 + " isfighting: " + isFighting);
        if (isFighting && localUser.getFighting()) {
            countdown(number - elapsedTime, elapsedTime, last_opp);
        }
    }
    public void countdown(final Long countdownLong, final Long elapsedLong, final int opp_id) {
        countDownTimer = new CountDownTimer(countdownLong, 1000) {
            TextView countdown = (TextView) findViewById(R.id.countdown);

            public void onTick(long millisUntilFinished) {
                Long timer = millisUntilFinished;
                countdown.setText("Cutting... " + (timer / 1000));
            }
            public void onFinish() {
                isFighting = false;
                countdown.setText("");
                cut(opp_id);
            }
        }.start();
    }
    public void chooseLog(View v) {
        int wc_type = v.getId();
        isFighting = true;
        Long date = new Date().getTime();
        final long countdownLong = 300*1000L;

        localUser.setCountdown(date);
        localUser.setNumber(countdownLong);
        localUser.setFighting(true);
        localUser.setLast_opp(wc_type);
        updateData();
        countdown(countdownLong, 0L, wc_type);
    }
    public void cut(int opp_id) {

        final Tree log = new Tree(opp_id);

        database = FirebaseDatabase.getInstance().getReference();

        database.child("log").child(Integer.toString(log.getId())).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Tree log_data = dataSnapshot.getValue(Tree.class);
                        if (log_data != null) {
                            log.setlevel(log_data.getlevel());
                            log.setXp(log_data.getXp());
                            log.setgold(log_data.getgold());
                            Log.d("app", "LOG GOLD: " + log_data.getgold());
                            finishCutting(log);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("app", "loadPost:onCancelled", databaseError.toException());
                    }
                }
        );
    }
    public void finishCutting(Tree log) {
        Log.d("app","DONE");
    }
    public void updateData() {
        database.child("users").child(localUser.getUserId()).setValue(localUser);
    }
}
