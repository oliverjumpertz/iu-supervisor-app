package com.example.supervisionapp.utils;

import com.example.supervisionapp.persistence.User;

public final class Utils {
    private Utils() {}

    public static String createSupervisorName(String title, String forename, String name) {
        if (title == null || title.isEmpty()) {
            return String.format("%s %s", forename, name);
        }
        return String.format("%s %s %s", title, forename, name);
    }

    public static String createSupervisorName(User user) {
        return createSupervisorName(user.title, user.foreName, user.name);
    }

    public static String createSupervisorName(com.example.supervisionapp.data.model.User user) {
        return createSupervisorName(user.getTitle(), user.getForename(), user.getName());
    }

    public static String createStudentName(String forename, String name) {
        return String.format("%s %s", forename, name);
    }

    public static String createStudentName(User user) {
        return createStudentName(user.foreName, user.name);
    }

    public static String createStudentName(com.example.supervisionapp.data.model.User user) {
        return createStudentName(user.getForename(), user.getName());
    }
}
