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

import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class LaporanModel {

    EnumComboBoxModel<JenisLaporan> jenisLaporanSearch = new EnumComboBoxModel<JenisLaporan>(JenisLaporan)

}

public enum JenisLaporan {

    LAPORAN_PENJUALAN_SALES('Laporan Penjualan Per Sales', 'laporan_penjualan_sales', 'laporanPenjualanPerSales'),
    LAPORAN_PENJUALAN_REGION('Laporan Penjualan Per Region', 'laporan_penjualan_region', 'laporanPenjualanPerRegion'),
    LAPORAN_PENJUALAN_KONSUMEN('Laporan Penjualan Per Konsumen', 'laporan_penjualan_konsumen', 'laporanPenjualanPerKonsumen'),
    LAPORAN_SISA_PIUTANG('Laporan Sisa Piutang', 'laporan_sisa_piutang', 'laporanSisaPiutang'),
    LAPORAN_STOK('Laporan Stok', 'laporan_stok', 'laporanStok')

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