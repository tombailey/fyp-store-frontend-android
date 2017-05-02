package me.tombailey.store;

import android.support.test.espresso.Espresso;
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

@RunWith(AndroidJUnit4.class)
public class ProxyStatusActivityTest {

    @Rule
    public ActivityTestRule<ProxyStatusActivity> mActivityRule =
            new ActivityTestRule<ProxyStatusActivity>(ProxyStatusActivity.class);

    @Test
    public void whenLoading_shouldShowProgressView() {
        onView(allOf(withId(R.id.activity_proxy_status_progress_bar), isDisplayed()))
                .check(matches(isDisplayed()));
    }

    @Test
    public void whenLoaded_shouldShowStatus() {
        ProxyStatusIdlingResource proxyStatusIdlingResource = new ProxyStatusIdlingResource();
        Espresso.registerIdlingResources(proxyStatusIdlingResource);

        onView(withId(R.id.activity_proxy_status_text_view_status))
                .check(matches(isDisplayed()));

        Espresso.unregisterIdlingResources(proxyStatusIdlingResource);
    }

}
