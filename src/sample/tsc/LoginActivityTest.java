package sample.tsc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import sample.tsc.LoginActivity;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
	private LoginActivity act;
	private String username = "kagerot";
	private String password = "1qaz2wsx";
	private EditText editTextUsername;
	private EditText editTextPassword;

	public LoginActivityTest() {
		super("sample.tsc", LoginActivity.class);
	}

	@Override
    protected void setUp() throws Exception {
		super.setUp();
		act = getActivity();
		editTextUsername = (EditText)act.findViewById(R.id.username);
		editTextPassword = (EditText)act.findViewById(R.id.password);
	}

	public void test_inform() throws Exception {
		act.inform(R.string.toast_login_ng);
		act.inform(R.string.toast_login_ok);
	}

	public void test_save() throws Exception {
		assertTrue(act.save());
		SharedPreferences settings = act.getSharedPreferences(Const.PREFS_NAME, Activity.MODE_PRIVATE);
		assertEquals(username, Utils.decode(settings.getString(Const.PREFS_USERNAME, "")));
		assertEquals(password, Utils.decode(settings.getString(Const.PREFS_PASSWORD, "")));
	}

	public void test_load() throws Exception {
		act.load();
	}

	public void test_login() throws Exception {
		act.runOnUiThread(new Runnable() {
			public void run() {
				editTextUsername.setText(username);
				editTextPassword.setText(password);
			}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue(act.login());
	}

	public void test_utils_encode() throws Exception {
		assertEquals("aG9nZQ==", Utils.encode("hoge"));
	}

	public void test_utils_decode() throws Exception {
		assertEquals("hoge", Utils.decode("aG9nZQ=="));
	}
}
