package receiptCreator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * This class use for create database for transactions.
 * @author shuoqiaoliu
 *
 */
public class DataBaseCreator {
	
	private String savePath;
	private String fileName;
	private String my_database_url;
	private String[] header = {"Purchased_Items", "Sale_Tax", "Total"};
	
	public DataBaseCreator(String newFileName, String path) {
		fileName = newFileName;
		savePath = path;
		my_database_url = "jdbc:sqlite:" + this.savePath + this.fileName + ".db";
	}
	
	public void createNewDatabase() {
		String my_table = "CREATE TABLE mytable (" 
				+ String.join(" TEXT NOT NULL, ", header) 
					+ " TEXT NOT NULL) ";

		try (Connection conn = DriverManager.getConnection(my_database_url);
			Statement stmt = conn.createStatement()) {
			stmt.execute(my_table);
			stmt.close();
			conn.close();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void insertToDatabase(String[] info) {
		String[] unknowValues = new String[header.length];
    	Arrays.fill(unknowValues, "?");
    	
        String sql = "INSERT INTO mytable("
        				+String.join(",", header)
        					+") VALUES("
        						+String.join(",", unknowValues)
        							+")";
 
        try (Connection conn = DriverManager.getConnection(my_database_url);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for(int i=0; i<header.length; i++) { 
            	pstmt.setString(i + 1, info[i]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "(Insert)");
        }
	}

}
