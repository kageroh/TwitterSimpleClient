package sample.tsc;

import java.net.URLEncoder;

import android.webkit.WebView;

import sample.tsc.util.Base64;


public class Utils {
	public static String encode(String string) {
		return new String(Base64.encodeBase64(string.getBytes()));
	}

	public static String decode(String string) {
		return new String(Base64.decodeBase64(string));
	}

	public static String encodeURIComponent(String string) {
		return URLEncoder.encode(string).replaceAll("\\+", "%20");
	}

	public static void postMessage(WebView wv, String fn, String msg) {
		wv.loadUrl("javascript:void(onmessage('" + fn + "','" + 
				(msg == null ? "" : encodeURIComponent(msg)) + "'))");
	}
}