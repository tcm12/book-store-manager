package view;

import controller.GuiController;
import controller.UserController;
import model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class UserPage extends JFrame {
    private JPanel userPanel;
    private JTextField titleTextField;
    private JTextField genreTextField;
    private JTextField authorTextField;
    private JButton searchButton;
    private JButton sellButton;
    private JTable bookTable;
    private JTextField amountTextField;
    private JButton logOutButton;

    private final UserController userController;
    private final GuiController guiController;

    public UserPage() {
        super("User Page");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(userPanel);
        this.pack();

        guiController = new GuiController();
        userController = new UserController();

        guiController.setUserPage(this);


        updateBookData(userController.getBookList());

        activateActionListeners();
    }

    public void updateBookData(List<Book> bookList) {

        DefaultTableModel modelBook = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        modelBook.addColumn("ID");
        modelBook.addColumn("Title");
        modelBook.addColumn("Author");
        modelBook.addColumn("Genre");
        modelBook.addColumn("Quantity");
        modelBook.addColumn("Price");

        for (Book tempBook : bookList) {
            modelBook.addRow(new Object[]{tempBook.getId(), tempBook.getTitle(), tempBook.getAuthor(), tempBook.getGenre(), tempBook.getQuantity(), tempBook.getPrice()});
        }

        bookTable.setModel(modelBook);

    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private boolean failedToExecute(int errorCode) {
        switch (errorCode) {
            case 1:
                showErrorMessage("Amount must be a positive number!");
                return true;
            case 2:
                showErrorMessage("Cant sell more books than the amount we have!");
                return true;
            case 3:
                showErrorMessage("Book not found!");
                return true;
            case 4:
                showErrorMessage("Amount must be a number!");
                return true;
            default:
                return false;
        }
    }

    private void emptyTextFields() {
        titleTextField.setText("");
        authorTextField.setText("");
        genreTextField.setText("");
        amountTextField.setText("");
    }

    private void logOut(){
        guiController.closeAllPages();
        LoginPage loginPage = new LoginPage();
    }

    private void searchBook(){
        String title = titleTextField.getText();
        String author = authorTextField.getText();
        String genre = genreTextField.getText();

        emptyTextFields();
        updateBookData(userController.filteredList(title, author, genre));
    }

    private void sellBook(){
        int id;

        try {
            id = (int) bookTable.getModel().getValueAt(bookTable.getSelectedRow(), 0);
        } catch (ArrayIndexOutOfBoundsException e1) {
            showErrorMessage("Please select a book to sell!");
            return;
        }

        if (failedToExecute(userController.sellBook(id, amountTextField.getText()))) {
            return;
        }

        emptyTextFields();
        updateBookData(userController.getBookList());
    }

    public void activateActionListeners() {
        logOutButton.addActionListener(e -> { logOut(); });

        searchButton.addActionListener(e -> { searchBook(); });

        sellButton.addActionListener(e -> { sellBook(); });
    }

}
