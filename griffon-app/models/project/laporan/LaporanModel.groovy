/*
 * Copyright 2014 Jocki Hendry.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package project.laporan

import org.jdesktop.swingx.combobox.EnumComboBoxModel

class LaporanModel {

    EnumComboBoxModel<JenisLaporan> jenisLaporanSearch = new EnumComboBoxModel<JenisLaporan>(JenisLaporan)

}

public enum JenisLaporan {

    LAPORAN_PEMBELIAN_SUPPLIER('Laporan Pembelian Per Supplier', 'laporan_pembelian_supplier', 'laporanPembelianPerSupplier'),
    LAPORAN_PENJUALAN_SALES('Laporan Penjualan Per Sales', 'laporan_penjualan_sales', 'laporanPenjualanPerSales'),
    LAPORAN_PENJUALAN_REGION('Laporan Penjualan Per Region', 'laporan_penjualan_region', 'laporanPenjualanPerRegion'),
    LAPORAN_PENJUALAN_KONSUMEN('Laporan Penjualan Per Konsumen', 'laporan_penjualan_konsumen', 'laporanPenjualanPerKonsumen'),
    LAPORAN_PENJUALAN_PRODUK('Laporan Penjualan Produk', 'laporan_penjualan_produk', 'laporanPenjualanProduk'),
    LAPORAN_PENJUALAN_TRIWULAN('Laporan Penjualan Triwulan', 'laporan_penjualan_sales_triwulan', 'laporanPenjualanTriwulan'),
    LAPORAN_SISA_PIUTANG('Laporan Sisa Piutang', 'laporan_sisa_piutang', 'laporanSisaPiutang'),
    LAPORAN_SISA_HUTANG('Laporan Sisa Hutang', 'laporan_sisa_hutang', 'laporanSisaHutang'),
    LAPORAN_PEMBAYARAN_PIUTANG('Laporan Pembayaran Piutang', 'laporan_pembayaran_piutang_per_sales', 'laporanPembayaranPiutang'),
    LAPORAN_STOK('Laporan Summary Stok', 'laporan_stok', 'laporanStok'),
    KARTU_STOK('Kartu Stok', 'kartu_stok', 'kartuStok'),
    LAPORAN_STOK_GUDANG('Laporan Stok Per Gudang', 'laporan_stok_gudang', 'laporanStokGudang'),
    LAPORAN_STOK_SUPPLIER('Laporan Stok Per Supplier', 'laporan_stok_supplier', 'laporanStokSupplier'),
    SURAT_JALAN('Surat Jalan', 'laporan_surat_jalan', 'laporanSuratJalan'),
    LAPORAN_RETURJUAL_KONSUMEN('Laporan Retur Jual Per Konsumen', 'laporan_retur_jual_konsumen', 'laporanReturJualPerKonsumen'),
    LAPORAN_RETURBELI_SUPPLIER('Laporan Retur Beli Per Supplier', 'laporan_retur_beli_supplier', 'laporanReturBeliPerSupplier'),
    LAPORAN_RETUR_PRODUK('Laporan Retur Produk', 'laporan_retur_produk', 'laporanReturProduk'),
    LAPORAN_TRANSAKSI_KAS('Laporan Transaksi Kas', 'laporan_kas_kategori', 'laporanTransaksiKas'),
    LAPORAN_NILAI_INVENTORY('Laporan Nilai Inventory', 'laporan_nilai_inventory', 'laporanNilaiInventory' ),
    LAPORAN_LABA_RUGI('Laporan Laba Rugi', 'laporan_laba_rugi', 'laporanLabaRugi')

    String nama
    String namaLaporan
    String namaMVC

    public JenisLaporan(String nama, String namaLaporan, String namaMVC) {
        this.nama = nama
        this.namaLaporan = namaLaporan
        this.namaMVC = namaMVC
    }

    @Override
    String toString() {
        nama
    }

}