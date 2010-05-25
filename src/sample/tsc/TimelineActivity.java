package sample.tsc;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.Toast;

public class TimelineActivity extends Activity {
	private String username;
	private String password;
	private ScrollView sv;
	private WebView wv;
	private JSONArray json;
	private String since_id;
	private final int timeOffset = 9;
	private final SimpleDateFormat dateParser =
		new SimpleDateFormat("EEE MMM dd HH:mm:ss +0000 yyyy", Locale.ENGLISH);
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
	private final String default_interval = "30";
	private final String url_tl = "http://twitter.com/statuses/friends_timeline.json";
	private final String url_fav = "http://twitter.com/favorites/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		load();

		sv = (ScrollView)findViewById(R.id.sv_timeline);
		wv = (WebView)findViewById(R.id.wv_timeline);
		wv.addJavascriptInterface(new JsInterface(this), "android");
		wv.getSettings().setJavaScriptEnabled(true);
		wv.loadDataWithBaseURL(
			"file:///android_asset/",
			"<!DOCTYPE html>" +
			"<meta charset='utf-8' />" +
			"<link rel='stylesheet' href='timeline.css' />" +
			"<script src='jquery-1.4.2.min.js'></script>" +
			"<script src='timeline.js'></script>" +
			"<div class='loader'><img src='loader.gif' /></div>",
			"text/html", "utf-8", null);
	}

	@Override
	public void onPause() {
		super.onPause();
		Utils.postMessage(wv, "clearInterval", null);
	}

	@Override
	public void onResume() {
		super.onResume();
		setInterval();
	}

	private void setInterval() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String interval = settings.getString(Const.PREFS_INTERVAL, default_interval);
		Utils.postMessage(wv, "setInterval", interval);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timeline, menu);

		MenuItem mi;
		mi = menu.findItem(R.id.menu_top);
		mi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				scrollTo(0, 0);
				return false;
			}
		});
		mi = menu.findItem(R.id.menu_prefs);
		mi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
	            Intent intent = new Intent();
	            intent.setClass(TimelineActivity.this, PrefsActivity.class);
	            startActivity(intent);
				return false;
			}
		});

		return true;
	}

	protected void scrollTo(int x, int y) {
		sv.scrollTo(x, y);
	}

	protected void inform(int resId) {
		Toast.makeText(TimelineActivity.this, resId, Toast.LENGTH_SHORT).show();
	}

	protected void load() {
		SharedPreferences settings = getSharedPreferences(Const.PREFS_NAME, MODE_PRIVATE);
		username = Utils.decode(settings.getString(Const.PREFS_USERNAME, ""));
		password = Utils.decode(settings.getString(Const.PREFS_PASSWORD, ""));
	}

	protected void get() throws Exception {
		String url = url_tl;
		if (since_id != null) {
			url += "?since_id=" + since_id;
		}
		json = new JSONHttpRequest().loadArray("GET", url, username, password);
		inform(json != null ? R.string.toast_get_ok : R.string.toast_login_ng);
	}

	protected String createHTML() throws Exception {
		String string = "";
		for (int i = 0; i < json.length(); i++) {
			JSONObject stats = json.getJSONObject(i);
			JSONObject user = (JSONObject)stats.get("user");
			String id = stats.getString("id");
			if (i == 0) {
				since_id = id;
			}
			string +=
					"<div class='section" +
					(stats.getBoolean("favorited") ? " favorited" : "") +
					"' title='" + id + "'>" +
					"  <img src='" + user.getString("profile_image_url") + "' />" +
					"  <div class='header'>" +
					"    <span class='screen_name'>" + user.getString("screen_name") + "</span>" +
					"    <span class='created_at'>" + parseDate(stats.getString("created_at")) + "</span>" +
					"    <span class='source'>via " + stats.getString("source") + "</span>" +
					"  </div>" +
					"  <p>" + stats.getString("text") + "</p>" +
					"</div>";
		}
		return "<div class='article'>" + string + "</div>";
	}

	protected String parseDate(String string) {
		try {
			Date date = dateParser.parse(string, new ParsePosition(0));
			int hour = date.getHours() + timeOffset;
			date.setHours(hour < 24 ? hour : hour - 24);
			return dateFormatter.format(date);
		} catch (Exception e) {
			return string;
		}
	}

	protected void favorite(String id) {
		String url = url_fav + "create/" + id + ".json";
		JSONObject json = new JSONHttpRequest().loadObject("POST", url, username, password);
		if (json != null) {
			Utils.postMessage(wv, "favorite", id);
			inform(R.string.toast_favorite_ok);
		} else {
			inform(R.string.toast_favorite_ng);
		}
	}

	protected void destroy(String id) {
		String url = url_fav + "destroy/" + id + ".json";
		JSONObject json = new JSONHttpRequest().loadObject("POST", url, username, password);
		if (json != null) {
			Utils.postMessage(wv, "destroy", id);
			inform(R.string.toast_destroy_ok);
		} else {
			inform(R.string.toast_destroy_ng);
		}
	}

	class JsInterface {
		private TimelineActivity that;

		public JsInterface(Activity context) {
			that = (TimelineActivity)context;
		}

		public void print(String string) {
			Log.d("TSC", string);
		}

		public void update() {
			try {
				get();
				if (json != null) {
					Utils.postMessage(wv, "insert", createHTML());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void getInterval() {
			setInterval();
		}

		public void dialogize(final String id, String favorited) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(that); 
			if (favorited.equals("true")) {
				dialog.
				setTitle(R.string.dialog_title_destroy).
				setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						destroy(id);
					}
				});
			} else {
				dialog.
				setTitle(R.string.dialog_title_favorite).
				setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						favorite(id);
					}
				});
			}
			dialog.
			setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			}).
			show();
		}
	}
}
