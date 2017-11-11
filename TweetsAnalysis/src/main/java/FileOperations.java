package main.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperations {
	
	BufferedWriter bw;
	
	public BufferedWriter getOutPutFile(String outputFile)
	{
        File f = new File(outputFile);
        
        FileWriter fw = null;
        
        if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			fw = new FileWriter(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bw = new BufferedWriter(fw);
		
		return bw;

	}
	
	public void destroy() throws IOException
	{
		bw.flush();
        bw.close();
        
	}

}
