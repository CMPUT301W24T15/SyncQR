package com.example.sync;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.scrollTo;
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
public class AttendeeDashboardTest {
    @Rule
    public ActivityScenarioRule<LoginActivity>scenario = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    // helper function to setup tests
    private void setupTest() {
        onView(withId(R.id.login_attendee_button)).perform(click());
    }
    // shouldn't change activity since this is home
    public void testHomeButton() {
        setupTest();
        onView(withId(R.id.home_button)).perform(click());
        onView(withId(R.id.attendee_label)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    // Test if we can open profile
    @Test
    public void testProfileButton() {
        setupTest();
        onView(withId(R.id.profile_button)).perform(click());
        onView(withId(R.id.profile_image_middle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    // Test if we can open event
    @Test
    public void testEventButton() {
        setupTest();
        onView(withId(R.id.event_button)).perform(click());
        onView(withId(R.id.sign_up_event_list)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    // Test if we can open message
    public void testMessageButton() {
        setupTest();
        onView(withId(R.id.messages_button)).perform(click());
        onView(withId(R.id.notificationsListView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

}
