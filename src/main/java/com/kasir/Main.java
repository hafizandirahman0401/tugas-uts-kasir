package com.kasir;

import com.kasir.gui.KasirFrame;
import com.kasir.service.IKasirService;
import com.kasir.service.KasirService;

import javax.swing.*;

/**
 * =========================================================================
 * TITIK MASUK UTAMA APLIKASI KASIR (MAIN ENTRY POINT)
 * =========================================================================
 * Aplikasi Desktop Java murni berbasis Swing (JFrame) untuk NetBeans IDE.
 * Menerapkan prinsip PBO: Enkapsulasi, Abstraksi, Pewarisan, dan Polimorfisme.
 */
public class Main {

    public static void main(String[] args) {
        // Gunakan tampilan sistem native
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Inisialisasi Layanan Bisnis Kasir
        IKasirService kasirService = new KasirService();

        // Jalankan Jendela Aplikasi Desktop Java (GUI / JFrame)
        SwingUtilities.invokeLater(() -> {
            KasirFrame frame = new KasirFrame(kasirService);
            frame.setVisible(true);
        });

        System.out.println("[OK] Aplikasi Kasir Desktop Java (GUI) berhasil dijalankan.");
    }
}
