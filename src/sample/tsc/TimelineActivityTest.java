package sample.tsc;

import android.test.ActivityInstrumentationTestCase2;
import sample.tsc.TimelineActivity;

public class TimelineActivityTest extends ActivityInstrumentationTestCase2<TimelineActivity> {
	private TimelineActivity act;

	public TimelineActivityTest() {
		super("sample.tsc", TimelineActivity.class);
	}

	@Override
    protected void setUp() throws Exception {
		super.setUp();
		act = getActivity();
	}

	public void test_scrollTo() throws Exception {
		act.scrollTo(0, 0);
	}

	public void test_inform() throws Exception {
		act.inform(R.string.toast_get_ok);
	}

	public void test_load() throws Exception {
		act.load();
	}

	public void test_get() throws Exception {
		act.runOnUiThread(new Runnable() {
			public void run() {
				try {
					act.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		getInstrumentation().waitForIdleSync();
	}

	public void test_createHTML() throws Exception {
		act.get();
		assertNotNull(act.createHTML());
	}

	public void test_parseDate() throws Exception {
		assertEquals("08:25", act.parseDate("Thu May 13 23:25:40 +0000 2010"));
	}

	public void test_favorite() throws Exception {
		act.favorite("");
	}

	public void test_destroy() throws Exception {
		act.destroy("");
	}
}
