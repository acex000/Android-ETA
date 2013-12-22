package com.example.eta;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class PostToServerTask extends AsyncTask<String, Void, String> {
	
	
	private static final String TAG = "PostToServerTask";
//	private String briefResult;

	public PostToServerTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... urls) {

		// params comes from the execute() call: params[0] is the url.
		try {
			return downloadUrl(urls[0], urls[1]);
		} catch (IOException e) {
			return "Unable to retrieve web page. URL may be invalid.";
		}
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		String briefResult;
		//print out the server response in log
		int index;
		index=result.indexOf("}");
		briefResult=result.substring(0, index+1);
		result = briefResult;
		Log.i(TAG, "server response: " + result);
		
	}

	// }

	private String downloadUrl(String myurl, String jsonStr) throws IOException {
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.
		int len = 500;

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			String urlParameters = jsonStr;

			// Send post request
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			Log.d(TAG, "The response is: " + response);
			is = conn.getInputStream();

			// Convert the InputStream into a string
			String contentAsString = readIt(is, len);
			return contentAsString;

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	// Reads an InputStream and converts it to a String.
	public String readIt(InputStream stream, int len) throws IOException,
			UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

//	public String getResult() {
//		return result;
//	}

}
