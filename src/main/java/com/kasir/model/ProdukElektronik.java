package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: PEWARISAN (INHERITANCE) & POLIMORFISME (DYNAMIC/OVERRIDING)
 * =========================================================================
 * 1. Pewarisan: ProdukElektronik mewarisi sifat dari superclass Produk (extends Produk).
 * 2. Atribut Tambahan: Memiliki atribut khusus 'masaGaransiBulan' (lama garansi dalam hitungan bulan).
 * 3. Polimorfisme (Overriding): Meng-override metode hitungDiskon(), getKategori(),
 *    dan getInfoKhusus() dengan logika yang berbeda dari ProdukMakanan.
 */
public class ProdukElektronik extends Produk {

    // Atribut unik khusus elektronik (Enkapsulasi)
    private int masaGaransiBulan;

    /**
     * Konstruktor ProdukElektronik
     */
    public ProdukElektronik(String kodeProduk, String namaProduk, double harga, int stok, int masaGaransiBulan) {
        // Memanggil konstruktor superclass Produk
        super(kodeProduk, namaProduk, harga, stok);
        this.masaGaransiBulan = masaGaransiBulan;
    }

    // ---------------------------------------------------------
    // POLIMORFISME: OVERRIDING METODE ABSTRACT DARI SUPERCLASS
    // ---------------------------------------------------------

    /**
     * Polimorfisme Dinamis:
     * Aturan diskon elektronik: Jika membeli minimal 2 unit elektronik sejenis,
     * pembeli mendapatkan promo bundling diskon 5%.
     */
    @Override
    public double hitungDiskon(int jumlahBeli) {
        if (jumlahBeli >= 2) {
            double total = getHarga() * jumlahBeli;
            return total * 0.05; // Diskon 5%
        }
        return 0.0;
    }

    @Override
    public String getKategori() {
        return "Elektronik";
    }

    @Override
    public String getInfoKhusus() {
        return "Garansi: " + this.masaGaransiBulan + " Bln";
    }

    // ---------------------------------------------------------
    // GETTER & SETTER KHUSUS
    // ---------------------------------------------------------

    public int getMasaGaransiBulan() {
        return masaGaransiBulan;
    }

    public void setMasaGaransiBulan(int masaGaransiBulan) {
        if (masaGaransiBulan >= 0) {
            this.masaGaransiBulan = masaGaransiBulan;
        }
    }
}
