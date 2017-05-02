package me.tombailey.store;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SearchActivityTest {

    @Rule
    public ActivityTestRule<SearchActivity> mActivityRule =
            new ActivityTestRule<SearchActivity>(SearchActivity.class, true, false);

    @Before
    public void setup() {
        Intent intent = new Intent();
        intent.putExtra(SearchActivity.KEYWORDS, "hello world");
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void whenLoading_shouldShowProgressView() {
        onView(withId(R.id.search_activity_linear_layout_progress))
                .check(matches(isDisplayed()));
    }

}
