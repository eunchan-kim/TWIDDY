/*
 * Main developers: 주세현, 한주형
 * Debuggers: 주세현, 한주형
 */
package com.example.twiddy_ui;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterObj {
	
	private Twitter twitter;
	
	TwitterObj()
	{
		this.twitter = null;
	}
	
	public Twitter GetTwitter(){
		return this.twitter;	
	}
	
	public void SetTwitter(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    
	    //the following is set without accesstoken- desktop client
	    cb.setDebugEnabled(true)
	  .setOAuthConsumerKey("6HE4SmIKhcf4uQa4a45nakhGW")
	  .setOAuthConsumerSecret("p0kYyNJqgkq0NNc4RKkjmvNU4bapR0OJY1VqMmbJ9yMAgj1E5J");
	    
	    TwitterFactory tf = new TwitterFactory(cb.build());
		this.twitter = tf.getInstance();	
	}
}
