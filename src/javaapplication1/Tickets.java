package javaapplication1;

//Imports
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null; //Intialize variable

	// Main menu object items
	private JMenu mnuFile = new JMenu("File"); //initialize File main menu
	private JMenu mnuAdmin = new JMenu("Admin"); //initialize Admin main menu
	private JMenu mnuTickets = new JMenu("Tickets"); //initialize Tickets main menu

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit; //create exit button
	JMenuItem mnuItemUpdate; //update button
	JMenuItem mnuItemDelete; //delete button
	JMenuItem mnuItemOpenTicket; //open ticket button
	JMenuItem mnuItemCloseTicket; //close ticket button
	JMenuItem mnuItemViewTicket; //view tickets button

	//primary method that calls the other methods
	public Tickets(Boolean isAdmin) {

		chkIfAdmin = isAdmin; //variable that gets passed from parameter
		createMenu(); //call createMenu method
		prepareGUI(); //call prepareGUI method

		System.out.println("Is Admin: " + chkIfAdmin); // print to console if the logged in user is an admin
		if (chkIfAdmin != true) { //condition for checking if the logged in user is an admin
			//if not an admin make the following invisible
			mnuAdmin.setVisible(false);
			mnuItemViewTicket.setVisible(false);
		}

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize close sub menu for tickets main menu
		mnuItemCloseTicket = new JMenuItem("Close Ticket");
		// add to Ticket Main menu
		mnuTickets.add(mnuItemCloseTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemCloseTicket.addActionListener(this);

	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar(); //create menu bar
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		bar.add(mnuAdmin); // add admin menu to bar
		bar.add(mnuTickets);// add tickets menu to bar
		// add menu bar components to frame
		setJMenuBar(bar); //set bar

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items

		//action for exit button
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} 
		
		//actions for open tickets button
		else if (e.getSource() == mnuItemOpenTicket) {

			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name"); //prompt user for their name
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description"); //prompt user for a description of their problem
			LocalDate openDate = LocalDate.now(); //get the current date

			// insert ticket information to database

			//above variables are passed to the method
			int id = dao.insertRecords(ticketName, ticketDesc, openDate); // assign return value of insertRecords to variable

			// display results if successful or not to console / dialog box
			if (id != 0) { //the method should return 0 if a ticket is not created
				System.out.println("Ticket ID : " + id + " created successfully!!!");//print to console ticket was created
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created on " + openDate);//tell user ticket was created
			} else
				System.out.println("Ticket cannot be created!!!");//tell the user teh ticket wasn't created
		}

		//actions for close ticket button
		else if (e.getSource() == mnuItemCloseTicket) {
			try {
				//prompts user for the ID of the ticket they'd like to close
				int tickID = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter ticket ID: "));
				LocalDate close_date = LocalDate.now(); //gtet current date

				//pass variables above to the method
				dao.closeTicket(tickID, close_date);

			} catch (Exception e4) {
				e4.printStackTrace();
			}
		}

		//action for view tickets button
		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		//actions for delete button
		else if (e.getSource() == mnuItemDelete) {
			try {
				//prompt user for ticket id
				int tickID = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter ticket ID:"));
				dao.deleteRecords(tickID); //call method and pass tickID as parameter

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		//actions for update button
		else if (e.getSource() == mnuItemUpdate) {
			try {
				//prompt user for ticket id
				int tickID = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter ticket ID:"));
				// prompt user for their new desctiption for ticket
				String updatedDesc = JOptionPane.showInputDialog(null, "Enter a new ticket description");

				dao.updateRecords(updatedDesc, tickID); //call method and pass the variables
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		}
	}
}
