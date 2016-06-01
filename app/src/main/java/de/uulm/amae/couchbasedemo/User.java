package de.uulm.amae.couchbasedemo;

import java.security.Timestamp;

/**
 * Created by Fabian Widmann on 01.06.2016.
 */
public class User{
    String email, password;
    Timestamp created;

    public User(String email, String password){
        this.email=email;
        this.password=password;
     }

    public User(String email, String password, Timestamp created){
        this.email=email;
        this.password=password;
        this.created=created;
    }

    @Override
    public String toString() {
        return "User[email="+email+"; passwd="+password+"];";
    }
}
