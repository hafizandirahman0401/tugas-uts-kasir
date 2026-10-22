package com.kasir.gui;

import com.kasir.model.*;
import com.kasir.service.IKasirService;
import com.kasir.util.FormatRupiah;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * =========================================================================
 * GUI WINDOW (JFRAME) - ANTARMUKA GRAFIS DESKTOP KASIR
 * =========================================================================
 * Membuka aplikasi kasir dalam bentuk jendela program visual (Desktop GUI)
 * sehingga pengguna tidak perlu lagi berinteraksi via terminal console.
 */
public class KasirFrame extends JFrame {

    private final IKasirService kasirService;
    private Transaksi transaksiAktif;

    // Komponen Tab POS (Kasir)
    private JTable tabelKatalogPOS;
    private DefaultTableModel modelKatalogPOS;
    private JTable tabelKeranjang;
    private DefaultTableModel modelKeranjang;
    private JSpinner spinnerQty;
    private JLabel lblSubtotal;
    private JLabel lblTotalDiskon;
    private JLabel lblTotalAkhir;
    private JTextField txtVoucher;
    private JTextField txtUangBayar;
    private JLabel lblKembalian;
    private JButton btnBayar;
    private JButton btnCetakStruk;
    private JButton btnResetPOS;

    // Komponen Tab CRUD Produk
    private JTable tabelProdukCRUD;
    private DefaultTableModel modelProdukCRUD;
    private JTextField txtKode;
    private JComboBox<String> cmbKategori;
    private JTextField txtNama;
    private JTextField txtHarga;
    private JSpinner spinnerStok;
    private JLabel lblInfoKhusus;
    private JTextField txtInfoKhusus;
    private JButton btnTambah;
    private JButton btnUpdate;
    private JButton btnHapus;
    private JButton btnResetForm;

    // Komponen Tab Riwayat
    private JTable tabelRiwayat;
    private DefaultTableModel modelRiwayat;

    public KasirFrame(IKasirService kasirService) {
        this.kasirService = kasirService;
        this.transaksiAktif = kasirService.buatTransaksiBaru();

        initUI();
        refreshTabelProduk();
        refreshRiwayat();
    }

    // Helper untuk membuat tombol berwarna solid modern yang anti-gagal di Windows Look & Feel
    private JButton buatTombolWarna(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(200, 200, 200));
                } else if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void initUI() {
        setTitle("KASIR TOKO HASAN - Point of Sales (POS)");
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel Utama
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        // Header Panel
        mainPanel.add(buatPanelHeader(), BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("Point of Sales (Kasir)", buatTabPOS());
        tabbedPane.addTab("Kelola Produk (CRUD)", buatTabCRUD());
        tabbedPane.addTab("Riwayat Penjualan", buatTabRiwayat());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel buatPanelHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(new Color(24, 119, 242));

        JLabel lblTitle = new JLabel("KASIR TOKO HASAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        header.add(lblTitle);
        return header;
    }

    // =========================================================================
    // TAB 1: POINT OF SALES (KASIR / TRANSAKSI)
    // =========================================================================

    private JPanel buatTabPOS() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        // Split Horizontal: Kiri Katalog Barang, Kanan Keranjang Belanja
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);

        // --- Panel Kiri: Katalog Produk ---
        JPanel panelKiri = new JPanel(new BorderLayout(5, 5));
        panelKiri.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Katalog Produk Tersedia",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        modelKatalogPOS = new DefaultTableModel(new String[]{"Kode", "Kategori", "Nama Produk", "Harga", "Stok", "Info Promo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelKatalogPOS = new JTable(modelKatalogPOS);
        tabelKatalogPOS.setRowHeight(24);
        tabelKatalogPOS.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelKiri.add(new JScrollPane(tabelKatalogPOS), BorderLayout.CENTER);

        // Kontrol tambah ke keranjang
        JPanel panelTambah = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelTambah.add(new JLabel("Jumlah Beli:"));
        spinnerQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        panelTambah.add(spinnerQty);

        JButton btnTambahKeranjang = buatTombolWarna("+ Masukkan Keranjang", new Color(39, 174, 96), Color.WHITE);
        btnTambahKeranjang.setPreferredSize(new Dimension(180, 32));
        btnTambahKeranjang.addActionListener(e -> tambahKeKeranjang());
        panelTambah.add(btnTambahKeranjang);

        panelKiri.add(panelTambah, BorderLayout.SOUTH);

        // --- Panel Kanan: Keranjang & Checkout ---
        JPanel panelKanan = new JPanel(new BorderLayout(5, 5));
        panelKanan.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Keranjang Belanja Pelanggan",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        modelKeranjang = new DefaultTableModel(new String[]{"Barang", "Harga Satuan", "Qty", "Diskon", "Total Bersih"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelKeranjang = new JTable(modelKeranjang);
        tabelKeranjang.setRowHeight(24);
        tabelKeranjang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelKanan.add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);

        // Panel Total & Pembayaran
        JPanel panelPembayaran = new JPanel(new GridBagLayout());
        panelPembayaran.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelPembayaran.setBackground(new Color(248, 249, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);

        lblSubtotal = new JLabel("Rp 0");
        lblTotalDiskon = new JLabel("Rp 0");
        lblTotalDiskon.setForeground(new Color(192, 57, 43));
        lblTotalAkhir = new JLabel("Rp 0");
        lblTotalAkhir.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalAkhir.setForeground(new Color(41, 128, 185));

        txtVoucher = new JTextField("0", 10);
        txtVoucher.addActionListener(e -> hitungPembayaranPOS());
        txtUangBayar = new JTextField("0", 10);
        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKembalian.setForeground(new Color(39, 174, 96));

        // Baris 1: Subtotal
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        panelPembayaran.add(new JLabel("Subtotal Kotor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        panelPembayaran.add(lblSubtotal, gbc);

        // Baris 2: Diskon Produk (Polimorfisme)
        gbc.gridx = 0; gbc.gridy = 1;
        panelPembayaran.add(new JLabel("Diskon Promo Produk:"), gbc);
        gbc.gridx = 1;
        panelPembayaran.add(lblTotalDiskon, gbc);

        // Baris 3: Voucher Diskon Ekstra
        gbc.gridx = 0; gbc.gridy = 2;
        panelPembayaran.add(new JLabel("Voucher Diskon (Rp):"), gbc);
        gbc.gridx = 1;
        panelPembayaran.add(txtVoucher, gbc);

        // Baris 4: TOTAL AKHIR
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblLabelTotal = new JLabel("TOTAL TAGIHAN:");
        lblLabelTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelPembayaran.add(lblLabelTotal, gbc);
        gbc.gridx = 1;
        panelPembayaran.add(lblTotalAkhir, gbc);

        // Baris 5: Uang Tunai
        gbc.gridx = 0; gbc.gridy = 4;
        panelPembayaran.add(new JLabel("Uang Pembeli (Rp):"), gbc);
        gbc.gridx = 1;
        panelPembayaran.add(txtUangBayar, gbc);

        // Baris 6: Kembalian
        gbc.gridx = 0; gbc.gridy = 5;
        panelPembayaran.add(new JLabel("Kembalian:"), gbc);
        gbc.gridx = 1;
        panelPembayaran.add(lblKembalian, gbc);

        // Tombol-tombol POS
        JPanel panelBtnPOS = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnBayar = buatTombolWarna("Bayar / Selesaikan", new Color(24, 119, 242), Color.WHITE);
        btnBayar.setPreferredSize(new Dimension(150, 32));
        btnBayar.addActionListener(e -> prosesBayarPOS());

        btnCetakStruk = buatTombolWarna("Cetak Struk", new Color(108, 117, 125), Color.WHITE);
        btnCetakStruk.setPreferredSize(new Dimension(110, 32));
        btnCetakStruk.setEnabled(false);
        btnCetakStruk.addActionListener(e -> tampilkanDialogStruk(transaksiAktif));

        btnResetPOS = buatTombolWarna("Transaksi Baru", new Color(108, 117, 125), Color.WHITE);
        btnResetPOS.setPreferredSize(new Dimension(130, 32));
        btnResetPOS.addActionListener(e -> resetTransaksiPOS());

        panelBtnPOS.add(btnResetPOS);
        panelBtnPOS.add(btnCetakStruk);
        panelBtnPOS.add(btnBayar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panelPembayaran.add(panelBtnPOS, gbc);

        panelKanan.add(panelPembayaran, BorderLayout.SOUTH);

        splitPane.setLeftComponent(panelKiri);
        splitPane.setRightComponent(panelKanan);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void tambahKeKeranjang() {
        int selectedRow = tabelKatalogPOS.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih produk dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kode = (String) modelKatalogPOS.getValueAt(selectedRow, 0);
        Produk p = kasirService.cariProduk(kode);
        if (p == null) return;

        int qty = (Integer) spinnerQty.getValue();
        if (qty > p.getStok()) {
            JOptionPane.showMessageDialog(this, "Jumlah beli melebihi stok tersedia (Stok: " + p.getStok() + ")!", "Stok Tidak Cukup", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tambah ke objek transaksi
        ItemTransaksi item = new ItemTransaksi(p, qty);
        transaksiAktif.tambahItem(item);

        // Update Tabel Keranjang
        modelKeranjang.addRow(new Object[]{
                p.getNamaProduk(),
                FormatRupiah.format(p.getHarga()),
                qty,
                FormatRupiah.format(item.getDiskon()),
                FormatRupiah.format(item.getTotalBersih())
        });

        hitungPembayaranPOS();
    }

    private void hitungPembayaranPOS() {
        transaksiAktif.hitungTotal();
        double voucher = 0;
        try {
            voucher = Double.parseDouble(txtVoucher.getText().trim());
        } catch (NumberFormatException ignored) {}

        double totalKotor = transaksiAktif.getTotalKotor();
        double diskonProduk = transaksiAktif.getTotalDiskonItem();
        double totalTagihan = (totalKotor - diskonProduk) - voucher;
        if (totalTagihan < 0) totalTagihan = 0;

        lblSubtotal.setText(FormatRupiah.format(totalKotor));
        lblTotalDiskon.setText("-" + FormatRupiah.format(diskonProduk));
        lblTotalAkhir.setText(FormatRupiah.format(totalTagihan));
    }

    private void prosesBayarPOS() {
        if (transaksiAktif.getDaftarItem().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong!", "Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double uangBayar;
        try {
            uangBayar = Double.parseDouble(txtUangBayar.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Masukkan nominal uang bayar yang valid!", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double voucher = 0;
        try {
            voucher = Double.parseDouble(txtVoucher.getText().trim());
        } catch (NumberFormatException ignored) {}

        // DEMO POLIMORFISME STATIS (OVERLOADING METHOD)
        boolean sukses;
        if (voucher > 0) {
            sukses = transaksiAktif.prosesPembayaran(uangBayar, voucher);
        } else {
            sukses = transaksiAktif.prosesPembayaran(uangBayar);
        }

        if (sukses) {
            lblKembalian.setText(FormatRupiah.format(transaksiAktif.getKembalian()));
            kasirService.simpanTransaksi(transaksiAktif);

            btnBayar.setEnabled(false);
            btnCetakStruk.setEnabled(true);

            // Perbarui data stok di tabel katalog
            refreshTabelProduk();
            refreshRiwayat();

            JOptionPane.showMessageDialog(this, "Pembayaran Berhasil!\nKembalian: "
                    + FormatRupiah.format(transaksiAktif.getKembalian()), "Sukses Transaksi", JOptionPane.INFORMATION_MESSAGE);

            // Otomatis buka dialog struk
            tampilkanDialogStruk(transaksiAktif);
        } else {
            double kurang = transaksiAktif.getTotalAkhir() - uangBayar;
            JOptionPane.showMessageDialog(this, "Uang pembayaran kurang: "
                    + FormatRupiah.format(kurang), "Uang Kurang", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetTransaksiPOS() {
        transaksiAktif = kasirService.buatTransaksiBaru();
        modelKeranjang.setRowCount(0);
        lblSubtotal.setText("Rp 0");
        lblTotalDiskon.setText("Rp 0");
        lblTotalAkhir.setText("Rp 0");
        txtVoucher.setText("0");
        txtUangBayar.setText("0");
        lblKembalian.setText("Rp 0");
        btnBayar.setEnabled(true);
        btnCetakStruk.setEnabled(false);
    }

    // =========================================================================
    // TAB 2: MANAJEMEN PRODUK (CRUD)
    // =========================================================================

    private JPanel buatTabCRUD() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Split Horizontal: Kiri Tabel Produk, Kanan Form Input
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.65);

        // --- Panel Tabel Produk ---
        JPanel panelTabel = new JPanel(new BorderLayout());
        panelTabel.setBorder(BorderFactory.createTitledBorder("Daftar Inventaris Produk (Read)"));

        modelProdukCRUD = new DefaultTableModel(new String[]{"Kode", "Kategori", "Nama Produk", "Harga", "Stok", "Info Khusus"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelProdukCRUD = new JTable(modelProdukCRUD);
        tabelProdukCRUD.setRowHeight(24);
        tabelProdukCRUD.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Klik baris di tabel untuk mengisi form secara otomatis
        tabelProdukCRUD.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabelProdukCRUD.getSelectedRow();
                if (row >= 0) {
                    txtKode.setText(modelProdukCRUD.getValueAt(row, 0).toString());
                    cmbKategori.setSelectedItem(modelProdukCRUD.getValueAt(row, 1).toString());
                    txtNama.setText(modelProdukCRUD.getValueAt(row, 2).toString());

                    // Ambil harga asli dari service
                    Produk p = kasirService.cariProduk(txtKode.getText());
                    if (p != null) {
                        txtHarga.setText(String.valueOf((long) p.getHarga()));
                        spinnerStok.setValue(p.getStok());
                        if (p instanceof ProdukMakanan m) {
                            txtInfoKhusus.setText(m.getTanggalKadaluarsa());
                        } else if (p instanceof ProdukElektronik elk) {
                            txtInfoKhusus.setText(String.valueOf(elk.getMasaGaransiBulan()));
                        }
                    }
                    txtKode.setEditable(false); // Kunci kode produk saat mode edit
                }
            }
        });

        panelTabel.add(new JScrollPane(tabelProdukCRUD), BorderLayout.CENTER);

        // --- Panel Form Input CRUD ---
        JPanel panelForm = new JPanel(new BorderLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Form Produk (Create & Update)"));

        JPanel inputFields = new JPanel(new GridLayout(6, 2, 8, 12));
        inputFields.setBorder(new EmptyBorder(15, 10, 15, 10));

        txtKode = new JTextField();
        cmbKategori = new JComboBox<>(new String[]{"Makanan", "Elektronik"});
        txtNama = new JTextField();
        txtHarga = new JTextField();
        spinnerStok = new JSpinner(new SpinnerNumberModel(10, 0, 10000, 1));
        lblInfoKhusus = new JLabel("Tanggal Expired (dd-MM-yyyy):");
        txtInfoKhusus = new JTextField();

        cmbKategori.addActionListener(e -> {
            String selected = (String) cmbKategori.getSelectedItem();
            if ("Makanan".equals(selected)) {
                lblInfoKhusus.setText("Tanggal Expired (dd-MM-yyyy):");
            } else {
                lblInfoKhusus.setText("Masa Garansi (Bulan):");
            }
        });

        inputFields.add(new JLabel("Kode Produk:"));
        inputFields.add(txtKode);
        inputFields.add(new JLabel("Kategori (Inheritance):"));
        inputFields.add(cmbKategori);
        inputFields.add(new JLabel("Nama Produk:"));
        inputFields.add(txtNama);
        inputFields.add(new JLabel("Harga Satuan (Rp):"));
        inputFields.add(txtHarga);
        inputFields.add(new JLabel("Jumlah Stok:"));
        inputFields.add(spinnerStok);
        inputFields.add(lblInfoKhusus);
        inputFields.add(txtInfoKhusus);

        // Tombol Aksi Form
        JPanel panelTombolCRUD = new JPanel(new GridLayout(4, 1, 0, 8));
        panelTombolCRUD.setBorder(new EmptyBorder(5, 10, 10, 10));

        btnTambah = buatTombolWarna("+ Tambah Produk (Create)", new Color(39, 174, 96), Color.WHITE);
        btnTambah.addActionListener(e -> tambahProdukCRUD());

        btnUpdate = buatTombolWarna("Perbarui Data (Update)", new Color(41, 128, 185), Color.WHITE);
        btnUpdate.addActionListener(e -> updateProdukCRUD());

        btnHapus = buatTombolWarna("Hapus Produk (Delete)", new Color(192, 57, 43), Color.WHITE);
        btnHapus.addActionListener(e -> hapusProdukCRUD());

        btnResetForm = buatTombolWarna("Bersihkan Form", new Color(108, 117, 125), Color.WHITE);
        btnResetForm.addActionListener(e -> resetFormCRUD());

        panelTombolCRUD.add(btnTambah);
        panelTombolCRUD.add(btnUpdate);
        panelTombolCRUD.add(btnHapus);
        panelTombolCRUD.add(btnResetForm);

        panelForm.add(inputFields, BorderLayout.NORTH);
        panelForm.add(panelTombolCRUD, BorderLayout.CENTER);

        splitPane.setLeftComponent(panelTabel);
        splitPane.setRightComponent(panelForm);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void tambahProdukCRUD() {
        String kode = txtKode.getText().trim().toUpperCase();
        String nama = txtNama.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();
        String strInfo = txtInfoKhusus.getText().trim();

        if (kode.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Nama produk tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (kasirService.cariProduk(kode) != null) {
            JOptionPane.showMessageDialog(this, "Kode produk '" + kode + "' sudah digunakan!", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double harga;
        try {
            harga = Double.parseDouble(txtHarga.getText().trim());
            if (harga <= 0) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka lebih besar dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stok = (Integer) spinnerStok.getValue();

        Produk p;
        if ("Makanan".equals(kategori)) {
            p = new ProdukMakanan(kode, nama, harga, stok, strInfo.isEmpty() ? "31-12-2027" : strInfo);
        } else {
            int garansi = 12;
            try { garansi = Integer.parseInt(strInfo); } catch (Exception ignored) {}
            p = new ProdukElektronik(kode, nama, harga, stok, garansi);
        }

        kasirService.tambahProduk(p);
        refreshTabelProduk();
        resetFormCRUD();
        JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateProdukCRUD() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();

        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih produk dari tabel untuk diperbarui!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double harga;
        try {
            harga = Double.parseDouble(txtHarga.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format harga salah!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int stok = (Integer) spinnerStok.getValue();

        boolean sukses = kasirService.updateProduk(kode, nama, harga, stok);
        if (sukses) {
            refreshTabelProduk();
            resetFormCRUD();
            JOptionPane.showMessageDialog(this, "Data produk berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate produk!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusProdukCRUD() {
        String kode = txtKode.getText().trim();
        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih produk dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus produk '" + kode + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean sukses = kasirService.hapusProduk(kode);
            if (sukses) {
                refreshTabelProduk();
                resetFormCRUD();
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void resetFormCRUD() {
        txtKode.setText("");
        txtKode.setEditable(true);
        txtNama.setText("");
        txtHarga.setText("");
        spinnerStok.setValue(10);
        txtInfoKhusus.setText("");
        tabelProdukCRUD.clearSelection();
    }

    // =========================================================================
    // TAB 3: RIWAYAT PENJUALAN
    // =========================================================================

    private JPanel buatTabRiwayat() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        modelRiwayat = new DefaultTableModel(new String[]{"No. Nota", "Tanggal & Waktu", "Jumlah Item", "Total Akhir"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelRiwayat = new JTable(modelRiwayat);
        tabelRiwayat.setRowHeight(24);
        tabelRiwayat.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tabelRiwayat), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCetakUlang = buatTombolWarna("Lihat / Cetak Ulang Struk", new Color(41, 128, 185), Color.WHITE);
        btnCetakUlang.setPreferredSize(new Dimension(190, 32));
        btnCetakUlang.addActionListener(e -> {
            int row = tabelRiwayat.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih salah satu transaksi dari riwayat!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String noNota = (String) modelRiwayat.getValueAt(row, 0);
            for (Transaksi t : kasirService.getRiwayatTransaksi()) {
                if (t.getNoNota().equals(noNota)) {
                    tampilkanDialogStruk(t);
                    break;
                }
            }
        });
        panelBawah.add(btnCetakUlang);
        panel.add(panelBawah, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================================
    // TAB 4: PENJELASAN KONSEP PBO
    // =========================================================================

    private JPanel buatTabOOP() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextArea txt = new JTextArea();
        txt.setFont(new Font("Consolas", Font.PLAIN, 13));
        txt.setEditable(false);
        txt.setText("""
                ===============================================================================
                             PEMETAAN KONSEP PEMROGRAMAN BERORIENTASI OBJEK (PBO)
                ===============================================================================

                1. ATRIBUT & ENKAPSULASI (Encapsulation):
                   - Variabel data pada kelas Produk, Transaksi, dan ItemTransaksi dibuat 'private'.
                   - Akses dan modifikasi hanya dapat melalui metode public Getter dan Setter.
                   - Dilengkapi validasi internal (misal: harga > 0, stok tidak boleh negatif).

                2. ABSTRAKSI (Abstraction):
                   - Abstract Class: 'Produk'
                     Menyediakan metode abstrak: hitungDiskon(int), getKategori(), getInfoKhusus().
                   - Interface: 'Cetakable'
                     Mendefinisikan kontrak method cetakStruk() yang diimplementasikan oleh Transaksi.
                   - Interface: 'IKasirService'
                     Memisahkan logika bisnis kasir dari kode antarmuka visual (Clean Architecture).

                3. PEWARISAN (Inheritance):
                   - Superclass: 'Produk'
                   - Subclass 1 : 'ProdukMakanan extends Produk'
                     Menambah atribut unik 'tanggalKadaluarsa'.
                   - Subclass 2 : 'ProdukElektronik extends Produk'
                     Menambah atribut unik 'masaGaransiBulan'.
                   - Konstruktor subclass memanggil super(kode, nama, harga, stok).

                4. POLIMORFISME (Polymorphism):
                   a. Polimorfisme Dinamis (Method Overriding):
                      - ProdukMakanan meng-override hitungDiskon(): Diskon 10% jika beli >= 5 item.
                      - ProdukElektronik meng-override hitungDiskon(): Diskon 5% jika beli >= 2 unit.
                      - List<Produk> menyimpan beragam objek subclass secara polimorfis.
                   b. Polimorfisme Statis (Method Overloading):
                      - Transaksi.prosesPembayaran(double nominal)
                      - Transaksi.prosesPembayaran(double nominal, double diskonVoucher)
                      - KasirService.tambahStok(String kode, int qty) vs tambahStok(Produk p, int qty)
                ===============================================================================
                """);
        txt.setCaretPosition(0);
        panel.add(new JScrollPane(txt), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // DIALOG STRUK BELANJA
    // =========================================================================

    private void tampilkanDialogStruk(Transaksi t) {
        if (t == null) return;

        JDialog dialog = new JDialog(this, "Struk Transaksi - " + t.getNoNota(), true);
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);

        StringBuilder sb = new StringBuilder();
        sb.append("===================================================\n");
        sb.append("              STRUK PEMBAYARAN KASIR               \n");
        sb.append("                 KASIR TOKO HASAN                  \n");
        sb.append("===================================================\n");
        sb.append("No. Nota : ").append(t.getNoNota()).append("\n");
        sb.append("Tanggal  : ").append(t.getTanggalWaktu()).append("\n");
        sb.append("---------------------------------------------------\n");
        sb.append(String.format("%-18s %4s %12s %12s\n", "Item", "Qty", "Harga", "Total"));
        sb.append("---------------------------------------------------\n");

        for (ItemTransaksi it : t.getDaftarItem()) {
            Produk p = it.getProduk();
            String nama = p.getNamaProduk();
            if (nama.length() > 18) nama = nama.substring(0, 18);
            sb.append(String.format("%-18s %4d %12s %12s\n",
                    nama, it.getJumlah(), FormatRupiah.format(p.getHarga()), FormatRupiah.format(it.getSubtotal())));
            if (it.getDiskon() > 0) {
                sb.append("  >> Promo (").append(p.getInfoKhusus()).append(") : -")
                  .append(FormatRupiah.format(it.getDiskon())).append("\n");
            }
        }

        sb.append("---------------------------------------------------\n");
        sb.append(String.format("%-28s : %18s\n", "Subtotal Kotor", FormatRupiah.format(t.getTotalKotor())));
        if (t.getTotalDiskonItem() > 0) {
            sb.append(String.format("%-28s : -%17s\n", "Diskon Produk", FormatRupiah.format(t.getTotalDiskonItem())));
        }
        if (t.getDiskonVoucher() > 0) {
            sb.append(String.format("%-28s : -%17s\n", "Voucher Diskon", FormatRupiah.format(t.getDiskonVoucher())));
        }
        sb.append("---------------------------------------------------\n");
        sb.append(String.format("%-28s : %18s\n", "TOTAL TAGIHAN", FormatRupiah.format(t.getTotalAkhir())));
        sb.append(String.format("%-28s : %18s\n", "Uang Diterima", FormatRupiah.format(t.getUangDiterima())));
        sb.append(String.format("%-28s : %18s\n", "Kembalian", FormatRupiah.format(t.getKembalian())));
        sb.append("===================================================\n");
        sb.append("      Terima Kasih Telah Berbelanja di Toko Kami!  \n");
        sb.append("===================================================\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.add(new JScrollPane(area), BorderLayout.CENTER);

        JButton btnTutup = new JButton("Tutup");
        btnTutup.addActionListener(e -> dialog.dispose());
        JPanel pnlBawah = new JPanel();
        pnlBawah.add(btnTutup);
        pnl.add(pnlBawah, BorderLayout.SOUTH);

        dialog.setContentPane(pnl);
        dialog.setVisible(true);
    }

    private void refreshTabelProduk() {
        modelKatalogPOS.setRowCount(0);
        modelProdukCRUD.setRowCount(0);

        List<Produk> list = kasirService.getAllProduk();
        for (Produk p : list) {
            Object[] row = new Object[]{
                    p.getKodeProduk(),
                    p.getKategori(),
                    p.getNamaProduk(),
                    FormatRupiah.format(p.getHarga()),
                    p.getStok(),
                    p.getInfoKhusus()
            };
            modelKatalogPOS.addRow(row);
            modelProdukCRUD.addRow(row);
        }
    }

    private void refreshRiwayat() {
        modelRiwayat.setRowCount(0);
        for (Transaksi t : kasirService.getRiwayatTransaksi()) {
            modelRiwayat.addRow(new Object[]{
                    t.getNoNota(),
                    t.getTanggalWaktu(),
                    t.getDaftarItem().size(),
                    FormatRupiah.format(t.getTotalAkhir())
            });
        }
    }
}
