package receiptCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Buyer {

	static String sourseFilePath = "";
	static String inputsFilePath = "";
	static CategorySaver categories;
	static DataBaseCreator myOutputDataBase;
	
	static Queue<String> mostExpensiveInLastThree = new LinkedList<>();
	static Queue<Double> amountInLastThree = new LinkedList<>();
	
	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		System.out.println("---Please type absolute path from source files---");
		System.out.println("For Example: /User/Source File/");
		sourseFilePath += scan.nextLine();
		System.out.println("---Please type absolute path for Input files---");
		System.out.println("For Example: /User/Inputs/");
		inputsFilePath += scan.nextLine();
		scan.close();
		
		myOutputDataBase = new DataBaseCreator("outputDatabase", sourseFilePath);
		
		categories = new CategorySaver(sourseFilePath); 
		
		File folder = new File(inputsFilePath);
		File[] listOfFiles = folder.listFiles();
		
		myOutputDataBase.createNewDatabase();
		
		//Get all input files
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
		
		//Queries text file create
		TextCreator queries = new TextCreator(inputsFilePath, "Queries");
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
		    	//Put purchase item to receipt text file
		    	textOutput.writeIn(writeInString);
		    	
		    	if(priceAfterTax > mostExpensive) {
		    		mostExpensive = priceAfterTax;
		    		mostExpensiveString = writeInString;
		    	}

		    }
		    //Put salesTax and total to receipt text file
		    textOutput.writeIn("Sales Tax: " + salesTax + " \nTotal: " + total);
		    //Add to database
		    myOutputDataBase.insertToDatabase(
		    		new String[] {purchasedItems,String.valueOf(salesTax),String.valueOf(total)});
		    
		    addToAmountInLastThree(total);
		    addToMostExpensiveInLastThree(mostExpensiveString);
		    
		    //Close
		    textOutput.closeText();
		    lineReader.close();
		} catch (IOException ex) {
		    System.err.println(ex);
		}
	}
	/*
	 * The most expensive item purchased in each of the last 3 transactions
	 */
	private static void addToMostExpensiveInLastThree(String s) {
		mostExpensiveInLastThree.add(s);
		if(mostExpensiveInLastThree.size()>3) {
			mostExpensiveInLastThree.poll();
		}
	}
	/*
	 * The average amount spent between the last 3 transactions
	 */
	private static void addToAmountInLastThree(double d) {
		amountInLastThree.add(d);
		if(amountInLastThree.size()>3) {
			amountInLastThree.poll();
		}
	}
	/*
	 * Check it is Imported item.
	 */
	private static boolean isImported(String[] items) {
		for(String item:items) {
			if(item.equals("imported")) {
				return true;
			}
		}
		return false;
	}
	/*
	 * Check it is book, food, medicine or else.
	 */
	private static boolean checkCategory(String[] items) {
		for(String item : items) {
			String category = categories.tellMeclassified(item);
			if(category.equals("book") || category.equals("food") || category.equals("medicines")) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Round up the numbers 0.05
	 */
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

	/*
	 * Function use to create output strings for receipt.
	 * Example: "1 imported bottle of perfume:"
	 */
	private static String outputString(String[] strArray) {
		String[] newString = new String[strArray.length - 2];
		for(int i=0;i<newString.length;i++) {
			newString[i] = strArray[i];
		}
		return String.join(" ", newString) + ": ";
	}
}
