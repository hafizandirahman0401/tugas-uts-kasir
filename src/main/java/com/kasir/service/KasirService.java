package com.kasir.service;

import com.kasir.model.ItemTransaksi;
import com.kasir.model.Produk;
import com.kasir.model.ProdukElektronik;
import com.kasir.model.ProdukMakanan;
import com.kasir.model.Transaksi;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 * KONSEP OOP:
 * 1. IMPLEMENTASI INTERFACE (ABSTRAKSI) -> implements IKasirService
 * 2. POLIMORFISME KOLEKSI -> List<Produk> menyimpan beragam objek turunan (ProdukMakanan & ProdukElektronik)
 * 3. POLIMORFISME STATIS (OVERLOADING) -> tambahStok(String, int) dan tambahStok(Produk, int)
 * =========================================================================
 */
public class KasirService implements IKasirService {

    // Koleksi polimorfik: Menyimpan referensi kelas abstrak Produk
    private final List<Produk> katalogProduk;
    private final List<Transaksi> riwayatTransaksi;
    private int counterNota;

    public KasirService() {
        this.katalogProduk = new ArrayList<>();
        this.riwayatTransaksi = new ArrayList<>();
        this.counterNota = 1;
        
        // Inisialisasi data sampel agar langsung siap digunakan saat demo
        initDataAwal();
    }

    /**
     * Mengisi data awal katalog produk (Makanan & Elektronik)
     */
    private void initDataAwal() {
        // Polimorfisme: objek ProdukMakanan dan ProdukElektronik dimasukkan ke List<Produk>
        tambahProduk(new ProdukMakanan("MKN-01", "Roti Tawar Gandum", 15000, 20, "28-12-2026"));
        tambahProduk(new ProdukMakanan("MKN-02", "Susu UHT Cokelat 1L", 22000, 15, "15-05-2027"));
        tambahProduk(new ProdukMakanan("MKN-03", "Keripik Kentang Keju", 9500, 30, "10-10-2027"));
        tambahProduk(new ProdukElektronik("ELK-01", "Mouse Wireless Silent", 85000, 10, 12));
        tambahProduk(new ProdukElektronik("ELK-02", "Flashdisk USB 64GB", 65000, 8, 24));
        tambahProduk(new ProdukElektronik("ELK-03", "Powerbank 10000mAh", 175000, 5, 18));
    }

    // ---------------------------------------------------------
    // IMPLEMENTASI OPERASI CRUD (Create, Read, Update, Delete)
    // ---------------------------------------------------------

    @Override
    public void tambahProduk(Produk produk) {
        if (produk != null) {
            katalogProduk.add(produk);
        }
    }

    @Override
    public List<Produk> getAllProduk() {
        return new ArrayList<>(katalogProduk);
    }

    @Override
    public Produk cariProduk(String kode) {
        if (kode == null) return null;
        for (Produk p : katalogProduk) {
            if (p.getKodeProduk().equalsIgnoreCase(kode.trim())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean updateProduk(String kode, String namaBaru, double hargaBaru, int stokBaru) {
        Produk p = cariProduk(kode);
        if (p != null) {
            p.setNamaProduk(namaBaru);
            p.setHarga(hargaBaru);
            p.setStok(stokBaru);
            return true;
        }
        return false;
    }

    @Override
    public boolean hapusProduk(String kode) {
        Produk p = cariProduk(kode);
        if (p != null) {
            return katalogProduk.remove(p);
        }
        return false;
    }

    // ---------------------------------------------------------
    // IMPLEMENTASI POINT OF SALES (POS) & TRANSAKSI
    // ---------------------------------------------------------

    @Override
    public Transaksi buatTransaksiBaru() {
        String noNota = String.format("TRX-%04d", counterNota++);
        return new Transaksi(noNota);
    }

    @Override
    public void simpanTransaksi(Transaksi transaksi) {
        if (transaksi != null && transaksi.isLunas()) {
            // Kurangi stok barang yang dibeli secara otomatis
            for (ItemTransaksi item : transaksi.getDaftarItem()) {
                item.getProduk().kurangiStok(item.getJumlah());
            }
            riwayatTransaksi.add(transaksi);
        }
    }

    @Override
    public List<Transaksi> getRiwayatTransaksi() {
        return new ArrayList<>(riwayatTransaksi);
    }

    // ---------------------------------------------------------
    // POLIMORFISME STATIS: OVERLOADING METHOD tambahStok
    // ---------------------------------------------------------

    @Override
    public boolean tambahStok(String kode, int jumlah) {
        Produk p = cariProduk(kode);
        if (p != null && jumlah > 0) {
            p.tambahStok(jumlah);
            return true;
        }
        return false;
    }

    @Override
    public void tambahStok(Produk produk, int jumlah) {
        if (produk != null && jumlah > 0) {
            produk.tambahStok(jumlah);
        }
    }
}
