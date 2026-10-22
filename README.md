# 🛒 KASIR TOKO HASAN - Point of Sales (PBO)

Aplikasi kasir desktop murni menggunakan bahasa **Java (Java Swing / JFrame)** yang dibuat untuk **NetBeans IDE**. Aplikasi ini memenuhi seluruh kriteria tugas Pemrograman Berorientasi Objek (PBO / OOP): **CRUD**, **Point of Sales (POS)**, **Atribut**, **Enkapsulasi**, **Abstraksi**, **Pewarisan (Inheritance)**, dan **Polimorfisme (Overriding & Overloading)**.

---

## 📁 Struktur Package & File (100% Java Murni)

```text
src/main/java/com/kasir/
├── Main.java                        <-- Titik masuk utama (Menjalankan JFrame)
│
├── gui/                             <-- Antarmuka Desktop Java Swing
│   └── KasirFrame.java                    (Jendela GUI Kasir Toko Hasan)
│
├── model/                           <-- Konsep PBO / OOP
│   ├── Cetakable.java                     (Interface - Abstraksi)
│   ├── Produk.java                        (Abstract Class - Superclass & Enkapsulasi)
│   ├── ProdukMakanan.java                 (Subclass - Pewarisan & Overriding)
│   ├── ProdukElektronik.java              (Subclass - Pewarisan & Overriding)
│   ├── ItemTransaksi.java                 (Detail Baris Keranjang & Polimorfisme)
│   └── Transaksi.java                     (Objek Transaksi, Overloading & Struk)
│
├── service/                         <-- Logika Bisnis & Operasional
│   ├── IKasirService.java                 (Interface Kontrak Layanan)
│   └── KasirService.java                  (Implementasi CRUD & Logika POS)
│
└── util/                            <-- Utilitas
    └── FormatRupiah.java                  (Format Mata Uang Rupiah)
```

---

## 🎯 Pemetaan Konsep PBO

1. **Atribut & Enkapsulasi**:
   Semua variabel atribut bertipe `private` (`kodeProduk`, `namaProduk`, `harga`, `stok`). Akses data melalui method getter dan setter disertai validasi internal.

2. **Abstraksi**:
   - `abstract class Produk`: memiliki abstract method `hitungDiskon(int jumlah)`, `getKategori()`, dan `getInfoKhusus()`.
   - `interface Cetakable`: mendefinisikan kontrak method `cetakStruk()`.
   - `interface IKasirService`: kontrak logika bisnis CRUD dan kasir.

3. **Pewarisan (Inheritance)**:
   - Superclass: `Produk`
   - Subclass: `ProdukMakanan extends Produk` dan `ProdukElektronik extends Produk`.

4. **Polimorfisme**:
   - **Dinamis (Overriding)**: Subclass meng-override `hitungDiskon()` dengan rumus berbeda (Makanan diskon jika beli >= 5, Elektronik jika beli >= 2).
   - **Statis (Overloading)**: Method `prosesPembayaran(double nominal)` dan `prosesPembayaran(double nominal, double diskonVoucher)`.

---

## 🚀 Cara Menjalankan di NetBeans IDE

1. Buka **NetBeans IDE**.
2. Klik **File** -> **Open Project...** (atau `Ctrl + Shift + O`).
3. Pilih folder: `C:\Users\Hp\OneDrive\Documents\kasir`.
4. Tekan tombol **F6** (atau klik tombol hijau **Run Project**).
5. Jendela aplikasi desktop Java Swing akan langsung tampil di layar monitor Anda.
