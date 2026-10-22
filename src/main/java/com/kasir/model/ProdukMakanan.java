package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: PEWARISAN (INHERITANCE) & POLIMORFISME (DYNAMIC/OVERRIDING)
 * =========================================================================
 * 1. Pewarisan: ProdukMakanan mewarisi semua atribut dan metode dari kelas Produk (extends Produk).
 * 2. Atribut Tambahan: Memiliki atribut khusus 'tanggalKadaluarsa' yang hanya dimiliki produk makanan.
 * 3. Polimorfisme (Overriding): Meng-override metode hitungDiskon(), getKategori(),
 *    dan getInfoKhusus() dengan logika spesifik kategori makanan.
 */
public class ProdukMakanan extends Produk {

    // Atribut unik khusus makanan (Enkapsulasi)
    private String tanggalKadaluarsa;

    /**
     * Konstruktor ProdukMakanan
     */
    public ProdukMakanan(String kodeProduk, String namaProduk, double harga, int stok, String tanggalKadaluarsa) {
        // Memanggil konstruktor superclass (Produk) menggunakan super()
        super(kodeProduk, namaProduk, harga, stok);
        this.tanggalKadaluarsa = tanggalKadaluarsa;
    }

    // ---------------------------------------------------------
    // POLIMORFISME: OVERRIDING METODE ABSTRACT DARI SUPERCLASS
    // ---------------------------------------------------------

    /**
     * Polimorfisme Dinamis:
     * Aturan diskon makanan: Jika pembelian minimal 5 bungkus/porsi,
     * pembeli mendapatkan diskon grosir makanan sebesar 10%.
     */
    @Override
    public double hitungDiskon(int jumlahBeli) {
        if (jumlahBeli >= 5) {
            double total = getHarga() * jumlahBeli;
            return total * 0.10; // Diskon 10%
        }
        return 0.0;
    }

    @Override
    public String getKategori() {
        return "Makanan";
    }

    @Override
    public String getInfoKhusus() {
        return "Exp: " + this.tanggalKadaluarsa;
    }

    // ---------------------------------------------------------
    // GETTER & SETTER KHUSUS
    // ---------------------------------------------------------

    public String getTanggalKadaluarsa() {
        return tanggalKadaluarsa;
    }

    public void setTanggalKadaluarsa(String tanggalKadaluarsa) {
        this.tanggalKadaluarsa = tanggalKadaluarsa;
    }
}
