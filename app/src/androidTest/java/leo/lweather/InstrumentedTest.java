package leo.lweather;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }


    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("leo.lweather", appContext.getPackageName());
    }

    @Before
    public void initValidString() {
        mStringToBetyped = "NotACity";
    }

    @Test
    public void testButton() {
        onView(withId(R.id.cityBN))
                .perform(click());

        onView(withId(R.id.goButton))
                .check(matches(isDisplayed()));
    }


    @Test
    public void incorrectLocationInput() {
        // Select Input City tab
        onView(withId(R.id.cityBN))
                .perform(click());
        // Type text and then press the button.
        onView(withId(R.id.cityNameEdit))
                .perform(typeText(mStringToBetyped));

        onView(withId(R.id.goButton))
                .perform(click());

        // Check that the text was changed with Incorrect city input.
        onView(withId(R.id.cityNameEdit))
                .check(matches(withText(mActivity.getResources()
                        .getString(R.string.locationNotFound))));
    }
}
