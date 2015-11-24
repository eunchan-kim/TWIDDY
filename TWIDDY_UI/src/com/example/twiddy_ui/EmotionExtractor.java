package com.example.twiddy_ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmotionExtractor {
	public static int getEmotion(String msg) {

		String URL = "http://143.248.142.86:4000/jsonrpc";
		HashMap<String,Object> params = new HashMap<String, Object>();
		params.put("jsonrpc", "2.0");
		params.put("method", "get_emotion");
		params.put("params", new ArrayList<String>(Arrays.asList(makeUnicode(msg))));
		params.put("id", new Integer(0));
		String response;
		try {
//			response = makeRequest(URL, params);
			response = "{\"jsonrpc\": \"2.0\", \"result\": 31, \"id\": 0}";
			return getScore(response);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private static String makeUnicode(String msg) {
		String res = "";
		for (int i = 0; i < msg.length(); i++) {
			res = res + String.format("%04X ", msg.codePointAt(i));
		}
		return res;
	}
	
	private static int getScore(String response) {
		/* result form: {"jsonrpc": "2.0", "result": 2, "id": 0} */
		int pos = response.indexOf("\"result\"");
		pos = response.indexOf(" ", 20);
		int end_pos = response.indexOf(",", pos);
		String result = response.substring(pos+1, end_pos);		
		return new Integer(result);
	}

	public static String makeRequest(String path, Map<String, Object> params) throws Exception 
	{
		//instantiates httpclient to make request
		DefaultHttpClient httpclient = new DefaultHttpClient();

		//url with the post data
		HttpPost httpost = new HttpPost(path);

		//convert parameters into JSON object
		JSONObject holder = getJsonObjectFromMap(params);

		//passes the results to a string builder/entity
		StringEntity se = new StringEntity(holder.toString());

		//sets the post request as the resulting string
		httpost.setEntity(se);
		//sets a request header so the page receving the request
		//will know what to do with it
		//	    httpost.setHeader("Accept", "application/json");
		httpost.setHeader("content-type", "application/json");

		//Handles what is returned from the page 
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		return httpclient.execute(httpost, responseHandler);
	}

	private static JSONObject getJsonObjectFromMap(Map<String, Object> params) throws JSONException {

		//all the passed parameters from the post request
		//iterator used to loop through all the parameters
		//passed in the post request
		Iterator iter = params.entrySet().iterator();

		//Stores JSON
		JSONObject holder = new JSONObject();

		//using the earlier example your first entry would get email
		//and the inner while would get the value which would be 'foo@bar.com' 
		//{ fan: { email : 'foo@bar.com' } }

		//While there is another entry
		while (iter.hasNext()) 
		{
			//gets an entry in the params
			Map.Entry pairs = (Map.Entry)iter.next();

			//creates a key for Map
			String key = (String)pairs.getKey();

			//Create a new map
			Object value = (Object) pairs.getValue();
			if (value instanceof ArrayList<?>) {
				ArrayList<?> arr = (ArrayList<?>) value;
				JSONArray jsonArr = new JSONArray(arr);
				holder.put(key, jsonArr);
			} else {

				holder.put(key, value);
			}
		}
		return holder;
	}
}
