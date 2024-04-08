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
public class OrganizerDashboardTest {
    @Rule
    public ActivityScenarioRule<LoginActivity>scenario = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    // helper function to setup tests
    private void setupTest() {
        onView(withId(R.id.login_organizer_button)).perform(click());
    }

    // Test if we can open event
    @Test
    public void testCreateEvent() {
        setupTest();
        onView(withId(R.id.create_new_event)).perform(click());
        onView(withId(R.id.fragment_container)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    // Test if we can open message
    @Test
    public void testViewEvent() {
        setupTest();
        onView(withId(R.id.view_my_events)).perform(click());
        onView(withId(R.id.fragment_container)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

}
