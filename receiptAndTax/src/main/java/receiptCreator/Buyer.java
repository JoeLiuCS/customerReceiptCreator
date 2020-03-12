package receiptCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Buyer {

	static String sourseFilePath = "/Users/shuoqiaoliu/git/customerReceiptCreator/receiptAndTax/src/main/resources/";
	static String inputsFilePath = sourseFilePath + "/Inputs/";
	static CategorySaver categories;
	
	Map<Double,String> priceAndItem = new HashMap<>();
	List<List<Double>> prices = new LinkedList<>();
	
	public static void main(String[] args) {
		
		categories = new CategorySaver(sourseFilePath); 
//		String type = categories.tellMeclassified("pills".toLowerCase());
//		System.out.println("Tell me: "+type);
		
		File folder = new File(inputsFilePath);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile()) {
				  
				String fileName = listOfFiles[i].getName();
				String categoryName = fileName.substring(0, fileName.length()-4);
				
				inputReader(inputsFilePath + fileName);
			  }
		}
	}
	
	private static void inputReader(String inputPath) {
		try {
		    BufferedReader lineReader = new BufferedReader(new FileReader(inputPath));
		    String lineText = null;
		    
		    double salesTax = 0.0;
		    double total = 0.0;
		 
		    while ((lineText = lineReader.readLine()) != null) {
		    	String[] stringArray = lineText.toLowerCase().split(" ");
		    	//book food or medicines
		    	boolean categoryWaiveTax = checkCategory(stringArray);
		    	
		    	Boolean imported = isImported(stringArray);
		    	
		    	double taxRate = 1.0;
		    	if(! categoryWaiveTax) taxRate += 0.1;
		    	if(imported) taxRate += 0.05;
		    	
		    	Double price = Double.valueOf(stringArray[stringArray.length-1]);
		    	
		    	Double priceAfterTax = priceRound(price * taxRate);
		    	System.out.println("Price: " + priceRound(priceAfterTax));
		    	
		    	salesTax = Math.round((salesTax + priceAfterTax - price) * 100.0) / 100.0;
		    	total = Math.round((total + priceAfterTax) * 100.0) / 100.0;
		    	
		    	System.out.println("sales tax: " + salesTax + " total: " + total);
		    	
//		    	System.out.print("Categ:"+ category );
//		    	System.out.println("Imported:"+ imported );
		    	
		    }
		    
		    System.out.println("Finished");
		    lineReader.close();
		} catch (IOException ex) {
		    System.err.println(ex);
		}
	}
	
	private static boolean isImported(String[] items) {
		for(String item:items) {
			if(item.equals("imported")) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean checkCategory(String[] items) {
		for(String item : items) {
			String category = categories.tellMeclassified(item);
			if(category.equals("book") || category.equals("food") || category.equals("medicines")) {
				return true;
			}
		}
		return false;
	}
	
	private static double priceRound(double price) {
		int dotPosition = 0;
		char[] strPrice = Double.toString(price).toCharArray();
		for(int i=0;i<strPrice.length;i++) {
			if(strPrice[i] == '.') {
				dotPosition = i;
				break;
			}
		}
		// only 2 decimals or 1 decimals
		if(strPrice.length - dotPosition - 1 <= 2) {
			return price;
		}
		else {
			char num = strPrice[dotPosition + 2];
			if (( num - '5') >= 0) {
				num += 1;
			}
			else {
				num = '5';
			}
			strPrice[dotPosition + 2] = num;
		}
		
		String newPrice ="";
		for(int i=0;i<5;i++) {
			newPrice +=strPrice[i];
		}
		
		return Double.valueOf(newPrice);
	}

}
