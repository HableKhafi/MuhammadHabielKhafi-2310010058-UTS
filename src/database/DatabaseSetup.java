/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 *
 * @author ASUS
 */
public class DatabaseSetup {
    
    public static void main(String[] args) {
        String sql = "CREATE TABLE IF NOT EXISTS addressbook ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nama TEXT NOT NULL,"
                + "alamat TEXT,"
                + "telepon TEXT NOT NULL,"
                + "email TEXT,"
                + "kategori TEXT"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel 'addressbook' berhasil dibuat atau sudah ada.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    
}
