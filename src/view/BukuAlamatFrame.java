/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;


import controller.AlamatController;
import model.Alamat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class BukuAlamatFrame extends javax.swing.JFrame {

    
    private DefaultTableModel model;
    private AlamatController controller;
    
    /**
     * Creates new form BukuAlamatFrame
     */
    public BukuAlamatFrame() {
        initComponents();
        
         controller = new AlamatController();

        model = new DefaultTableModel(new String[]{
                "No", "Nama", "Alamat", "Telepon", "Email", "Kategori"
        }, 0);
        tblAlamat.setModel(model);
        tblAlamat.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblAlamat.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblAlamat.getColumnModel().getColumn(2).setPreferredWidth(155);
        tblAlamat.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblAlamat.getColumnModel().getColumn(4).setPreferredWidth(200);
        tblAlamat.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblAlamat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        
        loadAlamat();
        
        this.setSize(800, 820); // atur ukuran default jendela
        this.setLocationRelativeTo(null); // tampil di tengah layar
    }

    
     // ---------------------------- LOAD DATA ----------------------------
    private void loadAlamat() {
        try {
            model.setRowCount(0);
            List<Alamat> list = controller.getAll();
            int no = 1;
            for (Alamat a : list) {
                model.addRow(new Object[]{
                        no++, a.getNama(), a.getAlamat(),
                        a.getTelepon(), a.getEmail(), a.getKategori()
                });
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

     // ---------------------------- TAMBAH DATA ----------------------------
    private void addAlamat() {
        String nama = txtNama.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String telepon = txtTelepon.getText().trim();
        String email = txtEmail.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();

       if (nama.isEmpty() || alamat.isEmpty() || telepon.isEmpty() || email.isEmpty() || kategori.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Semua field wajib diisi (Nama, Alamat, Telepon, Email, dan Kategori)!", 
            "Validasi Data", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // ✅ Validasi tambahan (panjang nomor telepon 12–16 digit)
    if (!telepon.matches("\\d{12,16}")) {
        JOptionPane.showMessageDialog(this, 
            "Nomor telepon harus terdiri dari 12 hingga 16 digit angka!", 
            "Validasi Telepon", JOptionPane.WARNING_MESSAGE);
        return;
    }

        try {
            controller.add(nama, alamat, telepon, email, kategori);
            JOptionPane.showMessageDialog(this, "Kontak berhasil ditambahkan!");
            clearFields();
            loadAlamat();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }
    
     // ---------------------------- EDIT DATA ----------------------------
    private void editAlamat() {
        int selectedRow = tblAlamat.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!");
            return;
        }

        int id = getDatabaseIdFromRow(selectedRow);
        String nama = txtNama.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String telepon = txtTelepon.getText().trim();
        String email = txtEmail.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();

         if (nama.isEmpty() || alamat.isEmpty() || telepon.isEmpty() || email.isEmpty() || kategori.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Semua field wajib diisi (Nama, Alamat, Telepon, Email, dan Kategori)!", 
            "Validasi Data", JOptionPane.WARNING_MESSAGE);
        return;
    }

        // ✅ Validasi tambahan (panjang nomor telepon 12–16 digit)
        if (!telepon.matches("\\d{12,16}")) {
        JOptionPane.showMessageDialog(this, 
            "Nomor telepon harus terdiri dari 12 hingga 16 digit angka!", 
            "Validasi Telepon", JOptionPane.WARNING_MESSAGE);
        return;
    }
        try {
            controller.update(id, nama, alamat, telepon, email, kategori);
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
            clearFields();
            loadAlamat();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }
    
    // ---------------------------- HAPUS DATA ----------------------------
    private void deleteAlamat() {
        int selectedRow = tblAlamat.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = getDatabaseIdFromRow(selectedRow);
            try {
                controller.delete(id);
                JOptionPane.showMessageDialog(this, "Alamat berhasil dihapus!");
                clearFields();
                loadAlamat();
            } catch (SQLException e) {
                showError(e.getMessage());
            }
        }
    }
    
        // ---------------------------- CARI DATA ----------------------------
    private void searchAlamat() {
        String keyword = txtPencarian.getText().trim();
        try {
            List<Alamat> list = keyword.isEmpty()
                    ? controller.getAll()
                    : controller.search(keyword);
            model.setRowCount(0);
            int no = 1;
            for (Alamat a : list) {
                model.addRow(new Object[]{
                        no++, a.getNama(), a.getAlamat(),
                        a.getTelepon(), a.getEmail(), a.getKategori()
                });
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }
    
     // ---------------------------- EXPORT CSV ----------------------------
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File CSV");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("No,Nama,Alamat,Telepon,Email,Kategori\n");
                for (int i = 0; i < model.getRowCount(); i++) {
                    writer.write(model.getValueAt(i, 0) + "," +
                            model.getValueAt(i, 1) + "," +
                            model.getValueAt(i, 2) + "," +
                            model.getValueAt(i, 3) + "," +
                            model.getValueAt(i, 4) + "," +
                            model.getValueAt(i, 5) + "\n");
                }
                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke: " + file.getAbsolutePath());
            } catch (IOException e) {
                showError(e.getMessage());
            }
        }
    }

    // ---------------------------- IMPORT CSV ----------------------------
    private void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih File CSV");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine(); // skip header
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 6) {
                        controller.add(data[1], data[2], data[3], data[4], data[5]);
                    }
                }
                JOptionPane.showMessageDialog(this, "Data berhasil diimpor!");
                loadAlamat();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    // ---------------------------- UTILITAS ----------------------------
    private void clearFields() {
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        txtEmail.setText("");
        cmbKategori.setSelectedIndex(0);
        cmbFilter.setSelectedIndex(0);
    }
    
    
     // fungsi bantu untuk mendapatkan id database dari nomor urut
    private int getDatabaseIdFromRow(int rowIndex) {
        try {
            List<Alamat> list = controller.getAll();
            return list.get(rowIndex).getId();
        } catch (SQLException e) {
            return -1;
        }
    }
    
    
    
   private void filterByKategori() {
    String kategori = (String) cmbFilter.getSelectedItem();

    try {
        List<Alamat> list;
        if (kategori.equals("Semua")) {
            list = controller.getAll();
        } else {
            list = controller.filterByKategori(kategori);
        }

        model.setRowCount(0);
        int no = 1;
        for (Alamat a : list) {
            model.addRow(new Object[]{
                no++, a.getNama(), a.getAlamat(),
                a.getTelepon(), a.getEmail(), a.getKategori()
            });
        }
    } catch (SQLException e) {
        showError(e.getMessage());
    }
}
    
    
    
    
    
    
    
    
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblNama = new javax.swing.JLabel();
        lblAlamat = new javax.swing.JLabel();
        lblTelepon = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblKategori = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtAlamat = new javax.swing.JTextField();
        txtTelepon = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        lblCari = new javax.swing.JLabel();
        txtPencarian = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlamat = new javax.swing.JTable();
        btnKeluar = new javax.swing.JButton();
        lblFilter = new javax.swing.JLabel();
        cmbFilter = new javax.swing.JComboBox<>();
        btnReset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aplikasi Buku Alamat");

        jPanel1.setBackground(new java.awt.Color(49, 49, 49));
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15), javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), "Aplikasi Buku Alamat", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Poppins Black", 0, 14), new java.awt.Color(255, 255, 255)))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 800));

        lblNama.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblNama.setForeground(new java.awt.Color(255, 255, 255));
        lblNama.setText("Nama : ");

        lblAlamat.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblAlamat.setForeground(new java.awt.Color(255, 255, 255));
        lblAlamat.setText("Alamat :");

        lblTelepon.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblTelepon.setForeground(new java.awt.Color(255, 255, 255));
        lblTelepon.setText("Telepon :");

        lblEmail.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(255, 255, 255));
        lblEmail.setText("Email : ");

        lblKategori.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblKategori.setForeground(new java.awt.Color(255, 255, 255));
        lblKategori.setText("Kategori :");

        txtNama.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        txtAlamat.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        txtTelepon.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        txtTelepon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTeleponKeyTyped(evt);
            }
        });

        txtEmail.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        cmbKategori.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Teman", "Keluarga", "Kantor" }));

        btnTambah.setBackground(new java.awt.Color(49, 49, 49));
        btnTambah.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setBackground(new java.awt.Color(49, 49, 49));
        btnEdit.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(49, 49, 49));
        btnHapus.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(49, 49, 49));
        btnExport.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnExport.setForeground(new java.awt.Color(255, 255, 255));
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnImport.setBackground(new java.awt.Color(49, 49, 49));
        btnImport.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnImport.setForeground(new java.awt.Color(255, 255, 255));
        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        lblCari.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblCari.setForeground(new java.awt.Color(255, 255, 255));
        lblCari.setText("Cari :");

        txtPencarian.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPencarianKeyTyped(evt);
            }
        });

        tblAlamat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblAlamat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAlamatMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblAlamat);

        btnKeluar.setBackground(new java.awt.Color(49, 49, 49));
        btnKeluar.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnKeluar.setForeground(new java.awt.Color(255, 255, 255));
        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

        lblFilter.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblFilter.setForeground(new java.awt.Color(255, 255, 255));
        lblFilter.setText("Filter : ");

        cmbFilter.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Teman", "Keluarga", "Kantor" }));
        cmbFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterActionPerformed(evt);
            }
        });

        btnReset.setBackground(new java.awt.Color(49, 49, 49));
        btnReset.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnReset.setForeground(new java.awt.Color(255, 255, 255));
        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 166, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblNama)
                                            .addComponent(lblAlamat))
                                        .addGap(21, 21, 21)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNama)
                                            .addComponent(txtAlamat)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblTelepon)
                                            .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblKategori)
                                        .addGap(18, 18, 18)
                                        .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(151, 151, 151))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCari, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnExport)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnImport)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnKeluar))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnTambah)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnEdit)
                                        .addGap(17, 17, 17)
                                        .addComponent(btnHapus)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnReset))
                                    .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNama)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lblAlamat))
                    .addComponent(txtAlamat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTelepon)
                    .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKategori)
                    .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnHapus)
                    .addComponent(btnEdit)
                    .addComponent(btnReset))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCari)
                    .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFilter)
                    .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExport)
                    .addComponent(btnImport)
                    .addComponent(btnKeluar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editAlamat();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        addAlamat();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteAlamat();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        exportToCSV();
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        importFromCSV();
    }//GEN-LAST:event_btnImportActionPerformed

    private void txtPencarianKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPencarianKeyTyped
        searchAlamat();
    }//GEN-LAST:event_txtPencarianKeyTyped

    private void tblAlamatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAlamatMouseClicked
       int row = tblAlamat.getSelectedRow();
                if (row != -1) {
                    txtNama.setText(model.getValueAt(row, 1).toString());
                    txtAlamat.setText(model.getValueAt(row, 2).toString());
                    txtTelepon.setText(model.getValueAt(row, 3).toString());
                    txtEmail.setText(model.getValueAt(row, 4).toString());
                    cmbKategori.setSelectedItem(model.getValueAt(row, 5).toString());
                }
    }//GEN-LAST:event_tblAlamatMouseClicked

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void cmbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterActionPerformed
        filterByKategori();
    }//GEN-LAST:event_cmbFilterActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        clearFields();
    }//GEN-LAST:event_btnResetActionPerformed

    private void txtTeleponKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTeleponKeyTyped
       char c = evt.getKeyChar();
        // Hanya boleh angka dan maksimal 16 karakter
        if (!Character.isDigit(c) || txtTelepon.getText().length() >= 16) {
            evt.consume(); // batalkan input
        }
    
    }//GEN-LAST:event_txtTeleponKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BukuAlamatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BukuAlamatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BukuAlamatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BukuAlamatFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BukuAlamatFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbFilter;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAlamat;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblKategori;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblTelepon;
    private javax.swing.JTable tblAlamat;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtPencarian;
    private javax.swing.JTextField txtTelepon;
    // End of variables declaration//GEN-END:variables
}
