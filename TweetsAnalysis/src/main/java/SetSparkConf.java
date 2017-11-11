package main.java;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class SetSparkConf {
	
	 JavaSparkContext ctx;
	 DataFrame d; 
	 SQLContext sc;
	 SparkConf sparkConf; 
	 
	 public SetSparkConf(String appName) {
		// TODO Auto-generated constructor stub
		sparkConf = new SparkConf().setAppName(appName).setMaster("local").set("spark.driver.allowMultipleContexts", "true");
		ctx = new JavaSparkContext(sparkConf);
		sc = new SQLContext(ctx);
	         
	}
	 
	public  JavaSparkContext getSparkConf()
	{
		
        return ctx;

	}
	
	public DataFrame getDataFrameFromJsonFile(String inputFile)
	{       //d = sc.jsonFile(inputFile);
	        d=sc.read().json(inputFile);
	        
	        return d;
	}
	
	public DataFrame getDataFrameFromTextFile(String inputFile)
	{       //d = sc.jsonFile(inputFile);
	        d=sc.read().text(inputFile);
	        
	        return d;
	}
	
	public  DataFrame executeQuery(String sqlQuery,String inputFile)
	{
	        getDataFrameFromJsonFile(inputFile);  
		    d.registerTempTable("tweets");	       
	        DataFrame data = sc.sql(sqlQuery);	        
	        return data;

	}

	public void destroy()
	{
		ctx.stop();
	}
}
