import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;

import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * @author Ijul
 */

public class SolitaireDisplay extends JComponent implements MouseListener
{
    private static final int CARD_WIDTH = 73;
    private static final int CARD_HEIGHT = 97;
    private static final int SPACING = 5;
    private static final int FACE_UP_OFFSET = 15;
    private static final int FACE_DOWN_OFFSET = 5;
    private int points; 

    private int selectedRow = -1;
    private int selectedCol = -1;
    private final Solitaire game;

    public static void main(String[] args) {
        int xd = 240;
        MysqlDataSource dataSource = new MysqlDataSource();
        String dbURL = "jdbc:mysql://localhost:3306/solitaire";
        String dbUsername = "root";
        String dbPassword = "";
        
        dataSource.setURL(dbURL);
        dataSource.setUser(dbUsername);
        dataSource.setPassword(dbPassword);
    
        JTextField nameField = new JTextField();
        Object[] message = {
            "YAY!! YOU WON!!",
            "Anda mendapatkan skor : " +  xd,
            "Nama Anda :", nameField
        };
    
        int option = JOptionPane.showConfirmDialog(null, message, "Victory!", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                try (Connection connection = dataSource.getConnection();
                    PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO scores (name, score) VALUES (?, ?)");
                    PreparedStatement topScoresStatement = connection.prepareStatement(
                        "SELECT name, score FROM scores ORDER BY score ASC LIMIT 5")) {
    
                    // Insert score
                    insertStatement.setString(1, playerName);
                    insertStatement.setInt(2, xd);
                    insertStatement.executeUpdate();
                    System.out.println("Score saved! Congratulations, " + playerName + "!");
    
                    boolean exitOption = false;
                    while (!exitOption) {
                        // Show leaderboard
                        StringBuilder topScoresMessage = new StringBuilder("Top 5 Scores:\n\n");
                        try (ResultSet rs = topScoresStatement.executeQuery()) {
                            while (rs.next()) {
                                topScoresMessage.append(rs.getString("name"))
                                                .append(" - ")
                                                .append(rs.getInt("score"))
                                                .append("\n");
                            }
                        }
    
                        // Options for Update/Delete
                        String[] options = {"Update", "Delete", "Exit"};
                        int choice = JOptionPane.showOptionDialog(null, 
                            topScoresMessage.toString() + "\nChoose an option:", 
                            "Top 5 Scores", 
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                            null, options, options[0]);
    
                        switch (choice) {
                            case 0: // Update
                                JTextField updateNameField = new JTextField(playerName);
                                Object[] updateMessage = {
                                    "Update your name:", updateNameField
                                };
    
                                int updateOption = JOptionPane.showConfirmDialog(null, updateMessage, "Update Name", JOptionPane.OK_CANCEL_OPTION);
                                if (updateOption == JOptionPane.OK_OPTION) {
                                    String updatedName = updateNameField.getText().trim();
                                    if (!updatedName.isEmpty()) {
                                        try (PreparedStatement updateStatement = connection.prepareStatement(
                                            "UPDATE scores SET name = ? WHERE name = ? AND score = ?")) {
                                            updateStatement.setString(1, updatedName);
                                            updateStatement.setString(2, playerName);
                                            updateStatement.setInt(3, xd);
                                            int rowsUpdated = updateStatement.executeUpdate();
                                            if (rowsUpdated > 0) {
                                                System.out.println("Name updated successfully!");
                                                playerName = updatedName; // Update local variable
                                            } else {
                                                System.out.println("Failed to update name.");
                                            }
                                        }
                                    }
                                }
                                break;
                            case 1: // Delete
                                int confirmDelete = JOptionPane.showConfirmDialog(null, 
                                    "Yakin gamau dimasukin skornya wak??", 
                                    "Alamak 😋", JOptionPane.YES_NO_OPTION);
                                if (confirmDelete == JOptionPane.YES_OPTION) {
                                    try (PreparedStatement deleteStatement = connection.prepareStatement(
                                        "DELETE FROM scores WHERE name = ? AND score = ?")) {
                                        deleteStatement.setString(1, playerName);
                                        deleteStatement.setInt(2, xd);
                                        int rowsDeleted = deleteStatement.executeUpdate();
                                        if (rowsDeleted > 0) {
                                            System.out.println("Score deleted successfully!");
                                        } else {
                                            System.out.println("Failed to delete score.");
                                        }
                                    }
                                }
                                break;
                            case 2: 
                            default:
                                exitOption = true;
                                break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Gagal Koneksi ke database");
                }
            } else {
                System.out.println("Terimakasih Sudah Bermain");
            }
        } else {
            System.out.println("Terimakasih Sudah Bermain");
        }
    }
public SolitaireDisplay(Solitaire game) {
    this.game = game;

    JFrame localFrame = new JFrame("Solitaire");
    localFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    SolitaireDisplay displayComponent = this; // Explicit reference to the current object
    localFrame.getContentPane().add(displayComponent);

    Dimension preferredSize = new Dimension(
        CARD_WIDTH * 7 + SPACING * 8, 
        CARD_HEIGHT * 2 + SPACING * 3 + FACE_DOWN_OFFSET * 7 + 13 * FACE_UP_OFFSET
    );
    displayComponent.setPreferredSize(preferredSize);
    displayComponent.addMouseListener(displayComponent);

    localFrame.pack();
    localFrame.setVisible(true);

    points = 0;
    System.currentTimeMillis();
}


    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(new Color(0, 128, 0));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        for(int i = 0; i<1000; i++)
        {
            System.out.println();
        }
        
        System.out.println(": " + points++);
        

    // if(game.celebrateTime()){
    //     MysqlDataSource dataSource = new MysqlDataSource();
    //     String dbURL = "jdbc:mysql://localhost:3306/solitaire";
    //     String dbUsername = "root";
    //     String dbPassword = "";
        
    //     dataSource.setURL(dbURL);
    //     dataSource.setUser(dbUsername);
    //     dataSource.setPassword(dbPassword);
        
    //     JTextField nameField = new JTextField();
    //     Object[] message = {
    //         "YAY!! YOU WON!!",
    //         "Anda mendapatkan skor : " + points,
    //         "Nama Anda :", nameField
    //     };
        
    //     int option = JOptionPane.showConfirmDialog(null, message, "Victory!", JOptionPane.OK_CANCEL_OPTION);
    //     if (option == JOptionPane.OK_OPTION) {
    //         String playerName = nameField.getText().trim();
    //         if (!playerName.isEmpty()) {
    //             try (Connection connection = dataSource.getConnection();
    //                 PreparedStatement insertStatement = connection.prepareStatement(
    //                     "INSERT INTO scores (name, score) VALUES (?, ?)");
    //                 PreparedStatement topScoresStatement = connection.prepareStatement(
    //                     "SELECT name, score FROM scores ORDER BY score ASC LIMIT 5")) {
        
    //                 // Insert skor player
    //                 insertStatement.setString(1, playerName);
    //                 insertStatement.setInt(2, points);
    //                 insertStatement.executeUpdate();
    //                 System.out.println("Score saved! Congratulations, " + playerName + "!");
        
    //                 // mengambil top 5 player
    //                 StringBuilder topScoresMessage = new StringBuilder("Top 5 Scores:\n\n");
    //                 try (ResultSet rs = topScoresStatement.executeQuery()) {
    //                     while (rs.next()) {
    //                         topScoresMessage.append(rs.getString("name"))
    //                                         .append(" - ")
    //                                         .append(rs.getInt("score"))
    //                                         .append("\n");
    //                     }
    //                 }
        
    //                 // Top 5 Skor
    //                 JOptionPane.showMessageDialog(null, topScoresMessage.toString(), "Top 5 Scores", JOptionPane.INFORMATION_MESSAGE);
    //             } catch (SQLException e) {
    //                 e.printStackTrace();
    //                 System.out.println("Gagal Koneksi ke database");
    //             }
    //         } else {
    //             System.out.println("Terimakasih Sudah Bermain");
    //         }
    //     } else {
    //         System.out.println("Terimakasih Sudah Bermain");
    //     }
    // }
    if (game.celebrateTime()) {
        MysqlDataSource dataSource = new MysqlDataSource();
        String dbURL = "jdbc:mysql://localhost:3306/solitaire";
        String dbUsername = "root";
        String dbPassword = "";
        
        dataSource.setURL(dbURL);
        dataSource.setUser(dbUsername);
        dataSource.setPassword(dbPassword);

        JTextField nameField = new JTextField();
        Object[] message = {
            "YAY!! YOU WON!!",
            "Anda mendapatkan skor : " + points,
            "Nama Anda :", nameField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Victory!", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                try (Connection connection = dataSource.getConnection();
                        PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO scores (name, score) VALUES (?, ?)");
                        PreparedStatement topScoresStatement = connection.prepareStatement(
                        "SELECT name, score FROM scores ORDER BY score ASC LIMIT 5")) {

                    insertStatement.setString(1, playerName);
                    insertStatement.setInt(2, points);
                    insertStatement.executeUpdate();
                    System.out.println("Score saved! Congratulations, " + playerName + "!");

                    boolean exitOption = false;
                    while (!exitOption) {
                        StringBuilder topScoresMessage = new StringBuilder("Top 5 Scores:\n\n");
                        try (ResultSet rs = topScoresStatement.executeQuery()) {
                            while (rs.next()) {
                                topScoresMessage.append(rs.getString("name"))
                                                .append(" - ")
                                                .append(rs.getInt("score"))
                                                .append("\n");
                            }
                        }

                        String[] options = {"Update", "Delete", "Exit"};
                        int choice = JOptionPane.showOptionDialog(null, 
                            topScoresMessage.toString() + "\nChoose an option:", 
                            "Top 5 Scores", 
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
                            null, options, options[0]);

                        switch (choice) {
                            case 0:
                                JTextField updateNameField = new JTextField(playerName);
                                Object[] updateMessage = {
                                    "Ganti nama wak?:", updateNameField
                                };

                                int updateOption = JOptionPane.showConfirmDialog(null, updateMessage, "Update Name", JOptionPane.OK_CANCEL_OPTION);
                                if (updateOption == JOptionPane.OK_OPTION) {
                                    String updatedName = updateNameField.getText().trim();
                                    if (!updatedName.isEmpty()) {
                                        try (PreparedStatement updateStatement = connection.prepareStatement(
                                            "UPDATE scores SET name = ? WHERE name = ? AND score = ?")) {
                                            updateStatement.setString(1, updatedName);
                                            updateStatement.setString(2, playerName);
                                            updateStatement.setInt(3, points);
                                            int rowsUpdated = updateStatement.executeUpdate();
                                            if (rowsUpdated > 0) {
                                                System.out.println("Berhasil diganti");
                                                playerName = updatedName; // Update local variable
                                            } else {
                                                System.out.println("Gagal mengganti");
                                            }
                                        }
                                    }
                                }
                                break;
                            case 1:
                                int confirmDelete = JOptionPane.showConfirmDialog(null, 
                                    "Yakin gamau dimasukin skornya wak??", 
                                    "Delete Score", JOptionPane.YES_NO_OPTION);
                                if (confirmDelete == JOptionPane.YES_OPTION) {
                                    try (PreparedStatement deleteStatement = connection.prepareStatement(
                                        "DELETE FROM scores WHERE name = ? AND score = ?")) {
                                        deleteStatement.setString(1, playerName);
                                        deleteStatement.setInt(2, points);
                                        int rowsDeleted = deleteStatement.executeUpdate();
                                        if (rowsDeleted > 0) {
                                            System.out.println("Data berhasil dihapus!");
                                        } else {
                                            System.out.println("Gagal menghapus data");
                                        }
                                    }
                                }
                                break;
                            case 2:
                            default:
                                exitOption = true;
                                break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Gagal Koneksi ke database");
                }
            } else {
                System.out.println("Terimakasih Sudah Bermain");
            }
        } else {
            System.out.println("Terimakasih Sudah Bermain");
        }
    }
        
        drawCard(g, game.getStockCard(), SPACING, SPACING);

        drawCard(g, game.getWasteCard(), SPACING * 2 + CARD_WIDTH, SPACING);
        if (selectedRow == 0 && selectedCol == 1)
            drawBorder(g, SPACING * 2 + CARD_WIDTH, SPACING);

        for (int i = 0; i < 4; i++)
            drawCard(g, game.getFoundationCard(i), SPACING * (4 + i) + CARD_WIDTH * (3 + i), SPACING);

        for (int i = 0; i < 7; i++)
        {
            Stack<Card> pile = game.getPile(i);
            int offset = 0;
            for (int j = 0; j < pile.size(); j++)
            {
                drawCard(g, pile.get(j), SPACING + (CARD_WIDTH + SPACING) * i, CARD_HEIGHT + 2 * SPACING + offset);
                if (selectedRow == 1 && selectedCol == i && j == pile.size() - 1)
                    drawBorder(g, SPACING + (CARD_WIDTH + SPACING) * i, CARD_HEIGHT + 2 * SPACING + offset);

                if (pile.get(j).mAtas())
                    offset += FACE_UP_OFFSET;
                else
                    offset += FACE_DOWN_OFFSET;
            }
        }
    
    }

    private void drawCard(Graphics g, Card card, int x, int y)
    {
        if (card == null)
        {
            g.setColor(Color.BLACK);
            g.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        }
        else
        {
            String fileName = card.getFileName();
            if (!new File(fileName).exists())
                throw new IllegalArgumentException("bad file name:  " + fileName);
            Image image = new ImageIcon(fileName).getImage();
            g.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        int col = e.getX() / (SPACING + CARD_WIDTH);
        int row = e.getY() / (SPACING + CARD_HEIGHT);
        if (row > 1)
            row = 1;
        if (col > 6)
            col = 6;

        if (row == 0 && col == 0)
            game.stockClicked();
        else if (row == 0 && col == 1)
            game.wasteClicked();
        else if (row == 0 && col >= 3)
            game.foundationClicked(col - 3);
        else if (row == 1)
            game.pileClicked(col);
        repaint();
    }

    private void drawBorder(Graphics g, int x, int y)
    {
        g.setColor(Color.YELLOW);
        g.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        g.drawRect(x + 1, y + 1, CARD_WIDTH - 2, CARD_HEIGHT - 2);
        g.drawRect(x + 2, y + 2, CARD_WIDTH - 4, CARD_HEIGHT - 4);
    }

    public void unselect()
    {
        selectedRow = -1;
        selectedCol = -1;
    }

    public boolean isWasteSelected()
    {
        return selectedRow == 0 && selectedCol == 1;
    }

    public void selectWaste()
    {
        selectedRow = 0;
        selectedCol = 1;
    }

    public boolean isPileSelected()
    {
        return selectedRow == 1;
    }

    public int selectedPile()
    {
        if (selectedRow == 1)
            return selectedCol;
        else
            return -1;
    }

    public void selectPile(int index)
    {
        selectedRow = 1;
        selectedCol = index;
    }
}