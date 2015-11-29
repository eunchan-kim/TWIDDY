package com.example.twiddy_ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    Context ctx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twit_login);
		ctx = this;
		LoadWebPageASYNC twitasync = new LoadWebPageASYNC();
		twitasync.execute("");
    }

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private class LoadWebPageASYNC extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... urls){		
	        TwitterObj tobj = new TwitterObj();
	        if(tobj.GetTwitter() == null){
	        	tobj.SetTwitter();
	        }
	        twitter = tobj.GetTwitter();
	        
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
                		webView.loadUrl(urltoload+"&force_login=true");
                		webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                            	webView.loadUrl("javascript:window.HTMLOUT.showHTML("
                            			+ "document.getElementsByTagName('code')[0].innerHTML);");	                            	
                            }
                        });
                	}
                });           
	         } catch (TwitterException te) {
	             te.printStackTrace();
	             Log.d("HTML", "Failed to get timeline: " + te.getMessage());
	             System.exit(-1);
	         }             
            return null;
		}
		
		class MyJavaScriptInterface  
		{  
			@JavascriptInterface
		    public void showHTML(String pin) throws TwitterException  
		    {  
		    	if(pin.length() > 0 ) {
		    		Log.d("PIN", "pin = " + pin);  
                	twitter.getOAuthAccessToken(requestToken, pin);	            		       
            	    Intent it_display = new Intent(ctx, DisplayEmotion.class);
            	    it_display.putExtra("twitter", twitter);
        			startActivity(it_display);            			
            	    finish();
	                				
		    	}
		    	else {
		    		Log.d("HTML", "No pin accept");
		    	}
		    }  
		}  
	}
}

