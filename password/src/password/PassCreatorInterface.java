// Import statements
package password;

public interface PassCreatorInterface {
    void addFileAccount(String username, String password);
    void addAccount(String username, String password);
    String getFileName();
    String getProtectedFileName();
    int getAccountCount();
    void remove(passNew reference);
    passNew getAccountByName(String username, String password);
    void printAllAccounts();
}