// passNew class
package password;

public class passNew {
    // Instance variables for username and password
    private String userName;
    private String passWord;
    
    // Constructor to initialize username and password
    public passNew(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }
    
    // Getter method for username
    public String getUsername() {
        return userName;
    }
    
    // Getter method for password
    public String getPassword() {
        return passWord;
    }
    
    // Method to print username and password
    public String printInfo() {
       return String.format("%s, %s \n", this.userName, this.passWord);
    }

    // Setter method to update username
    public void setUsername(String usernameNew) {
        this.userName = usernameNew;
    }

    // Setter method to update password
    public void setPassword(String passwordNew) {
        this.passWord = passwordNew;
    }
    
    public String toString(passNew reference) {
    	return reference.userName;
    }
}
