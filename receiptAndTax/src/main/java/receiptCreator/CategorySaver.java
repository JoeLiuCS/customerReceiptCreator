package receiptCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CategorySaver {
	
	private String path;
	private Set<String> foods;
	private Set<String> books;
	private Set<String> medicines;
	
	public CategorySaver(String soursePath) {
		this.path = soursePath;
		this.foods = new HashSet<String>();
		this.books = new HashSet<String>();
		this.medicines = new HashSet<String>();
		storeAllFiles();
	}
	
	public String tellMeclassified(String inputString) {
		if(foods.contains(inputString)) {
			return "food";
		}
		else if(books.contains(inputString)) {
			return "book";
		}
		else if(medicines.contains(inputString)) {
			return "medicines";
		}
		else {
			return "else";
		}
	}
	
	private void storeAllFiles() {
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  
			String fileName = listOfFiles[i].getName();
			String categoryName = fileName.substring(0, fileName.length()-4);
			
			switch(categoryName) {
				case "foods":
					storeFile(this.foods,path+fileName);
					break;
				case "books":
					storeFile(this.books,path+fileName);
					break;
				case "medicines":
					storeFile(this.medicines,path+fileName);
					break;
				default:
					//Nothing
					break;
			}
		  }
		}
		
		System.out.println("Foods total:" + foods.size()+" books total: "+books.size() + " medicines total: "+medicines.size());
	}
	
	private void storeFile(Set<String> product,String productPath) {
		System.out.println("My path:"+productPath);
		try {
		    BufferedReader lineReader = new BufferedReader(new FileReader(productPath));
		    String lineText = null;
		 
		    while ((lineText = lineReader.readLine()) != null) {
		    	product.add(lineText.toLowerCase());
		    }
		 
		    lineReader.close();
		} catch (IOException ex) {
		    System.err.println(ex);
		}
	}
	
}
