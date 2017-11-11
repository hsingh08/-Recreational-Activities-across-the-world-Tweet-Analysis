package main.java;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@WebServlet("/queryRecreationMode")
public class QueryRecreationMode extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException 
	{
		String inputFile = getServletContext().getRealPath("/")+"/Twitter.json";
		long startTime=System.currentTimeMillis();
		SetSparkConf sc=new SetSparkConf("RecreationActivityCount");
		JavaRDD<String> data = sc.ctx.textFile(inputFile);		
		JavaRDD<String> output = data.filter(s -> s.contains("\"text\":"));
		
		JavaRDD<String> words = output.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterable<String> call(String row) throws Exception {
				// TODO Auto-generated method stub								
					String type = "";
					String rowText=row.toLowerCase();					
					KeywordsList kw=new KeywordsList();									
					for (String key : kw.modeMap.keySet())
					{	System.out.println("Key : " + key);
						for (String value : kw.modeMap.get(key))
						{   if(rowText.contains(value))
							{	type=type+" "+key;							
								break;
							}}}					
					type.trim();
					return Arrays.asList(type.split(" "));
		 }});
				
		JavaPairRDD<String, Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
			public Tuple2<String, Integer> call(String s) {
				return new Tuple2(s, 1);
			}
		});

		
		JavaPairRDD<String, Integer> reducedCounts = counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
			public Integer call(Integer x, Integer y) {
				return x + y;
			}
		});
		
		
		FileOperations fo=new FileOperations();
        BufferedWriter bw=fo.getOutPutFile(getServletContext().getRealPath("/")+"/popularword.csv");
        
        bw.append("word,count");
		List<String> keys = reducedCounts.keys().toArray();
		List<Integer> values = reducedCounts.values().toArray();
		for (int i = 0; i < keys.size() - 1; i++) {
			bw.newLine();
			bw.append(keys.get(i) + "," + values.get(i));
			System.out.println(keys.get(i) + "," + values.get(i));
		}
		long endTime=System.currentTimeMillis();        
        System.out.println("Total time for execution:"+(endTime-startTime));        
		fo.destroy();
		sc.destroy();
		// request.setAttribute("total", total);
		RequestDispatcher rd = request.getRequestDispatcher("wordcloud.jsp");
		rd.forward(request, response);
		
		
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
