/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;

/**
 *
 * @author ASUS
 */
public class AlamatDAO {
    public List<Alamat> getAll() throws SQLException {
        List<Alamat> list = new ArrayList<>();
        String sql = "SELECT * FROM addressbook";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Alamat(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("telepon"),
                        rs.getString("email"),
                        rs.getString("kategori")
                ));
            }
        }
        return list;
    }

    public void add(Alamat a) throws SQLException {
        String sql = "INSERT INTO addressbook (nama, alamat, telepon, email, kategori) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNama());
            ps.setString(2, a.getAlamat());
            ps.setString(3, a.getTelepon());
            ps.setString(4, a.getEmail());
            ps.setString(5, a.getKategori());
            ps.executeUpdate();
        }
    }

    public void update(Alamat a) throws SQLException {
        String sql = "UPDATE addressbook SET nama=?, alamat=?, telepon=?, email=?, kategori=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNama());
            ps.setString(2, a.getAlamat());
            ps.setString(3, a.getTelepon());
            ps.setString(4, a.getEmail());
            ps.setString(5, a.getKategori());
            ps.setInt(6, a.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM addressbook WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Alamat> search(String keyword) throws SQLException {
        List<Alamat> list = new ArrayList<>();
        String sql = "SELECT * FROM addressbook WHERE nama LIKE ? OR telepon LIKE ? OR email LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Alamat(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("telepon"),
                        rs.getString("email"),
                        rs.getString("kategori")
                ));
            }
        }
        return list;
    }
}
