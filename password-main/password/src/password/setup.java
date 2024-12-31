// setup class
package password;

public class setup {
    // Instance variables for master username and password
    private String masUsername;
    private String masPassword;
    
    // Constructor to initialize master username and password
    public setup(){
        this.masUsername = "0";
        this.masPassword = "0";
    }
    
    // Getter method for master username
    public String getUsername() {
        return this.masUsername;
    }
    
    // Getter method for master password
    public String getPassword() {
        return this.masPassword;
    }
    
    // Setter method to update master username
    public String setUsername(String username) {
        return this.masUsername=username;
    }
    
    // Setter method to update master password
    public String setPassword(String password) {
        return this.masPassword=password;
    }

    // Method to validate user login credentials
    public static boolean login(setup acc,String inputName,String inputPassword) {
        // Check if input username and password match master username and password
        if(!inputName.equals(acc.getUsername()) || !inputPassword.equals(acc.getPassword())) {
            System.out.println("Username or password is invalid.");
            return false;
        }
        
        // If credentials match, login is successful
        System.out.printf("\nLogin successful.\n\n");
        return true;
    }
}
