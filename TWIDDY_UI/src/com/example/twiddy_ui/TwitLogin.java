package com.example.twiddy_ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;


public class TwitLogin extends Activity {
	
	RequestToken requestToken;
    Twitter twitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twit_login);
	
		LoadWebPageASYNC twitasync = new LoadWebPageASYNC();
		twitasync.execute("");
    }
	


	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private class LoadWebPageASYNC extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... urls){
		
	        ConfigurationBuilder cb = new ConfigurationBuilder();
	         
	        //the following is set without accesstoken- desktop client
	        cb.setDebugEnabled(true)
	      .setOAuthConsumerKey("6HE4SmIKhcf4uQa4a45nakhGW")
	      .setOAuthConsumerSecret("p0kYyNJqgkq0NNc4RKkjmvNU4bapR0OJY1VqMmbJ9yMAgj1E5J");
	   
	        try {
	            TwitterFactory tf = new TwitterFactory(cb.build());
	            twitter = tf.getInstance();
	            try {
	                requestToken = twitter.getOAuthRequestToken(); 
	                
	                final String urltoload = requestToken.getAuthenticationURL();

	                System.out.println("Open the following URL and grant access to your account:");
	                System.out.println(requestToken.getAuthorizationURL());
	                TwitLogin.this.runOnUiThread(new Runnable() {
	                	public void run(){
	                		final WebView webView = (WebView) findViewById(R.id.webView);
	                		webView.getSettings().setJavaScriptEnabled(true);
	                		webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
	                		webView.loadUrl(urltoload);
	                		webView.setWebViewClient(new WebViewClient() {
	                            @Override
	                            public void onPageFinished(WebView view, String url) {
	                            	//webView.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
	                            	webView.loadUrl("javascript:window.HTMLOUT.showHTML(document.getElementsByTagName('code')[0].innerHTML);");
	                            	Log.d("HTML", "BBBBBBBBBB");
	                            }
	                        });
	                	}
	                });
	                   
	            } catch (IllegalStateException ie) {
	                // access token is already available, or consumer key/secret is not set.
	                if (!twitter.getAuthorization().isEnabled()) {
	                    System.out.println("OAuth consumer key/secret is not set.");
	                    System.exit(-1);
	                }
	            }
	             
	            //Status status = twitter.updateStatus("Hello From Twddy");
	
	         } catch (TwitterException te) {
	             te.printStackTrace();
	             System.out.println("Failed to get timeline: " + te.getMessage());
	             System.exit(-1);
	         } 
	        return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			
		}
		
		class MyJavaScriptInterface  
		{  
			@JavascriptInterface
		    public void showHTML(String pin) throws TwitterException  
		    {  
		    	if(pin.length() > 0 ) {
		    		Log.d("HTML", "pin = " + pin);
	                AccessToken accessToken = null;	                
	                try {
	                	if (pin.length() > 0) {
	                		accessToken = twitter.getOAuthAccessToken(requestToken, pin);
	                		List<twitter4j.Status> statuses = twitter.getHomeTimeline();
	                		Log.d("HTML","Showing home timeline.");
	                	    for (twitter4j.Status status : statuses) {
	                	        Log.d("HTML",status.getUser().getName() + ":" +
	                	                           status.getText());
	                	    }
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
				
		    	}
		    	else {
		    		Log.d("HTML", "No pin accept");
		    	}
		    }  
		}  
	}
}

