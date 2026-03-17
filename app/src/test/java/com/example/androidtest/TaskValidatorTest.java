package com.example.androidtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.androidtest.util.TaskValidator;

import org.junit.Test;

public class TaskValidatorTest {

    @Test
    public void titleValidation() {
        assertFalse(TaskValidator.isValidTitle(null));
        assertFalse(TaskValidator.isValidTitle(""));
        assertTrue(TaskValidator.isValidTitle("a"));
        assertTrue(TaskValidator.isValidTitle("  abc  "));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 61; i++) {
            sb.append('a');
        }
        assertFalse(TaskValidator.isValidTitle(sb.toString()));
    }

    @Test
    public void noteValidation() {
        assertTrue(TaskValidator.isValidNote(null));
        assertTrue(TaskValidator.isValidNote(""));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append('a');
        }
        assertTrue(TaskValidator.isValidNote(sb.toString()));

        sb.append('b');
        assertFalse(TaskValidator.isValidNote(sb.toString()));
    }

    @Test
    public void priorityClamp() {
        assertEquals(0, TaskValidator.clampPriority(-1));
        assertEquals(0, TaskValidator.clampPriority(0));
        assertEquals(1, TaskValidator.clampPriority(1));
        assertEquals(2, TaskValidator.clampPriority(2));
        assertEquals(2, TaskValidator.clampPriority(3));
    }
}
