package com.quizzy.mrk.quizzy.Technique;

import com.quizzy.mrk.quizzy.Entities.User;

public class Session {

    private static Session session = null ;
    private User user = null;

    private Session (User user){
        this.user = user;
    }

    public static void ouvrir(User user){
        Session.session = new Session(user);
    }

    public static void fermer(){
        session = null;
    }

    public User getUser() {
        return this.user;
    }

    public static Session getSession() {
        return session;
    }
}

