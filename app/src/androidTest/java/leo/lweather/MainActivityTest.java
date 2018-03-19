package leo.lweather;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Leo on 15-Mar-18.
 */
public class MainActivityTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }


    @Test
    public void testLaunchTextViewCityName() {
        View view = mActivity.findViewById(R.id.cityName);

        assertNotNull(view);
    }

    @Test
    public void testLaunchEditTextView() {
        View view = mActivity.findViewById(R.id.cityNameEdit);

        assertNotNull(view);
    }

    @Test
    public void testLaunchTextViewDescription() {
        View view = mActivity.findViewById(R.id.description);

        assertNotNull(view);
    }

    @Test
    public void testLaunchTextViewTemperature() {
        View view = mActivity.findViewById(R.id.temperature);

        assertNotNull(view);
    }

    @Test
    public void testLaunchTextViewWind() {
        View view = mActivity.findViewById(R.id.wind);

        assertNotNull(view);
    }


    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}