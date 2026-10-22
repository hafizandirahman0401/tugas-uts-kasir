package com.kasir.service;

import com.kasir.model.Produk;
import com.kasir.model.Transaksi;
import java.util.List;

/**
 * =========================================================================
 * KONSEP OOP: ABSTRAKSI (INTERFACE SERVICE)
 * =========================================================================
 * Interface IKasirService menjadi kontrak layanan sistem kasir.
 * Memisahkan antarmuka (interface) dari implementasi logika bisnis,
 * sehingga memudahkan pemeliharaan kode dan penerapan Clean Architecture.
 */
public interface IKasirService {

    // ---------------------------------------------------------
    // OPERASI CRUD PRODUK (Create, Read, Update, Delete)
    // ---------------------------------------------------------

    /**
     * CREATE: Menambahkan produk baru ke katalog
     */
    void tambahProduk(Produk produk);

    /**
     * READ: Mengambil seluruh daftar produk
     */
    List<Produk> getAllProduk();

    /**
     * READ: Mencari produk berdasarkan kode produk
     */
    Produk cariProduk(String kode);

    /**
     * UPDATE: Memperbarui data produk
     */
    boolean updateProduk(String kode, String namaBaru, double hargaBaru, int stokBaru);

    /**
     * DELETE: Menghapus produk dari katalog
     */
    boolean hapusProduk(String kode);

    // ---------------------------------------------------------
    // OPERASI POINT OF SALES (POS) & TRANSAKSI
    // ---------------------------------------------------------

    /**
     * Membuat objek transaksi kasir baru dengan nomor nota otomatis
     */
    Transaksi buatTransaksiBaru();

    /**
     * Menyelesaikan transaksi dan menyimpan ke riwayat transaksi
     */
    void simpanTransaksi(Transaksi transaksi);

    /**
     * Mendapatkan daftar riwayat transaksi yang pernah dilakukan
     */
    List<Transaksi> getRiwayatTransaksi();

    // ---------------------------------------------------------
    // POLIMORFISME STATIS (OVERLOADING)
    // ---------------------------------------------------------

    /**
     * Overloading 1: Menambah stok berdasarkan kode produk
     */
    boolean tambahStok(String kode, int jumlah);

    /**
     * Overloading 2: Menambah stok berdasarkan objek Produk langsung
     */
    void tambahStok(Produk produk, int jumlah);
}
