package com.example.kurmo.gamenite;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User where first_name LIKE  :firstName AND last_name LIKE :lastName")
    User findByName(String firstName, String lastName);

    @Query("SELECT COUNT(*) from User")
    int countUsers();

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}