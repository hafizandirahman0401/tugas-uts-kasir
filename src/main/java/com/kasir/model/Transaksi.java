package com.kasir.model;

import com.kasir.util.FormatRupiah;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 * KONSEP OOP:
 * 1. ABSTRAKSI: Mengimplementasikan interface Cetakable (implements Cetakable).
 * 2. ENKAPSULASI: Semua atribut private dengan getter/setter terkontrol.
 * 3. POLIMORFISME STATIS (OVERLOADING): Memiliki beberapa method 'prosesPembayaran'
 *    dengan parameter yang berbeda (signature berbeda).
 * =========================================================================
 */
public class Transaksi implements Cetakable {

    private String noNota;
    private String tanggalWaktu;
    private List<ItemTransaksi> daftarItem;
    private double totalKotor;
    private double totalDiskonItem;
    private double diskonVoucher;
    private double totalAkhir;
    private double uangDiterima;
    private double kembalian;
    private boolean lunas;

    public Transaksi(String noNota) {
        this.noNota = noNota;
        this.daftarItem = new ArrayList<>();
        this.tanggalWaktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        this.diskonVoucher = 0.0;
        this.lunas = false;
    }

    /**
     * Menambahkan item belanja ke dalam transaksi
     */
    public void tambahItem(ItemTransaksi item) {
        this.daftarItem.add(item);
        hitungTotal();
    }

    /**
     * Menghitung rincian total belanja dari semua item
     */
    public void hitungTotal() {
        this.totalKotor = 0;
        this.totalDiskonItem = 0;

        for (ItemTransaksi item : daftarItem) {
            this.totalKotor += item.getSubtotal();
            this.totalDiskonItem += item.getDiskon();
        }

        this.totalAkhir = (this.totalKotor - this.totalDiskonItem) - this.diskonVoucher;
        if (this.totalAkhir < 0) {
            this.totalAkhir = 0;
        }
    }

    // ---------------------------------------------------------
    // POLIMORFISME STATIS: METHOD OVERLOADING (prosesPembayaran)
    // ---------------------------------------------------------

    /**
     * Overloading 1: Pembayaran standar secara tunai tanpa kupon diskon tambahan
     * @param nominalUang Jumlah uang yang dibayarkan pelanggan
     * @return true jika pembayaran sukses (uang cukup)
     */
    public boolean prosesPembayaran(double nominalUang) {
        hitungTotal();
        if (nominalUang >= this.totalAkhir) {
            this.uangDiterima = nominalUang;
            this.kembalian = nominalUang - this.totalAkhir;
            this.lunas = true;
            return true;
        }
        return false;
    }

    /**
     * Overloading 2: Pembayaran dengan tambahan potongan voucher diskon
     * @param nominalUang Jumlah uang yang dibayarkan
     * @param diskonVoucher Potongan diskon ekstra dari kupon/voucher
     * @return true jika pembayaran sukses
     */
    public boolean prosesPembayaran(double nominalUang, double diskonVoucher) {
        this.diskonVoucher = diskonVoucher;
        hitungTotal();
        return prosesPembayaran(nominalUang); // Memanggil overloading 1
    }

    // ---------------------------------------------------------
    // IMPLEMENTASI DARI INTERFACE CETAKABLE (ABSTRAKSI)
    // ---------------------------------------------------------

    @Override
    public void cetakStruk() {
        System.out.println("\n=======================================================");
        System.out.println("                 STRUK PEMBAYARAN KASIR                ");
        System.out.println("                    KASIR TOKO SERBAMART HAFIZ                   ");
        System.out.println("=======================================================");
        System.out.println("No. Nota : " + noNota);
        System.out.println("Tanggal  : " + tanggalWaktu);
        System.out.println("-------------------------------------------------------");
        System.out.printf("%-18s %5s %12s %14s\n", "Item", "Qty", "Harga Satuan", "Total");
        System.out.println("-------------------------------------------------------");

        for (ItemTransaksi it : daftarItem) {
            Produk p = it.getProduk();
            // Polimorfisme: getKategori() dan getInfoKhusus() dipanggil secara dinamis
            String labelBarang = p.getNamaProduk() + " (" + p.getKategori() + ")";
            System.out.printf("%-18s %5d %12s %14s\n",
                    (labelBarang.length() > 18 ? labelBarang.substring(0, 18) : labelBarang),
                    it.getJumlah(),
                    FormatRupiah.format(p.getHarga()),
                    FormatRupiah.format(it.getSubtotal()));

            // Cetak keterangan diskon item jika ada
            if (it.getDiskon() > 0) {
                System.out.printf("  >> Diskon Promo (%s) : -%s\n",
                        p.getInfoKhusus(),
                        FormatRupiah.format(it.getDiskon()));
            }
        }

        System.out.println("-------------------------------------------------------");
        System.out.printf("%-35s : %15s\n", "Subtotal Kotor", FormatRupiah.format(totalKotor));
        if (totalDiskonItem > 0) {
            System.out.printf("%-35s : -%14s\n", "Total Diskon Produk", FormatRupiah.format(totalDiskonItem));
        }
        if (diskonVoucher > 0) {
            System.out.printf("%-35s : -%14s\n", "Voucher Diskon Tambahan", FormatRupiah.format(diskonVoucher));
        }
        System.out.println("-------------------------------------------------------");
        System.out.printf("%-35s : %15s\n", "TOTAL TAGIHAN", FormatRupiah.format(totalAkhir));
        System.out.printf("%-35s : %15s\n", "Uang Diterima (Tunai)", FormatRupiah.format(uangDiterima));
        System.out.printf("%-35s : %15s\n", "Kembalian", FormatRupiah.format(kembalian));
        System.out.println("=======================================================");
        System.out.println("         Terima Kasih Telah Berbelanja di Toko Kami!   ");
        System.out.println("=======================================================\n");
    }

    // ---------------------------------------------------------
    // GETTER & SETTER
    // ---------------------------------------------------------

    public String getNoNota() {
        return noNota;
    }

    public String getTanggalWaktu() {
        return tanggalWaktu;
    }

    public List<ItemTransaksi> getDaftarItem() {
        return daftarItem;
    }

    public double getTotalKotor() {
        return totalKotor;
    }

    public double getTotalDiskonItem() {
        return totalDiskonItem;
    }

    public double getDiskonVoucher() {
        return diskonVoucher;
    }

    public double getTotalAkhir() {
        return totalAkhir;
    }

    public double getUangDiterima() {
        return uangDiterima;
    }

    public double getKembalian() {
        return kembalian;
    }

    public boolean isLunas() {
        return lunas;
    }
}

