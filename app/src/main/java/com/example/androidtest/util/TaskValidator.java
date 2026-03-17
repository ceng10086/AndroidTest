package com.example.androidtest.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class TaskValidator {

    public static final int TITLE_MIN_LENGTH = 1;
    public static final int TITLE_MAX_LENGTH = 60;
    public static final int NOTE_MAX_LENGTH = 500;

    private TaskValidator() {
    }

    @NonNull
    public static String normalizeTitle(@Nullable String title) {
        return title == null ? "" : title.trim();
    }

    @NonNull
    public static String normalizeNote(@Nullable String note) {
        return note == null ? "" : note.trim();
    }

    public static boolean isValidTitle(@Nullable String title) {
        String t = normalizeTitle(title);
        return t.length() >= TITLE_MIN_LENGTH && t.length() <= TITLE_MAX_LENGTH;
    }

    public static boolean isValidNote(@Nullable String note) {
        String n = normalizeNote(note);
        return n.length() <= NOTE_MAX_LENGTH;
    }

    public static int clampPriority(int priority) {
        if (priority < 0) {
            return 0;
        }
        if (priority > 2) {
            return 2;
        }
        return priority;
    }
}
