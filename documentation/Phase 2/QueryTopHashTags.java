package main.java;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SaveMode;

@WebServlet("/querytophashtags")
public class QueryTopHashTags extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException 
	{
		String inputFile = getServletContext().getRealPath("/")+"Twitter.json";
		String hashTagsFile = getServletContext().getRealPath("/")+"hashtags.txt";
		
		System.out.println("Servlet input file-----------" + inputFile);
		long startTime=System.currentTimeMillis();
		
		SetSparkConf sparkConf=new SetSparkConf("topHashTags");
		
		DataFrame data=sparkConf.getDataFrameFromJsonFile(inputFile);
		data=data.select("text");
				
		DataFrame data1=sparkConf.getDataFrameFromTextFile(hashTagsFile);
		
		// join is performed with hashtags file, then grouping and count is performed
		data=data.join(data1,data.col("text").contains(data1.col("value")));
		data=data.groupBy(data.col("value")).count().orderBy(org.apache.spark.sql.functions.desc("count")).limit(20);
		
		//saving file
		data.repartition(1).write().format("json").mode(SaveMode.Overwrite).save(getServletContext().getRealPath("/")+"/tophashtagsoutput");
	    
		long endTime=System.currentTimeMillis();        
	    System.out.println("Total time for execution:"+(endTime-startTime));
				
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
