package com.acme.fixtures;

import com.acme.core.User;

public class UserFixtures {
    public static User getUser() {
        return new User(10, "John");
    }
}
