package receiptCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Buyer {

	static String sourseFilePath = "/Users/shuoqiaoliu/git/customerReceiptCreator/receiptAndTax/src/main/resources/";
	static String inputsFilePath = sourseFilePath + "Inputs/";
	static CategorySaver categories;
	static DataBaseCreator myOutputDataBase = new DataBaseCreator("outputDatabase",sourseFilePath);
	
	static Queue<String> mostExpensiveInLastThree = new LinkedList<>();
	static Queue<Double> amountInLastThree = new LinkedList<>();
	
	public static void main(String[] args) {
		
		categories = new CategorySaver(sourseFilePath); 
		
		File folder = new File(inputsFilePath);
		File[] listOfFiles = folder.listFiles();
		
		myOutputDataBase.createNewDatabase();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getName();
				if(fileName.indexOf(".txt") != -1) {
					String textName = fileName.substring(0, fileName.length()-4);
					TextCreator textOutput = new TextCreator(inputsFilePath,textName);
					inputReader(inputsFilePath + fileName,textOutput);
				}
			  }
		}
		
		TextCreator queries = new TextCreator(inputsFilePath,"Queries");
		queries.writeIn("The most expensive item purchased in each of the last 3 transactions");
		while(!mostExpensiveInLastThree.isEmpty()){
			queries.writeIn(mostExpensiveInLastThree.poll());
		}
		double total =0.0;
		double amountTotal = 0.0;
		while(!amountInLastThree.isEmpty()) {
			total +=amountInLastThree.poll();
			amountTotal += 1.0;
		}
		total = Math.round((total/amountTotal) * 100.0) / 100.0;
		queries.writeIn("The average amount spent between the last 3 transactions");
		queries.writeIn(String.valueOf(total));
		queries.closeText();
	}
	
	private static void inputReader(String inputPath, TextCreator textOutput) {
		try {
		    BufferedReader lineReader = new BufferedReader(new FileReader(inputPath));
		    String lineText = null;
		    
		    String purchasedItems = "";
		    double salesTax = 0.0;
		    double total = 0.0;
		    
		    double mostExpensive = 0.0;
		    String mostExpensiveString ="";
		    
		    while ((lineText = lineReader.readLine()) != null) {
		    	String[] stringArray = lineText.toLowerCase().split(" ");
		    	//book food or medicines
		    	boolean categoryWaiveTax = checkCategory(stringArray);
		    	boolean imported = isImported(stringArray);
		    	
		    	double taxRate = 1.0;
		    	if(! categoryWaiveTax) taxRate += 0.1;
		    	if(imported) taxRate += 0.05;
		    	
		    	Double price = Double.valueOf(stringArray[stringArray.length-1]);
		    	
		    	Double priceAfterTax = priceRound(price * taxRate);
		    	
		    	salesTax = Math.round((salesTax + priceAfterTax - price) * 100.0) / 100.0;
		    	total = Math.round((total + priceAfterTax) * 100.0) / 100.0;
		    	
		    	String writeInString = outputString(stringArray)+Double.toString(priceAfterTax);
		    	purchasedItems = purchasedItems + writeInString + "\n";
		    	textOutput.writeIn(writeInString);
		    	
		    	if(priceAfterTax > mostExpensive) {
		    		mostExpensive = priceAfterTax;
		    		mostExpensiveString = writeInString;
		    	}
		    	
		    	System.out.println(outputString(stringArray)+Double.toString(priceAfterTax));

		    }
		    textOutput.writeIn("Sales Tax: " + salesTax + " \nTotal: " + total);
		    System.out.println("Sales Tax: " + salesTax + " \nTotal: " + total+"\n");
		    
		    myOutputDataBase.insertToDatabase(new String[] {purchasedItems,String.valueOf(salesTax),String.valueOf(total)});
		    
		    addToAmountInLastThree(total);
		    addToMostExpensiveInLastThree(mostExpensiveString);
		    
		    textOutput.closeText();
		    lineReader.close();
		} catch (IOException ex) {
		    System.err.println(ex);
		}
	}
	
	private static void addToMostExpensiveInLastThree(String s) {
		mostExpensiveInLastThree.add(s);
		if(mostExpensiveInLastThree.size()>3) {
			mostExpensiveInLastThree.poll();
		}
	}
	
	private static void addToAmountInLastThree(double d) {
		amountInLastThree.add(d);
		if(amountInLastThree.size()>3) {
			amountInLastThree.poll();
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

	
	private static String outputString(String[] strArray) {
		String[] newString = new String[strArray.length - 2];
		for(int i=0;i<newString.length;i++) {
			newString[i] = strArray[i];
		}
		return String.join(" ", newString) + ": ";
	}
}
