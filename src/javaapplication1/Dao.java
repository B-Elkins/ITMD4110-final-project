package javaapplication1;

//imports
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

public class Dao {
	// instance fields
	static Connection connect = null; //intialize variables
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			//connects the program to remote database
			connect = DriverManager.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false" + "&user=fp411&password=411");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		//create tables with all their fields
		final String createTicketsTable = "CREATE TABLE belki_support_tickets2(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), open_date DATE, close_date DATE)";
		final String createUsersTable = "CREATE TABLE belki_users2(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			//make an sql statement to be sent to DBMS
			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);//Create belki_support_tickets2
			statement.executeUpdate(createUsersTable);//Create belki_users2
			System.out.println("Created tables in given database..."); //print to console

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			//read userlist.csv for user credentials
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				//Insert field headers
				sql = "insert into belki_users2(uname,upass,admin) " + "values('" + rowData.get(0) + "','"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);//execute statment
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	//method for insertion/creation
	public int insertRecords(String ticketName, String ticketDesc , LocalDate openDate) {
		int id = 0; //intialize variable as 0
		try {
			statement = getConnection().createStatement();//connect and create sql statement
			//execute the sql statement
			//insert the values of the parameters to database
			statement.executeUpdate("Insert into belki_support_tickets2" + "(ticket_issuer, ticket_description, open_date) values(" + " '"
					+ ticketName + "','" + ticketDesc + "','" + openDate + "');", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1); //id changes to the id of new ticket
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id; //return ticket id

	}

	//method for reading the contents of the database table
	public ResultSet readRecords() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();// create statement
			//query entire table
			results = statement.executeQuery("SELECT * FROM belki_support_tickets2");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results; //return results of query
	}

	//method for updating pre existing records
	public void updateRecords(String updatedDesc, int tickID) {
		try {
			System.out.println("Creating update statement...");
			statement = connect.createStatement();// create statement
			//change the ticket description of the ticket with the given ID
			String sql = "UPDATE belki_support_tickets2 " +
						 "SET ticket_description =" + "'" + updatedDesc + "'" +  "WHERE ticket_id = " + tickID;
			statement.executeUpdate(sql);//execute
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//method for deleting records
	public void deleteRecords(int tickID) {
		try {
			System.out.println("Creating statement...");
			statement = connect.createStatement();// create statement
			//Delete ticket of the given ID
			String sql = "DELETE FROM belki_support_tickets2 " + "WHERE ticket_id = " + tickID;

			//Confirm the user wants to delete this ticket with a dialog box
			int response = JOptionPane.showConfirmDialog(null, "Delete ticket" + tickID + "?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (response == JOptionPane.NO_OPTION) {//if select no cancel
				System.out.println("NO record deleted");
			} else if (response == JOptionPane.YES_OPTION) {//if select yes execute 
				statement.executeUpdate(sql);
			} else if (response == JOptionPane.CLOSED_OPTION) {
				System.out.println("Request cancelled");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//method for closing a ticket
	public void closeTicket(int tickID, LocalDate close_date) {
		try {

			//update close_date field to the current date
			String sql = "UPDATE belki_support_tickets2 " + "SET close_date = " + "'" + close_date + "'" + "WHERE ticket_id = " + tickID;
			statement = connect.createStatement(); //create statement
			statement.execute(sql); //execute

			//dialog box to confirm user wants to close this ticket
			int response = JOptionPane.showConfirmDialog(null, "Close ticket" + tickID + "?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (response == JOptionPane.NO_OPTION) {// if pick no then cancel
				System.out.println("NO ticket closed");
			} else if (response == JOptionPane.YES_OPTION) {// if pick yes then close
				statement.executeUpdate(sql);
			} else if (response == JOptionPane.CLOSED_OPTION) {
				System.out.println("Request cancelled");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
