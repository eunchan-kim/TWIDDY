package com.example.twiddy_ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TwitLogin extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twit_login);
		
		LoadWebPageASYNC twitasync = new LoadWebPageASYNC();
		twitasync.execute("");
		
    }
        	
	
	private class LoadWebPageASYNC extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... urls){
		
			String testStatus="Hello from twitter4j ";
	        ConfigurationBuilder cb = new ConfigurationBuilder();
	         
	        //the following is set without accesstoken- desktop client
	        cb.setDebugEnabled(true)
	      .setOAuthConsumerKey("6HE4SmIKhcf4uQa4a45nakhGW")
	      .setOAuthConsumerSecret("p0kYyNJqgkq0NNc4RKkjmvNU4bapR0OJY1VqMmbJ9yMAgj1E5J");
	   
	        try {
	            TwitterFactory tf = new TwitterFactory(cb.build());
	            Twitter twitter = tf.getInstance();
	            try {
	                System.out.println("-----");
	 
	                // get request token.
	                // this will throw IllegalStateException if access token is already available
	                // this is oob, desktop client version
	                RequestToken requestToken = twitter.getOAuthRequestToken(); 
	 
	                System.out.println("Got request token.");
	                System.out.println("Request token: " + requestToken.getToken());
	                System.out.println("Request token secret: " + requestToken.getTokenSecret());
	 
	                System.out.println("|-----");
	 
	                AccessToken accessToken = null;
	 
	                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	                
	                final String urltoload = requestToken.getAuthenticationURL();

	                System.out.println("Open the following URL and grant access to your account:");
	                System.out.println(requestToken.getAuthorizationURL());
	                TwitLogin.this.runOnUiThread(new Runnable() {
	                	public void run(){
	                		WebView webView = (WebView) findViewById(R.id.webView);
	                		webView.getSettings().setJavaScriptEnabled(true);
	                		webView.loadUrl(urltoload);
	                	}
	                });
	                    
	                System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
	                String pin = "";//br.readLine();
	                /*    
	                try {
	                	if (pin.length() > 0) {
	                		accessToken = twitter.getOAuthAccessToken(requestToken, pin);
	                	} else {
	                		accessToken = twitter.getOAuthAccessToken(requestToken);
	                	}
	                } catch (TwitterException te) {
	                	if (401 == te.getStatusCode()) {
	                		System.out.println("Unable to get the access token.");
	                	} else {
	                		te.printStackTrace();
	                	}
	                }
	                /*
	                System.out.println("Got access token.");
	                System.out.println("Access token: " + accessToken.getToken());
	                System.out.println("Access token secret: " + accessToken.getTokenSecret());
	                 */
	            } catch (IllegalStateException ie) {
	                // access token is already available, or consumer key/secret is not set.
	                if (!twitter.getAuthorization().isEnabled()) {
	                    System.out.println("OAuth consumer key/secret is not set.");
	                    System.exit(-1);
	                }
	            }
	             
	            //Status status = twitter.updateStatus("Hello From Twddy");
	  
	            //System.out.println("Successfully updated the status to [" + status.getText() + "].");
	  
	            //System.out.println("ready exit");
	              
	            //System.exit(0);
	         } catch (TwitterException te) {
	             te.printStackTrace();
	             System.out.println("Failed to get timeline: " + te.getMessage());
	             System.exit(-1);
	         } /*catch (IOException ioe) {
	             ioe.printStackTrace();
	             System.out.println("Failed to read the system input.");
	             System.exit(-1);
	         }*/
	        return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			
		}
	}
}

