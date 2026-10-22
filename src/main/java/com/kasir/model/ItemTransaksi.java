package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: ENKAPSULASI & POLIMORFISME
 * =========================================================================
 * ItemTransaksi mewakili satu baris pembelian pada keranjang belanja kasir.
 * Menggunakan referensi 'Produk' (Polimorfisme), sehingga dapat menampung
 * produk makanan maupun elektronik tanpa perlu membuat kelas terpisah.
 */
public class ItemTransaksi {

    // Referensi polimorfik ke superclass Produk
    private Produk produk;
    private int jumlah;

    public ItemTransaksi(Produk produk, int jumlah) {
        this.produk = produk;
        this.jumlah = jumlah;
    }

    public Produk getProduk() {
        return produk;
    }

    public void setProduk(Produk produk) {
        this.produk = produk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        if (jumlah > 0) {
            this.jumlah = jumlah;
        }
    }

    /**
     * Menghitung subtotal kotor (Harga Satuan x Jumlah)
     */
    public double getSubtotal() {
        return produk.getHarga() * jumlah;
    }

    /**
     * Memanggil metode hitungDiskon() secara polimorfis.
     * Java akan secara otomatis mengeksekusi rumus diskon milik
     * ProdukMakanan atau ProdukElektronik sesuai objek aslinya.
     */
    public double getDiskon() {
        return produk.hitungDiskon(jumlah);
    }

    /**
     * Menghitung total bersih setelah dipotong diskon
     */
    public double getTotalBersih() {
        return getSubtotal() - getDiskon();
    }
}
