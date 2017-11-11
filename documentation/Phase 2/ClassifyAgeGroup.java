package main.java;

import java.io.IOException;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@WebServlet("/queryagegroup")
public class ClassifyAgeGroup extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException 
	{
		String inputFile = getServletContext().getRealPath("/")+"TwitterSentimentAnalysis.json";
		System.out.println("Servlet input file-----------" + inputFile);
		long startTime=System.currentTimeMillis();
		
		SetSparkConf sparkConf=new SetSparkConf("sentimentanalysis");
    
		CollectTweets ct=new CollectTweets();
		ct.collectData(100, false,inputFile);
		
		long endTimeForTweets=System.currentTimeMillis();
	    System.out.println("Total time for collecting tweets:"+(endTimeForTweets-startTime));
	    
		DataFrame df=sparkConf.getDataFrameFromJsonFile(inputFile);
		df=df.select("text");		
		
		/*
		 * This UDF will work on text tweet and it will using uclassify PUBLIC api, wil give the age group of tweet text and hence 
		 * suggests which age group have the max tweets w.r.t adventure , recreation etc.
		 */
		sparkConf.sc.udf().register("classifyAge", new UDF1<String, String>() {
		      @Override
		      public String call(String text) {
		    	
		    	  /*
			        * Sample response from uclassify URL is:
			        * 
			        * {
		                 	13-17: 0.435646,
		                 	18-25: 0.154309,
							26-35: 0.110658,
							36-50: 0.118112,
							51-65: 0.086204,
							65-100: 0.0950713
					   }
					   
			        *  The one with the highest percentage will be at the top and hence age group is identified.
			        */
		    	  
		    	  
			       String key="Not Defined";
			       System.out.println("text:"+text);
			       if(null==text)
			    	   return key;
			       text=text.replace("#", "");
			       text=text.replace("@", "");
			       text = text.replaceAll("[^a-zA-Z0-9/]" , " ");
			       text = text.replaceAll("  " , "");
			       text = text.replaceAll(" " , "%20");
			       
			       /* 
			        *  Sample text is : Kid Mid Radio - Attention Everyone ID https://t.co/DH2J6BRKT5 #nowplaying #listenlive
			        */
			       if(text.contains("http"))
			       {
			    	   String text1=text.substring(text.indexOf("http"));
			    	   text1=text1.substring(0, text1.indexOf(" ")+1);
			    	   System.out.println("text for http replacement is:"+text1);
			    	   text=text.replaceAll(text1, "");
			       
			       }
			       
			       System.out.println("final text is:"+text);
			       
				  			  
				   /*
				    * This nullify all the security. Better way is to install end site certifacte in JVM using keytool
				    * 
				    * http://stackoverflow.com/questions/6659360/how-to-solve-javax-net-ssl-sslhandshakeexception-error
				    */
				   
				   TrustManager[] trustAllCerts = new TrustManager[]{
		                    new X509TrustManager() {

		                        public java.security.cert.X509Certificate[] getAcceptedIssuers()
		                        {
		                            return null;
		                        }
		                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
		                        {
		                            //No need to implement.
		                        }
		                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
		                        {
		                            //No need to implement.
		                        }
		                    }
		            };

		            // Install the all-trusting trust manager
		            try 
		            {
		                SSLContext sc = SSLContext.getInstance("SSL");
		                sc.init(null, trustAllCerts, new java.security.SecureRandom());
		                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		            } 
		            catch (Exception e) 
		            {
		                System.out.println(e);
		            }
		           
		           String classifyURL =   "https://api.uclassify.com/v1/uclassify/Ageanalyzer/Classify?readkey=AkIXBID53q3M&text="+text;
				   System.out.println("classifyURL:"+classifyURL);
					    
				   Client client = Client.create();
				   WebResource web = client.resource(classifyURL);
				   ClientResponse response = web.accept("application/json").get(ClientResponse.class);
				   String output = response.getEntity(String.class);  
				   System.out.println("output is:"+output.toString());
				   JSONObject response1 = new JSONObject(output.toString());
			       
				   Iterator keys=response1.keys();
				
				   Double maxValue=0.0;
				   
				   while(keys.hasNext())
				   {
					   String key1 = (String)keys.next();
					   Double value=response1.getDouble(key1);
					   System.out.println("age group key is:"+key1+" value :"+value);					  
					  if(value>maxValue)
					   {	   maxValue=value;					           
					           key=key1;
					           System.out.println("updated age group to:"+key);
					   }
					   
				   }
				   System.out.println("age group is:"+key);
				   return key;  
		        
		      }
		    },DataTypes.StringType);
		
		/*calling User defined function on text for age classification*/
		df=df.select(org.apache.spark.sql.functions.callUDF("classifyAge", df.col("text")).as("ageGroup"));
		df=df.groupBy("ageGroup").count();
		df.repartition(1).write().format("json").mode(SaveMode.Overwrite).save(getServletContext().getRealPath("/")+"/ageAnalysis");
        
        long endTime=System.currentTimeMillis();
	    System.out.println("Total time for execution:"+(endTime-startTime));
	    System.out.println("Total time for query execution:"+(endTime-endTimeForTweets));
	    
		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
