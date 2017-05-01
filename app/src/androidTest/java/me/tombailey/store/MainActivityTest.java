package me.tombailey.store;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void whenLoading_shouldShowProgressView() {
        onView(allOf(withId(R.id.featured_app_list_fragment_linear_layout_progress), isDisplayed()))
                .check(matches(isDisplayed()));
    }

    @Test
    public void whenLoaded_shouldShowRecyclerView() {
        onView(allOf(withId(R.id.featured_app_list_fragment_recycler_view), isDisplayed()))
                .check(matches(isDisplayed()));
    }

}
