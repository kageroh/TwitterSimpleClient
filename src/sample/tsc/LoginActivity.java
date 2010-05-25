package sample.tsc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class LoginActivity extends Activity {
	private String username;
	private String password;
	private EditText editTextUsername;
	private EditText editTextPassword;
	private Button buttonLogin;
	private boolean logged = false;
	private final String url = "http://twitter.com/account/verify_credentials.json";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		load();

		editTextUsername = (EditText)findViewById(R.id.username);
		editTextUsername.setText(username);

		editTextPassword = (EditText)findViewById(R.id.password);
		editTextPassword.setText(password);

		buttonLogin = (Button)findViewById(R.id.login_button);
		buttonLogin.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				try {
					login();
					if (logged) {
			            Intent intent = new Intent();
			            intent.setClass(LoginActivity.this, TimelineActivity.class);
			            startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		save();
	}

	protected void inform(int resId) {
		Toast.makeText(LoginActivity.this, resId, Toast.LENGTH_SHORT).show();
	}

	protected boolean save() {
		username = editTextUsername.getText().toString();
		password = editTextPassword.getText().toString();
		SharedPreferences settings = getSharedPreferences(Const.PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Const.PREFS_USERNAME, Utils.encode(username));
		editor.putString(Const.PREFS_PASSWORD, Utils.encode(password));
		return editor.commit();
	}

	protected void load() {
		SharedPreferences settings = getSharedPreferences(Const.PREFS_NAME, MODE_PRIVATE);
		username = Utils.decode(settings.getString(Const.PREFS_USERNAME, ""));
		password = Utils.decode(settings.getString(Const.PREFS_PASSWORD, ""));
	}

	protected boolean login() {
		username = editTextUsername.getText().toString();
		password = editTextPassword.getText().toString();
		JSONObject json = new JSONHttpRequest().loadObject("GET", url, username, password);
		logged = (json != null);
		inform(logged ? R.string.toast_login_ok : R.string.toast_login_ng);
		return logged;
	}
}
