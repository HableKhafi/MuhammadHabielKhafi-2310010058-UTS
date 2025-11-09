/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Alamat;
import model.AlamatDAO;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class AlamatController {
    private AlamatDAO dao;

    public AlamatController() {
        dao = new AlamatDAO();
    }

    public List<Alamat> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(String nama, String alamat, String telepon, String email, String kategori) throws SQLException {
        dao.add(new Alamat(0, nama, alamat, telepon, email, kategori));
    }

    public void update(int id, String nama, String alamat, String telepon, String email, String kategori) throws SQLException {
        dao.update(new Alamat(id, nama, alamat, telepon, email, kategori));
    }

    public void delete(int id) throws SQLException {
        dao.delete(id);
    }

    public List<Alamat> search(String keyword) throws SQLException {
        return dao.search(keyword);
    }
    
    public List<Alamat> filterByKategori(String kategori) throws SQLException {
        return dao.filterByKategori(kategori);
}
}
