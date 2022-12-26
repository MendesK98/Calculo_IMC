package com.example.calculoimc.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Usuarios {

    public static List <User> users = new ArrayList<>();

    public static void addUser (User user) {
        users.add(user);
    }

}
