package main.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.Buffer;

import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

public class CollectTweets {	

	private final Object lock = new Object();
	ConfigurationBuilder cb = new ConfigurationBuilder();
	
	public CollectTweets() {
		// TODO Auto-generated constructor stub
		cb.setDebugEnabled(true);
		cb.setJSONStoreEnabled(true);
		cb.setOAuthConsumerKey("aZn7loZltZBEsmrOX5yHcOT17");
	    cb.setOAuthConsumerSecret("175UuYAkkD0k7WyAngEeBBonmRQIguFhvCAm2w0WhT7oArSc9Z");
	    cb.setOAuthAccessToken("628431093-Wcap5DePKGZTT3eFypvpNAGhwxxxdbzkBeqmYCza");
	    cb.setOAuthAccessTokenSecret("DJWoU2m29tTh7R3IWIGCTgP5sSwpEAlaCQHxUYWDnIK8R");
	   
	}
	

	public void collectData(int noOfTweetsLimit,boolean isLocationReqd,String fileName)
	{
	    FileOperations fo=new FileOperations();
	    BufferedWriter bw=fo.getOutPutFile(fileName);
	    
	    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	   

		StatusListener listener = new StatusListener() {
			long count;

			public void onException(Exception arg0) {}

			public void onDeletionNotice(StatusDeletionNotice arg0) {}

			public void onScrubGeo(long arg0, long arg1) {}

			public void onStallWarning(StallWarning arg0) {}

			public void onStatus(Status status) {		

				/* In case the user need the location. So if status dont have geolocation, it should return without
				 * further processing
				 * 
				 */
				if(isLocationReqd && status.getGeoLocation()==null){
					
					return;
				}
				
				String jsonTweet = TwitterObjectFactory.getRawJSON(status);
				try {
					    System.out.println(count + "\n"+ "noOfTweetsLimit:"+noOfTweetsLimit);	
					    count=count+1;
						bw.append(jsonTweet + "\n");	
						if(count>noOfTweetsLimit)
						{
						   System.out.println("inside aaaa"); 	
							twitterStream.clearListeners();
						}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}

			public void onTrackLimitationNotice(int arg0) {}

		};
		
		FilterQuery fq = new FilterQuery();
        String keywords[]={"adventure","amusement","Antiques","archery","Audio","Autos","Aviation","badminton",
				"baking","baseball","beach","bike","biking","Birding",
				"blogging","Boating","bowling","camping","Camps","canoe",
				"carrom","caving","chess","cliff", "jumping","coins","Collecting","concerts",
				"cooking","crafting","cricket","currency","cycling","dance","diving","DIY","driving","eating",
				"exercise","films","fishing","folk","music","Food","fooseball","football","fun","game",
				"gardening","guitar","Guns","gym","gymnastics","Highways",
				"hiking","hobby","hockey","hunting","indoor","instrument","jogging","judo","jumba",
				"kayak","Kites","kitesurfing","lawntennis","listening",
				"massage","Motorcycle","mountain" ,"biking","movie","music","nightout","Outdoor","paddle",
				"painting","paragliding","parasailing","park","Pets","photography","playing","poetry",
				"postage","puzzles","Radio","rafting","RailRoad","reading","Relax","riding horses",
				"rock climbing","row boat","rowing","sailboat","sailing","Scouting","scrapbooking",
				"sewing","shooting","skateboarding","skydiving","slides","snorkel","song","spa",
				"sports","squash","stamps","sudoku","surfing","swim","swimming","tabletennis",
				"taekwondo","television","tennis","Theme Parks","tour","train","Travel","trekking",
				"vacation","watching TV","water polo","water ski","whitewater","wildlife","wind surfing",
				"yoga","zorbing"
             };
		
		     fq.track(keywords);
		     
		     twitterStream.addListener(listener);
		     
		     twitterStream.filter(fq);
		     
		     System.out.println("returning statuses");
		     
		   
	}


}
