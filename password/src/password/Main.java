package password;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// Main class
public class Main implements ActionListener{
    // Scanner object for user input
    private static Scanner scan = new Scanner(System.in);
    // Object for setup credentials
    private static setup naurnaur = new setup();
    // Constants for file names
    private static final String DATA_FILE = ".dat";
    private static final String ENCRYPTED_FILE = "encrypted";
    // Flag to indicate if it's a master login
    private static boolean master;
    // String to check username and password
    private static String check = "";
    // Default input username and password
    private static String inputName = "username";
    private static String inputPassword = "password";
    
    private static JLabel labelUser;
	private static JLabel labelPass;
	private static JButton newAccButton;
	private static JFrame frame;
	private static JPanel panel;
	private static JTextField userText;
	private static JPasswordField passText;
	private static JButton loginButton;
	private static JLabel successLabel;
	private static JButton viewPasswords;
	private static JButton addPasswords;
	private static JButton editPasswords;
	private static JButton removePasswords;
	private static JButton ExitProgram;
	private static JButton back;
	private static JButton backLogin;
	private static  DefaultListModel<String> model = new DefaultListModel<>();     
    JList<String> list;
    
    // Object for handling user account operations
    public static passCreator account = new passCreator();
    public static boolean access = false;
    
    public static String inName = "";
    public static String outName = "";


    // Main method
    public static void main(String[] args) throws Exception{
        new Main();
        
        // Loop for account creation or login
        while(!access) {
            System.out.printf("Press Y for a new account\nPress N to log in\nPress T to launch test cases\n");
            String choice = scan.next();
            if(choice.equals("Y")) {
                createAccount();
            }else if(choice.equals("N")) {
                access = login();
            }else if(choice.equals("T")) {
            	 try {
                     PassCreatorInterface passCreator = new passCreator("temp.dat");
                     
                     passCreator.addAccount("user1", "password1");
                     passCreator.addAccount("user2", "password2");
                     
                     System.out.printf("Sample data:\n");
                     
                     passCreator.printAllAccounts();
                     
                     passNew foundAccount = passCreator.getAccountByName("user1", "password1");
                     if (foundAccount != null) {
                         System.out.println("Found Account: " + foundAccount.getUsername());
                     } else {
                         System.out.println("Account not found.");
                     }
                     
                     passCreator.remove(foundAccount);
                     
                     passCreator.printAllAccounts();
                     clearFile("temp.dat");
                     System.exit(0);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
            }else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
        
        // Main menu
        if(access) {
            int choice= -1;
            System.out.printf("\nopening user files..\n");
            String inName = "";
            String outName = "";
            if(master) {
                inName = account.getProtectedFileName();
                outName = account.getFileName();
            }else {
                inName = ENCRYPTED_FILE + check + DATA_FILE;
                outName = check + DATA_FILE;
            }
            decryptFile(inName, outName, inputPassword);
            clearFile(inName);
            account = openDataFile(outName);
            System.out.print("files opened successfully.\n");
            
            // Loop for user operations
            while(choice != 5) {
                displayMenu();
                choice = scan.nextInt();
                
                // View passwords
                if(choice == 1){
                    account.printAllAccounts();
                    System.out.printf("\nAccount count: %d\n\n",account.getAccountCount() );
                // Add new password
                }else if(choice == 2) {
                    System.out.print("Enter your new username: ");
                    String name = scan.next();
                    System.out.print("Enter your new password: ");
                    String password = scan.next();
                    account.addAccount(name, password);
                // Edit password
                }else if(choice == 3) {
                    System.out.print("Enter the username to edit: ");
                    String user = scan.next();
                    System.out.print("Enter the password to edit: ");
                    String password = scan.next();
                    passNew reference = account.getAccountByName(user, password);
                    if(reference != null) {
                        editPassword(outName, "temp.dat" , password, user, reference);
                    }
                // Remove password
                }else if(choice == 4) {
                    System.out.print("Enter the username to remove: ");
                    String user = scan.next();
                    System.out.print("Enter the password to remove: ");
                    String password = scan.next();
                    passNew reference = account.getAccountByName(user, password);
                    if(reference != null) {
                        account.remove(reference);
                        removePassword(outName, "temp.dat" , password, user);
                    }
                // Exit
                }else if(choice == 5) {
                    encryptFile(outName, inName, inputPassword);
                    clearFile(outName);
                    System.out.printf("\nExiting...\n");
                }else {
                    System.exit(2);
                }
            }
        }
    }

    // Method to edit password
    private static void editPassword(String outFile, String inFile, String pass, String user, passNew reference) throws FileNotFoundException {
        try (Scanner in = new Scanner(new File(outFile));
             PrintWriter out = new PrintWriter(inFile)) {
            while(in.hasNextLine()) {
                String line = in.nextLine();
                if(line.equals(user + " " +  pass)) {
                    System.out.printf("\nEnter your new username: ");
                    String username = scan.next();
                    System.out.print("Enter your new password: ");
                    String password = scan.next();
                    line = username + " " + password;
                    
                    reference.setUsername(username);
                    reference.setPassword(password);
                }
                out.println(line);
            }
            
            in.close();
            out.close();
            
            
            clearFile(outFile);
            copyFile(inFile,outFile);
            clearFile(inFile);
        }
        catch(Exception error) {
            System.out.println("Error while editing password." + error.getMessage());
        }
    }
    
    // Method to remove password
    private static void removePassword(String outFile, String inFile, String pass, String user) throws FileNotFoundException {
        try (Scanner in = new Scanner(new File(outFile));
             PrintWriter out = new PrintWriter(inFile)) {
            while(in.hasNextLine()) {
                String line = in.nextLine();
                if(line.equals(user + " " +  pass)) {
                    out.println();
                }else {
                    out.println(line);
                }
            }
            
            in.close();
            out.close();
            
            
            clearFile(outFile);
            copyFile(inFile,outFile);
            clearFile(inFile);
        }
        catch(Exception error) {
            System.out.println("Error while removing password." + error.getMessage());
        }
    }
    
    // Method to copy file
    private static void copyFile(String inFile, String outFile)throws IOException{
        try (Scanner in = new Scanner(new File(inFile));
             PrintWriter out = new PrintWriter(outFile)) {
                while (in.hasNextLine()) {
                    out.println(in.nextLine());
                }
            }catch (IOException e) {
                System.out.println("Error while copying file: " + e.getMessage());
            }
    }
    
    // Method to create new account
    private static void createAccount() throws Exception {
    	
        System.out.print("Enter your username: ");
        String name = scan.next();
        
        System.out.print("Enter your password\n(must be 16 or less characters):\n ");
        String password = scan.next();
        
        if (password.length() > 16){
        	while(password.length() > 16){
        		System.out.print("Password length is longer than 16 characters.\nEnter a new password:\n");
        		password = scan.next();
        	}
    	}
        
        String encryptedFileName = encryptFileName(password,name)+DATA_FILE;
        
        if(fileExists(encryptedFileName)) {
            System.out.println("Username taken, please use a new name");
        }else{
            naurnaur.setUsername(name);
            naurnaur.setPassword(password);
            createUserFile(encryptFileName(password,name));
            createUserFile(ENCRYPTED_FILE + encryptFileName(password,name));
        }
    }
    
    // Method for user login
    private static boolean login() throws Exception{
        System.out.printf("\nEnter your username: ");
        inputName = scan.next();
        
        System.out.print("Enter your password: ");
        inputPassword = scan.next();
        check = encryptFileName(inputPassword,inputName);
        master = inputName.equals("0") && inputPassword.equals("0");

        if(fileExists(check + DATA_FILE) || master) {
            naurnaur.setUsername(inputName);
            naurnaur.setPassword(inputPassword);
            return true;
        }else {
            System.out.printf("\nUsername or password is incorrect.\n\n");
            return false;
        }
    }
        
    // Method to display main menu
    
    private static void displayMenu() {
        System.out.println();
        System.out.println("Welcome, " + naurnaur.getUsername() + ".");
        System.out.printf ("\n=======Options=======\n");
        System.out.println("1:  View Passwords  ");
        System.out.println("2:   Add Password   ");
        System.out.println("3:  Edit Password  ");
        System.out.println("4:  Remove Password  ");
        System.out.println("5:   Exit Program   ");
    }
    
    
    // Method to open data file
    private static passCreator openDataFile(String fileName) throws IOException {
        return new passCreator(fileName);
      }
    
    // Method to clear file
    private static void clearFile(String fileName){
        try {
        FileWriter out = new FileWriter(fileName, false);
        out.close();
        }catch(IOException error){
            System.out.println("Error while clearing the file: " + error.getMessage());
        }
    }
    
    // Method to encrypt file
    private static void encryptFile(String inFile, String outFile, String key)throws FileNotFoundException {
        String add = "0000000000000000";
        if(key.length() < 16) {
            key += add.substring(0,16 - key.length());
        }
         try (Scanner in = new Scanner(new File(inFile));
                 PrintWriter out = new PrintWriter(outFile)) {
             
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),new IvParameterSpec("aaaabbbbccccdddd".getBytes("ASCII")));
            
            while(in.hasNextLine()) {
                String to = in.nextLine();
                byte[] gibb = cipher.doFinal(to.getBytes(StandardCharsets.UTF_8));
                String encryptedBase64 = Base64.getEncoder().encodeToString(gibb);
                out.println(encryptedBase64);
                }
            in.close();
            out.close();
         }catch(Exception error) {
            System.out.println("Error while encrypting file." + error.getMessage());
        }
    }
    
    // Method to decrypt file
    private static void decryptFile(String inFile, String outFile, String key)throws FileNotFoundException {
        String add = "0000000000000000";
        if(key.length() < 16) {
            key += add.substring(0,16 - key.length());
        }
        
         try (Scanner in = new Scanner(new File(inFile));
                 PrintWriter out = new PrintWriter(outFile)) {
             
             
             Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
             cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("aaaabbbbccccdddd".getBytes("ASCII")));
                 
        while(in.hasNextLine()) {    
            String to = in.nextLine();
            byte[] bbig = Base64.getDecoder().decode(to);
            byte[] decrypted = cipher.doFinal(bbig);
            String decryptedBase64 = new String(decrypted, StandardCharsets.UTF_8);
            out.println(decryptedBase64);
            
        }
        //System.out.println("decrypted");
        in.close();
        out.close();
        }catch(Exception error) {
            System.out.println("Error while decrypting file.Exiting...\n" + error.getMessage());
            error.printStackTrace();
            System.exit(2);
        }
    }
     
    // Method to create user file
    static boolean createUserFile(String name)throws IOException{
         boolean works;
        File f = new File(name + DATA_FILE);
            works = f.createNewFile();
        if (works) {
                System.out.println("File created successfully.");
                return works;
            } else {
                System.out.println("File creation failed. Account creation failed");
                return works;
            }
     }
     
    // Method to check if file exists
    public static boolean fileExists(String fileName){
         File f = new File(fileName);
         return f.exists();
     }
     
    // Method to check if file is empty
    public static boolean fileEmpty(String fileName) {
         File f = new File(fileName + DATA_FILE);
         return f.length() == 0;
     }
     
    // Method to encrypt file name
     public static String encryptFileName(String key, String text) throws Exception {
         String add = "0000000000000000";
         if(key.length() < 16) {
             key += add.substring(0,16 - key.length());
         }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("aaaabbbbccccdddd".getBytes("ASCII")));
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
            return encryptedBase64.replace("/","_");
        }
     
     public Main() {
         frame = new JFrame();
         panel = new JPanel();
         
         frame.setSize(450, 200);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.add(panel);

         panel.setLayout(null);

         labelUser = new JLabel("User");
         labelUser.setBounds(20, 20, 80, 25);
         panel.add(labelUser);

         userText = new JTextField();
         userText.setBounds(100, 20, 310, 25);
         panel.add(userText);

         labelPass = new JLabel("Password");
         labelPass.setBounds(20, 60, 80, 25);
         panel.add(labelPass);

         passText = new JPasswordField();
         passText.setBounds(100, 60, 310, 25);
         panel.add(passText);

         loginButton = new JButton("Login");
         loginButton.setBounds(20, 100, 80, 25);
         loginButton.addActionListener(this);
         panel.add(loginButton);

         successLabel = new JLabel("");
         successLabel.setBounds(29, 140, 310, 25);
         panel.add(successLabel);
         
         newAccButton = new JButton("Create Account");
         newAccButton.setBounds(120, 100, 150, 25);
         newAccButton.addActionListener(this);
         panel.add(newAccButton);

         frame.setVisible(true);
     }
     
     @Override
 	public void actionPerformed(ActionEvent e){
    	 if(e.getSource() == loginButton) {
	 		try {
				access = loginGUI();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	 		if(access){
			 	clearPanel();
			 	displayMenuGUI();
			 	addPasswordsToList();
	 		}
    	 }else if(e.getSource() == newAccButton) {
    		 newAccountGUI();
    	 }else if(e.getSource() == viewPasswords){
    	  	viewPasswords();
    	  }else if(e.getSource() == addPasswords){
    	  	addPasswords();
    	  }else if(e.getSource() == editPasswords){
    	  	try {
				editPasswords();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
    	  }else if(e.getSource() == removePasswords){
    	  	try {
				removePasswords();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
    	  }else if(e.getSource() == back || e.getSource() == backLogin) {
    		  if(e.getSource() == back) {
    		  back();
    		  }else if(e.getSource() == backLogin) {
    			try {
					backLogin();
					panel.revalidate();
			        panel.repaint();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		  }
    	  }else if(e.getSource() == ExitProgram){
    	  	panel.setVisible(false);
    	  	frame.setVisible(false);
    	  	try {
				encryptFile(outName, inName, inputPassword);
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}
            clearFile(outName);
            System.out.printf("\nExiting...\n");
    	  	System.exit(2);
    	  }	 
    	 
	 }
     
     @SuppressWarnings("deprecation")
	private boolean loginGUI() throws Exception{
    	 	boolean access = false;
    	 	inputName = userText.getText();
	 		//@SuppressWarnings("deprecation")
	 		inputPassword = passText.getText();
	 		System.out.println(inputName + ", " + inputPassword);
	 		
	 		check = encryptFileName(inputPassword,inputName);
	        master = inputName.equals("0") && inputPassword.equals("0");

	        if(fileExists(check + DATA_FILE) || master) {
	        	successLabel.setText("Login successful");
					access = true;
	            naurnaur.setUsername(inputName);
	            naurnaur.setPassword(inputPassword);
	            if(access) {
		              System.out.printf("\nopening user files..\n");
		              if(master) {
		            	 System.out.printf("\nopening MASTER..\n");
		                  inName = account.getProtectedFileName();
		                  outName = account.getFileName();
		              }else {
		                  inName = ENCRYPTED_FILE + check + DATA_FILE;
		                  outName = check + DATA_FILE;
		              }
		              decryptFile(inName, outName, inputPassword);
		              clearFile(inName);
		              account = openDataFile(outName);
		              System.out.print("files opened successfully.\n");
		           }
			}else{
					successLabel.setText("Incorrect username or password.");
			}
	    return access;
     }
     
     private void newAccountGUI(){
    	 	panel.setVisible(false);
		 	clearPanel();
		 	panel.setVisible(true);

		    labelUser = new JLabel("New User");
		    labelUser.setBounds(20, 20, 80, 25);
		    panel.add(labelUser);

		    userText = new JTextField();
		    userText.setBounds(120, 20, 310, 25);
		    panel.add(userText);

		    labelPass = new JLabel("New Password");
		    labelPass.setBounds(20, 60, 100, 25);
		    panel.add(labelPass);

		    passText = new JPasswordField();
		    passText.setBounds(120, 60, 310, 25);
		    panel.add(passText);
		    
		    successLabel = new JLabel("");
		    successLabel.setBounds(29, 140, 310, 25);
		    panel.add(successLabel);
		    
		    backLogin = new JButton("Back");
    	    backLogin.setBounds(120, 100, 150, 25);
    	    backLogin.addActionListener(this);

		    JButton createNewButton = new JButton("Create");
		    createNewButton.setBounds(20, 100, 80, 25);
		    createNewButton.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent event) {
		            // Handle new account creation logic here
		            String newUser = userText.getText();
		            String newPassword = new String(passText.getPassword());
		            
		            try {
						createAccountFilesGUI(newUser,newPassword);
					}catch (Exception e) {
						e.printStackTrace();
					}
		            }
		        }
		    );
		    panel.add(createNewButton);
		    panel.add(backLogin);
		    panel.revalidate();
	        panel.repaint();
     }
     
     // Method to create new account
     private static void createAccountFilesGUI(String name, String password) throws Exception {
     	
         
         if (password.length() > 16){
         	while(password.length() > 16){
         		System.out.print("Password length is longer than 16 characters.\nEnter a new password:\n");
         		password = new String(passText.getPassword());
         	}
     	}
         
         String encryptedFileName = encryptFileName(password,name);
         
         if(fileExists(encryptedFileName + DATA_FILE)) {
             System.out.println("Username taken, please use a new name");
             successLabel.setText("Username taken, please use a new name");
         }else{
             naurnaur.setUsername(name);
             naurnaur.setPassword(password);
             createUserFile(encryptFileName(password,name));
             createUserFile(ENCRYPTED_FILE + encryptFileName(password,name));
             System.out.println("New account created: " + name + ", " + password);
	         successLabel.setText("Account created successfully!");
         }
     }
     
     private void displayMenuGUI() {
    	 frame.setSize(260, 330);
    	 JLabel labelUser = new JLabel("Welcome, " + naurnaur.getUsername());
    	    labelUser.setBounds(20, 20, 400, 50);
    	    labelUser.setFont(new Font("Calibri", Font.BOLD, 24));
    	    panel.add(labelUser);
    	    
    	    viewPasswords = new JButton("View Passwords");
    	    viewPasswords.setBounds(20, 80, 200, 25);
    	    viewPasswords.addActionListener(this);
    	    panel.add(viewPasswords);

    	    addPasswords = new JButton("Add Password");
    	    addPasswords.setBounds(20, 120, 200, 25);
    	    addPasswords.addActionListener(this);
    	    panel.add(addPasswords);

    	    editPasswords = new JButton("Edit Password");
    	    editPasswords.setBounds(20, 160, 200, 25);
    	    editPasswords.addActionListener(this);
    	    panel.add(editPasswords);

    	    removePasswords = new JButton("Remove Password");
    	    removePasswords.setBounds(20, 200, 200, 25);
    	    removePasswords.addActionListener(this);
    	    panel.add(removePasswords);

    	    ExitProgram = new JButton("Exit Program");
    	    ExitProgram.setBounds(20, 240, 200, 25);
    	    ExitProgram.addActionListener(this);
    	    panel.add(ExitProgram);

    	    panel.revalidate();
    	    panel.repaint();
     }
     
     public void redisplayLoginGUI(){
     labelUser = new JLabel("User");
     labelUser.setBounds(20, 20, 80, 25);
     panel.add(labelUser);

     userText = new JTextField();
     userText.setBounds(100, 20, 310, 25);
     panel.add(userText);

     labelPass = new JLabel("Password");
     labelPass.setBounds(20, 60, 80, 25);
     panel.add(labelPass);

     passText = new JPasswordField();
     passText.setBounds(100, 60, 310, 25);
     panel.add(passText);

     loginButton = new JButton("Login");
     loginButton.setBounds(20, 100, 80, 25);
     loginButton.addActionListener(this);
     panel.add(loginButton);

     successLabel = new JLabel("");
     successLabel.setBounds(29, 140, 310, 25);
     panel.add(successLabel);
     
     newAccButton = new JButton("Create Account");
     newAccButton.setBounds(120, 100, 150, 25);
     newAccButton.addActionListener(this);
     panel.add(newAccButton);
     
     panel.revalidate();
	 panel.repaint();
     }
     
     @SuppressWarnings("unused")
	private void clearFrame(){
    	 frame.getContentPane().removeAll();
    	 frame.revalidate();
    	 frame.repaint();
     }
     
     private void clearPanel() {
    	 panel.removeAll();
    	 panel.revalidate();
    	 panel.repaint();
     }
     
     private void viewPasswords() {
    	 	clearPanel();
    	    JList<String> passwordList = new JList<>(model);
    	    passwordList.setBounds(20, 20, 200, 200);
    	    panel.add(passwordList);
    	    back = new JButton("Back");
    	    back.setBounds(20, 240, 200, 25);
    	    back.addActionListener(this);
    	    panel.add(back);
    	    panel.revalidate();
    	    panel.repaint();
     }
     
     private void editPasswords() throws FileNotFoundException {
    	 String username = JOptionPane.showInputDialog("Enter the username to edit:");
    	    String password = JOptionPane.showInputDialog("Enter the current password:");

    	    passNew reference = account.getAccountByName(username, password);
    	    if (reference != null) {
    	        String newUsername = JOptionPane.showInputDialog("Enter the new username:",username);
    	        String newPassword = JOptionPane.showInputDialog("Enter the new password:",password);
    	        editPasswordGUI(outName, "temp.dat", username, password, newUsername, newPassword, reference);
    	        refreshModel();
    	    } else {
    	        JOptionPane.showMessageDialog(panel, "Account not found.");
    	    }
     }
     
     private static void editPasswordGUI(String outFile, String inFile, String user, String pass, String newUser, String newPass, passNew reference) throws FileNotFoundException {
         try (Scanner in = new Scanner(new File(outFile));
              PrintWriter out = new PrintWriter(inFile)) {
             while(in.hasNextLine()) {
                 String line = in.nextLine();
                 if(line.equals(user + " " +  pass)) {
                     line = newUser + " " + newPass;
                     reference.setUsername(newUser);
         	         reference.setPassword(newPass);
                 }
                 out.println(line);
             }
             
             in.close();
             out.close();
             
             
             clearFile(outFile);
             copyFile(inFile,outFile);
             clearFile(inFile);
         }
         catch(Exception error) {
             System.out.println("Error while editing password." + error.getMessage());
         }
     }
     
     private void refreshModel() {
    	 model.clear();
    	    for (passNew account : account.getList()) {
    	        model.addElement(account.printInfo());
    	    }
	}

	private void removePasswords() throws FileNotFoundException {
		String username = JOptionPane.showInputDialog("Enter the username to remove:");
	    String password = JOptionPane.showInputDialog("Enter the password to remove:");

	    passNew accountToRemove = account.getAccountByName(username, password);
	    if (accountToRemove != null) {
	        account.remove(accountToRemove);
	        removePassword(outName, "temp.dat" , password, username);
	        refreshModel();
	    } else {
	        JOptionPane.showMessageDialog(panel, "Account not found.");
	    }
     }
     
     private void addPasswords() {
    	 String username = JOptionPane.showInputDialog("Enter your new username:");
    	    String password = JOptionPane.showInputDialog("Enter your new password:");

    	    if (username != null && password != null) {
    	        account.addAccount(username, password);
    	        model.addElement(username + " : " + password);
    	    }
     }
     
     private void back(){
    	 clearPanel();
    	 displayMenuGUI();
     }
     
     private void backLogin() throws IOException{
    	 clearPanel();
    	 redisplayLoginGUI();
     }
     
     private void addPasswordsToList() {
    	 ArrayList<passNew> accountList = account.getList();
    	 for(int i = 0; i<accountList.size(); ++i) {
    		 model.addElement(accountList.get(i).printInfo());
    	 }
     }
     
}