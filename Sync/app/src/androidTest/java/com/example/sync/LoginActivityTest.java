package com.example.sync;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class LoginActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity>scenario = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    // Test if attendee login button redirects to the correct activity
    @Test
    public void testAttendeeLogin() {
        onView(withId(R.id.login_attendee_button)).perform(click());
        onView(withId(R.id.attendee_label)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    // Test if organizer login button redirects to the correct activity
    @Test
    public void testOrganizerLogin() {
        onView(withId(R.id.login_organizer_button)).perform(click());
        try {
            onView(withId(R.id.congratulation)).check(matches(isDisplayed()));
            onView(isRoot()).perform(pressBack());
        } catch (NoMatchingViewException e) {
            // no dialog popping out, do nothing
        }
        onView(withId(R.id.dashboard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

}
