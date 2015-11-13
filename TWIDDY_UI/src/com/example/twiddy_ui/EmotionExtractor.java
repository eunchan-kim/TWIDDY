package com.example.twiddy_ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class EmotionExtractor {
	public static void getEmotion(String msg) {
		
		String URL = "http://143.248.142.86:4000/jsonrpc";
		HashMap<String,Object> params = new HashMap<String, Object>();
		params.put("method", "get_emotion");
		params.put("params", msg);
		params.put("jsonrpc", "2.0");
		params.put("id", new Integer(0));
		String response;
		try {
			response = makeRequest(URL, params);
			Log.e("getEmotion", response);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json");

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

	        holder.put(key, value);
	    }
	    return holder;
	}
}
