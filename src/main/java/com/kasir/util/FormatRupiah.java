package com.kasir.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Kelas utilitas untuk memformat angka desimal/nominal ke format Rupiah (Rp).
 */
public class FormatRupiah {

    private static final DecimalFormat formatRp;

    static {
        DecimalFormatSymbols simbol = new DecimalFormatSymbols();
        simbol.setCurrencySymbol("Rp ");
        simbol.setMonetaryDecimalSeparator(',');
        simbol.setGroupingSeparator('.');

        formatRp = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        formatRp.setDecimalFormatSymbols(simbol);
        formatRp.setMaximumFractionDigits(0);
    }

    /**
     * Mengubah angka nominal menjadi string berformat Rupiah (misal: 15000 -> Rp 15.000)
     * @param nominal angka yang akan diformat
     * @return string format Rupiah
     */
    public static String format(double nominal) {
        return formatRp.format(nominal);
    }
}
