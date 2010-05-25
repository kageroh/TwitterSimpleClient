package sample.tsc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;

public class JSONHttpRequest {
	private int timeout = 15 * 1000;

	private static Proxy getProxy() {
	    String host = android.net.Proxy.getDefaultHost();
	    int port = android.net.Proxy.getDefaultPort();
	    if ((host != null) && (port != -1)) {
	        SocketAddress addr = new InetSocketAddress(host, port);
	        return new Proxy(Proxy.Type.HTTP, addr);
	    } else {
	        return null;
	    }
	}

	public JSONObject loadObject(String method, String url, String username, String password) {
		String json = load(method, url, username, password);
		try {
			return new JSONObject(json == null ? "{}" : json);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONArray loadArray(String method, String url, String username, String password) {
		String json = load(method, url, username, password);
		try {
			return new JSONArray(json == null ? "[{}]" : json);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String load(String method, String path, String username, String password) {
		try {
	        Authenticator.setDefault(new HttpAuthenticator(username, password));

	        URL url = new URL(path);
			Proxy proxy = getProxy();
			HttpURLConnection urlconn = proxy != null ?
	        		(HttpURLConnection)url.openConnection(proxy) :
	        			(HttpURLConnection)url.openConnection();

	        urlconn.setConnectTimeout(timeout);
	        urlconn.setReadTimeout(timeout);
			urlconn.setRequestMethod(method);

			urlconn.connect();

			String string = "";
	        BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
	        while (true) {
	            String line = reader.readLine();
	            if (line == null) { break; }
	            string += line;
	        }

	        reader.close();
	        urlconn.disconnect();
	        return string;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

class HttpAuthenticator extends Authenticator {
	private String username;
	private String password;

	public HttpAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}

	public String myGetRequestingPrompt() {
        return super.getRequestingPrompt();
    }
}
