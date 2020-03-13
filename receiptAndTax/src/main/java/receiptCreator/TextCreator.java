package receiptCreator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class TextCreator {
	
	private PrintWriter writer;
	
	public TextCreator(String storePath,String fileName) {
		try {
			this.writer = new PrintWriter(storePath+"Output"+fileName+".txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void writeIn(String str) {
		this.writer.println(str);
	}
	
	public void closeText() {
		this.writer.close();
	}
}
