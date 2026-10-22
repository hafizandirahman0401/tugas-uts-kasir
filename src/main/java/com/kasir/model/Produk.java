package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: ABSTRAKSI (ABSTRACT CLASS) & ENKAPSULASI (ENCAPSULATION)
 * =========================================================================
 * 1. Abstraksi: Kelas Produk bersifat abstract, tidak dapat diinstansiasi secara
 *    langsung. Kelas ini menjadi cetak biru (blueprint) bagi kelas-kelas produk
 *    turunannya (seperti Makanan, Elektronik).
 * 2. Enkapsulasi: Semua variabel/atribut dibungkus dengan modifier 'private'.
 *    Akses dan modifikasi nilai hanya dapat dilakukan melalui Getter dan Setter
 *    yang disertai validasi data.
 */
public abstract class Produk {

    // ---------------------------------------------------------
    // ATRIBUT (Dienkapsulasi dengan access modifier private)
    // ---------------------------------------------------------
    private String kodeProduk;
    private String namaProduk;
    private double harga;
    private int stok;

    /**
     * Konstruktor Superclass
     * @param kodeProduk Kode unik produk (misal: MKN01, ELK01)
     * @param namaProduk Nama barang
     * @param harga Harga satuan barang
     * @param stok Jumlah stok awal barang
     */
    public Produk(String kodeProduk, String namaProduk, double harga, int stok) {
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        setHarga(harga); // menggunakan setter agar tervalidasi
        setStok(stok);   // menggunakan setter agar tervalidasi
    }

    // ---------------------------------------------------------
    // METHOD ABSTRAK (Wajib di-override oleh kelas turunan)
    // ---------------------------------------------------------
    
    /**
     * Menghitung besaran diskon berdasarkan jumlah pembelian.
     * Aturan diskon akan diimplementasikan secara berbeda oleh tiap subclass (Polimorfisme).
     * @param jumlahBeli Jumlah barang yang dibeli
     * @return Total potongan harga (diskon)
     */
    public abstract double hitungDiskon(int jumlahBeli);

    /**
     * Mengembalikan nama kategori produk (misal: "Makanan", "Elektronik").
     * @return String nama kategori
     */
    public abstract String getKategori();

    /**
     * Mengembalikan informasi khusus yang membedakan tipe produk
     * (misal: tanggal kadaluarsa untuk makanan, masa garansi untuk elektronik).
     * @return String deskripsi informasi khusus
     */
    public abstract String getInfoKhusus();

    // ---------------------------------------------------------
    // GETTER & SETTER (ENKAPSULASI dengan validasi data)
    // ---------------------------------------------------------

    public String getKodeProduk() {
        return kodeProduk;
    }

    public void setKodeProduk(String kodeProduk) {
        if (kodeProduk != null && !kodeProduk.trim().isEmpty()) {
            this.kodeProduk = kodeProduk.trim().toUpperCase();
        }
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        if (namaProduk != null && !namaProduk.trim().isEmpty()) {
            this.namaProduk = namaProduk.trim();
        }
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        if (harga > 0) {
            this.harga = harga;
        } else {
            System.out.println("[Peringatan] Harga harus lebih dari 0!");
        }
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok >= 0) {
            this.stok = stok;
        } else {
            System.out.println("[Peringatan] Stok tidak boleh bernilai negatif!");
        }
    }

    // ---------------------------------------------------------
    // METODE OPERASIONAL STOK
    // ---------------------------------------------------------

    /**
     * Mengurangi stok saat terjadi transaksi penjualan
     * @param jumlah Jumlah yang dikurangi
     * @return true jika berhasil, false jika stok tidak mencukupi
     */
    public boolean kurangiStok(int jumlah) {
        if (jumlah > 0 && this.stok >= jumlah) {
            this.stok -= jumlah;
            return true;
        }
        return false;
    }

    /**
     * Menambah stok barang (misal saat restock gudang)
     * @param jumlah Jumlah penambahan stok
     */
    public void tambahStok(int jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
        }
    }
}
