package receiptCreator;

public class Buyer {

	public static void main(String[] args) {
		
		CategorySaver categories = new CategorySaver("/Users/shuoqiaoliu/eclipse-workspace/receiptAndTax/src/main/resources/"); 
		String type = categories.tellMeclassified("pills".toLowerCase());
		System.out.println("Tell me: "+type);
	}

}
