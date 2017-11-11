package main.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SaveMode;



/**
 * Servlet implementation class CountryServlet
 */
@WebServlet("/querycountry")
public class QueryCountry extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryCountry() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		String inputFile = getServletContext().getRealPath("/")+"Twitter.json";
		System.out.println("Servlet input file-----------" + inputFile);
		long startTime=System.currentTimeMillis();
		
		SetSparkConf sc=new SetSparkConf("Country");
        DataFrame data=sc.getDataFrameFromJsonFile(inputFile);		
		data=data.groupBy(data.col("place.country")).count();
		DataFrame data1=data.filter("count >100");		
		DataFrame data2=data.filter("count<100");
		
		data2=data2.agg(org.apache.spark.sql.functions.sum(data.col("count")).as("count"));		
		data2=data2.withColumn("country", org.apache.spark.sql.functions.lit("others"));
		data2=data2.select("country","count");
		data2=data2.unionAll(data1);
		
		data2.repartition(1).write().format("json").mode(SaveMode.Overwrite).save(getServletContext().getRealPath("/")+"/country");
        
            
        long endTime=System.currentTimeMillis();        
        System.out.println("Total time for execution:"+(endTime-startTime));
		
        sc.destroy();
        
        
        RequestDispatcher rd = request.getRequestDispatcher("country.jsp");
        rd.forward(request, response);
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}