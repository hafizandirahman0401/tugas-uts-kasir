/* ==========================================================================
   LOGIKA INTERAKTIF WEBSITE KASIR TOKO HASAN (index.js)
   ========================================================================== */

// Data Awal Produk (Disimpan di localStorage agar CRUD tersimpan permanen)
const PRODUK_DEFAULT = [
    { kode: "MKN-01", kategori: "Makanan", nama: "Roti Tawar Gandum", harga: 15000, stok: 20, info: "Exp: 28-12-2026" },
    { kode: "MKN-02", kategori: "Makanan", nama: "Susu UHT Cokelat 1L", harga: 22000, stok: 15, info: "Exp: 15-05-2027" },
    { kode: "MKN-03", kategori: "Makanan", nama: "Keripik Kentang Keju", harga: 9500, stok: 30, info: "Exp: 10-10-2027" },
    { kode: "ELK-01", kategori: "Elektronik", nama: "Mouse Wireless Silent", harga: 85000, stok: 10, info: "Garansi: 12 Bln" },
    { kode: "ELK-02", kategori: "Elektronik", nama: "Flashdisk USB 64GB", harga: 65000, stok: 8, info: "Garansi: 24 Bln" },
    { kode: "ELK-03", kategori: "Elektronik", nama: "Powerbank 10000mAh", harga: 175000, stok: 5, info: "Garansi: 18 Bln" }
];

let katalog = [];
let keranjang = [];
let riwayat = [];
let counterNota = 1;

// Format Angka ke Rupiah
function formatRp(nominal) {
    return "Rp " + Number(nominal).toLocaleString("id-ID");
}

// Inisialisasi Aplikasi Saat Halaman Dibuka
function initApp() {
    // Ambil data dari localStorage atau gunakan default
    const savedKatalog = localStorage.getItem("toko_hasan_katalog");
    if (savedKatalog) {
        katalog = JSON.parse(savedKatalog);
    } else {
        katalog = [...PRODUK_DEFAULT];
        simpanKatalogStorage();
    }

    const savedRiwayat = localStorage.getItem("toko_hasan_riwayat");
    if (savedRiwayat) {
        riwayat = JSON.parse(savedRiwayat);
        counterNota = riwayat.length + 1;
    }

    renderKatalogPOS();
    renderKatalogCRUD();
    renderRiwayat();
}

function simpanKatalogStorage() {
    localStorage.setItem("toko_hasan_katalog", JSON.stringify(katalog));
}

function simpanRiwayatStorage() {
    localStorage.setItem("toko_hasan_riwayat", JSON.stringify(riwayat));
}

// -------------------------------------------------------------
// NAVIGASI TAB
// -------------------------------------------------------------
function switchTab(tabId, btn) {
    document.querySelectorAll(".tab-content").forEach(el => el.classList.remove("active"));
    document.querySelectorAll("nav button").forEach(el => el.classList.remove("active"));
    document.getElementById("tab-" + tabId).classList.add("active");
    btn.classList.add("active");
}

// -------------------------------------------------------------
// POINT OF SALES (POS / KASIR)
// -------------------------------------------------------------
function renderKatalogPOS() {
    const tbody = document.getElementById("pos-product-list");
    tbody.innerHTML = "";

    katalog.forEach(p => {
        const badgeClass = p.kategori === "Makanan" ? "badge-food" : "badge-elec";
        tbody.innerHTML += `
            <tr>
                <td><strong>${p.kode}</strong></td>
                <td>
                    <div style="font-weight: 600;">${p.nama}</div>
                    <small><span class="badge ${badgeClass}">${p.kategori}</span> &bull; ${p.info}</small>
                </td>
                <td>${formatRp(p.harga)}</td>
                <td><strong>${p.stok}</strong></td>
                <td>
                    <button class="btn btn-primary" onclick="tambahKeKeranjang('${p.kode}')" ${p.stok <= 0 ? "disabled" : ""}>
                        ${p.stok <= 0 ? "Habis" : "+ Beli"}
                    </button>
                </td>
            </tr>
        `;
    });
}

function tambahKeKeranjang(kode) {
    const produk = katalog.find(p => p.kode === kode);
    if (!produk || produk.stok <= 0) return;

    const ada = keranjang.find(item => item.kode === kode);
    if (ada) {
        if (ada.qty + 1 > produk.stok) {
            alert("Jumlah beli melebihi stok yang tersedia (Stok: " + produk.stok + ")!");
            return;
        }
        ada.qty++;
    } else {
        keranjang.push({
            kode: produk.kode,
            nama: produk.nama,
            kategori: produk.kategori,
            harga: produk.harga,
            info: produk.info,
            qty: 1
        });
    }

    renderKeranjang();
}

function hitungDiskonItem(item) {
    // Makanan: beli >= 5 dapat diskon 10%
    if (item.kategori === "Makanan" && item.qty >= 5) {
        return (item.harga * item.qty) * 0.10;
    }
    // Elektronik: beli >= 2 dapat diskon 5%
    if (item.kategori === "Elektronik" && item.qty >= 2) {
        return (item.harga * item.qty) * 0.05;
    }
    return 0;
}

function renderKeranjang() {
    const tbody = document.getElementById("cart-list");

    if (keranjang.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #888; padding: 20px;">Keranjang belanja masih kosong</td></tr>`;
        hitungTotalPOS();
        return;
    }

    tbody.innerHTML = "";
    keranjang.forEach((item, index) => {
        const sub = item.harga * item.qty;
        const disc = hitungDiskonItem(item);
        tbody.innerHTML += `
            <tr>
                <td><strong>${item.nama}</strong></td>
                <td>
                    <input type="number" min="1" value="${item.qty}" style="width: 60px; padding: 4px;" onchange="updateQtyKeranjang(${index}, this.value)">
                </td>
                <td>${formatRp(sub)}</td>
                <td class="${disc > 0 ? "text-danger" : ""}">
                    ${disc > 0 ? "-" + formatRp(disc) : "Rp 0"}
                </td>
                <td>
                    <button class="btn btn-danger" style="padding: 4px 8px;" onclick="hapusDariKeranjang(${index})">&times;</button>
                </td>
            </tr>
        `;
    });

    hitungTotalPOS();
}

function updateQtyKeranjang(index, val) {
    const qtyBaru = parseInt(val);
    if (isNaN(qtyBaru) || qtyBaru <= 0) {
        renderKeranjang();
        return;
    }

    const item = keranjang[index];
    const produk = katalog.find(p => p.kode === item.kode);
    if (produk && qtyBaru > produk.stok) {
        alert("Jumlah melebihi stok yang tersedia (Maksimal: " + produk.stok + ")!");
        renderKeranjang();
        return;
    }

    item.qty = qtyBaru;
    renderKeranjang();
}

function hapusDariKeranjang(index) {
    keranjang.splice(index, 1);
    renderKeranjang();
}

function resetKeranjang() {
    keranjang = [];
    document.getElementById("pos-voucher").value = "0";
    document.getElementById("pos-bayar").value = "";
    renderKeranjang();
}

function hitungTotalPOS() {
    let subtotal = 0;
    let totalDiskon = 0;

    keranjang.forEach(item => {
        subtotal += (item.harga * item.qty);
        totalDiskon += hitungDiskonItem(item);
    });

    const voucher = parseFloat(document.getElementById("pos-voucher").value) || 0;
    let totalAkhir = (subtotal - totalDiskon) - voucher;
    if (totalAkhir < 0) totalAkhir = 0;

    document.getElementById("pos-subtotal").innerText = formatRp(subtotal);
    document.getElementById("pos-diskon").innerText = "-" + formatRp(totalDiskon);
    document.getElementById("pos-total").innerText = formatRp(totalAkhir);

    return { subtotal, totalDiskon, voucher, totalAkhir };
}

function prosesPembayaran() {
    if (keranjang.length === 0) {
        alert("Keranjang belanja masih kosong!");
        return;
    }

    const totals = hitungTotalPOS();
    const uangBayar = parseFloat(document.getElementById("pos-bayar").value) || 0;

    if (uangBayar < totals.totalAkhir) {
        alert("Uang yang dibayarkan kurang! Kurang: " + formatRp(totals.totalAkhir - uangBayar));
        return;
    }

    const kembalian = uangBayar - totals.totalAkhir;
    const noNota = "TRX-" + String(counterNota++).padStart(4, "0");
    const sekarang = new Date().toLocaleString("id-ID");

    // Kurangi stok barang
    keranjang.forEach(item => {
        const p = katalog.find(prod => prod.kode === item.kode);
        if (p) p.stok -= item.qty;
    });
    simpanKatalogStorage();

    // Buat Struk Nota
    let teksStruk = `========================================\n`;
    teksStruk += `        STRUK PEMBAYARAN KASIR          \n`;
    teksStruk += `      KASIR TOKO SERBAMART HAFIZ        \n`;
    teksStruk += `========================================\n`;
    teksStruk += `No. Nota : ${noNota}\n`;
    teksStruk += `Tanggal  : ${sekarang}\n`;
    teksStruk += `----------------------------------------\n`;
    teksStruk += `Item              Qty    Harga     Total\n`;
    teksStruk += `----------------------------------------\n`;

    keranjang.forEach(item => {
        let nama = item.nama;
        if (nama.length > 15) nama = nama.substring(0, 15);
        teksStruk += `${nama.padEnd(16)} x${String(item.qty).padEnd(3)} ${formatRp(item.harga).padStart(10)} ${formatRp(item.harga * item.qty).padStart(10)}\n`;
        const d = hitungDiskonItem(item);
        if (d > 0) {
            teksStruk += `  >> Diskon Promo: -${formatRp(d)}\n`;
        }
    });

    teksStruk += `----------------------------------------\n`;
    teksStruk += `Subtotal Kotor   : ${formatRp(totals.subtotal).padStart(20)}\n`;
    if (totals.totalDiskon > 0) {
        teksStruk += `Diskon Produk    : -${formatRp(totals.totalDiskon).padStart(19)}\n`;
    }
    if (totals.voucher > 0) {
        teksStruk += `Voucher Diskon   : -${formatRp(totals.voucher).padStart(19)}\n`;
    }
    teksStruk += `----------------------------------------\n`;
    teksStruk += `TOTAL TAGIHAN    : ${formatRp(totals.totalAkhir).padStart(20)}\n`;
    teksStruk += `Uang Diterima    : ${formatRp(uangBayar).padStart(20)}\n`;
    teksStruk += `Kembalian        : ${formatRp(kembalian).padStart(20)}\n`;
    teksStruk += `========================================\n`;
    teksStruk += `      Terima Kasih Telah Berbelanja!    \n`;
    teksStruk += `========================================\n`;

    // Simpan ke riwayat transaksi
    riwayat.unshift({
        noNota: noNota,
        tanggal: sekarang,
        itemCount: keranjang.length,
        total: totals.totalAkhir,
        struk: teksStruk
    });
    simpanRiwayatStorage();

    // Tampilkan Modal Struk
    document.getElementById("isi-struk").innerText = teksStruk;
    document.getElementById("modal-struk").style.display = "flex";

    // Reset Form POS & render ulang
    resetKeranjang();
    renderKatalogPOS();
    renderKatalogCRUD();
    renderRiwayat();
}

function tutupModalStruk() {
    document.getElementById("modal-struk").style.display = "none";
}

// -------------------------------------------------------------
// CRUD PRODUK (CREATE, READ, UPDATE, DELETE)
// -------------------------------------------------------------
function renderKatalogCRUD() {
    const tbody = document.getElementById("crud-product-list");
    tbody.innerHTML = "";

    katalog.forEach(p => {
        const badgeClass = p.kategori === "Makanan" ? "badge-food" : "badge-elec";
        tbody.innerHTML += `
            <tr>
                <td><strong>${p.kode}</strong></td>
                <td><span class="badge ${badgeClass}">${p.kategori}</span></td>
                <td>${p.nama}</td>
                <td>${formatRp(p.harga)}</td>
                <td>${p.stok}</td>
                <td><small>${p.info}</small></td>
                <td>
                    <button class="btn btn-outline" style="padding: 4px 8px; margin-right: 4px;" onclick="siapkanEditProduk('${p.kode}')">✏️ Edit</button>
                    <button class="btn btn-danger" style="padding: 4px 8px;" onclick="hapusProduk('${p.kode}')">🗑️ Hapus</button>
                </td>
            </tr>
        `;
    });
}

function updateLabelKategoriCRUD() {
    const kat = document.getElementById("crud-kategori").value;
    const lbl = document.getElementById("crud-lbl-info");
    const input = document.getElementById("crud-info");

    if (kat === "Makanan") {
        lbl.innerText = "Tanggal Kadaluarsa (dd-MM-yyyy):";
        input.placeholder = "Contoh: 31-12-2027";
    } else {
        lbl.innerText = "Masa Garansi (Bulan):";
        input.placeholder = "Contoh: 12";
    }
}

function simpanProdukCRUD(event) {
    event.preventDefault();

    const mode = document.getElementById("crud-mode").value;
    const kode = document.getElementById("crud-kode").value.trim().toUpperCase();
    const nama = document.getElementById("crud-nama").value.trim();
    const kategori = document.getElementById("crud-kategori").value;
    const harga = parseFloat(document.getElementById("crud-harga").value) || 0;
    const stok = parseInt(document.getElementById("crud-stok").value) || 0;
    let info = document.getElementById("crud-info").value.trim();

    if (!info) {
        info = kategori === "Makanan" ? "Exp: 31-12-2027" : "Garansi: 12 Bln";
    } else {
        info = kategori === "Makanan" ? "Exp: " + info : "Garansi: " + info + " Bln";
    }

    if (mode === "create") {
        // Cek duplikasi kode
        if (katalog.some(p => p.kode === kode)) {
            alert("Gagal: Kode produk '" + kode + "' sudah digunakan!");
            return;
        }

        katalog.push({ kode, kategori, nama, harga, stok, info });
        alert("Produk '" + nama + "' berhasil ditambahkan!");
    } else {
        // Mode Update
        const target = katalog.find(p => p.kode === kode);
        if (target) {
            target.nama = nama;
            target.harga = harga;
            target.stok = stok;
            target.info = info;
            alert("Data produk '" + kode + "' berhasil diperbarui!");
        }
    }

    simpanKatalogStorage();
    resetFormCRUD();
    renderKatalogPOS();
    renderKatalogCRUD();
}

function siapkanEditProduk(kode) {
    const p = katalog.find(item => item.kode === kode);
    if (!p) return;

    document.getElementById("crud-mode").value = "update";
    document.getElementById("crud-form-title").innerText = "Perbarui Data Produk (Update)";
    document.getElementById("crud-btn-submit").innerText = "✏️ Update Produk";

    document.getElementById("crud-kode").value = p.kode;
    document.getElementById("crud-kode").readOnly = true;
    document.getElementById("crud-kategori").value = p.kategori;
    document.getElementById("crud-kategori").disabled = true;

    document.getElementById("crud-nama").value = p.nama;
    document.getElementById("crud-harga").value = p.harga;
    document.getElementById("crud-stok").value = p.stok;

    const rawInfo = p.info.replace("Exp: ", "").replace("Garansi: ", "").replace(" Bln", "");
    document.getElementById("crud-info").value = rawInfo;

    updateLabelKategoriCRUD();
}

function hapusProduk(kode) {
    if (!confirm("Apakah Anda yakin ingin menghapus produk '" + kode + "'?")) return;

    katalog = katalog.filter(p => p.kode !== kode);
    simpanKatalogStorage();
    renderKatalogPOS();
    renderKatalogCRUD();
    alert("Produk '" + kode + "' berhasil dihapus!");
}

function resetFormCRUD() {
    document.getElementById("form-crud").reset();
    document.getElementById("crud-mode").value = "create";
    document.getElementById("crud-form-title").innerText = "Tambah Produk Baru (Create)";
    document.getElementById("crud-btn-submit").innerText = "+ Tambah Produk";
    document.getElementById("crud-kode").readOnly = false;
    document.getElementById("crud-kategori").disabled = false;
    updateLabelKategoriCRUD();
}

// -------------------------------------------------------------
// RIWAYAT TRANSAKSI
// -------------------------------------------------------------
function renderRiwayat() {
    const tbody = document.getElementById("riwayat-list");

    if (riwayat.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #888; padding: 20px;">Belum ada riwayat transaksi</td></tr>`;
        return;
    }

    tbody.innerHTML = "";
    riwayat.forEach((trx, idx) => {
        tbody.innerHTML += `
            <tr>
                <td><strong>${trx.noNota}</strong></td>
                <td>${trx.tanggal}</td>
                <td>${trx.itemCount} jenis barang</td>
                <td><strong>${formatRp(trx.total)}</strong></td>
                <td>
                    <button class="btn btn-outline" style="padding: 4px 10px;" onclick="cetakUlangStruk(${idx})">🧾 Cetak Struk</button>
                </td>
            </tr>
        `;
    });
}

function cetakUlangStruk(index) {
    const t = riwayat[index];
    if (!t) return;
    document.getElementById("isi-struk").innerText = t.struk;
    document.getElementById("modal-struk").style.display = "flex";
}

// Jalankan aplikasi saat file selesai dimuat
window.onload = initApp;
