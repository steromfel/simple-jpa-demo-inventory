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
package project.retur

import domain.event.PerubahanRetur
import domain.event.PerubahanStok
import domain.event.PerubahanStokTukar
import domain.event.PesanStok
import domain.event.TransaksiSistem
import domain.exception.DataDuplikat
import domain.exception.DataTidakBolehDiubah
import domain.exception.StokTidakCukup
import domain.faktur.Referensi
import domain.inventory.DaftarBarang
import domain.inventory.ItemBarang
import domain.inventory.Produk
import domain.inventory.ReferensiStok
import domain.inventory.ReferensiStokBuilder
import domain.labarugi.KATEGORI_SISTEM
import domain.penjualan.FakturJualOlehSales
import domain.penjualan.PengeluaranBarang
import domain.retur.*
import org.joda.time.LocalDate
import project.penjualan.FakturJualRepository
import project.user.NomorService
import simplejpa.transaction.Transaction

@Transaction
class ReturJualRepository {

    NomorService nomorService
    FakturJualRepository fakturJualRepository
    
    List<ReturJual> cariReturOlehSales(LocalDate tanggalMulaiSearch, LocalDate tanggalSelesaiSearch, String nomorSearch, String konsumenSearch, Boolean sudahDiprosesSearch, boolean excludeDeleted = false) {
        findAllReturJualOlehSalesByDsl([orderBy: 'tanggal,nomor', excludeDeleted: excludeDeleted]) {
            if (!nomorSearch) {
                tanggal between(tanggalMulaiSearch, tanggalSelesaiSearch)
            } else {
                nomor like("%${nomorSearch}%")
            }
            if (konsumenSearch) {
                and()
                konsumen__nama like("%${konsumenSearch}%")
            }
            if (sudahDiprosesSearch != null) {
                and()
                sudahDiproses eq(sudahDiprosesSearch)
            }
        }
    }

    List<ReturJual> cariReturEceran(LocalDate tanggalMulaiSearch, LocalDate tanggalSelesaiSearch, String nomorSearch, String konsumenSearch, Boolean sudahDiprosesSearch, boolean excludeDeleted = false) {
        findAllReturJualEceranByDsl([orderBy: 'tanggal,nomor', excludeDeleted: excludeDeleted]) {
            if (!nomorSearch) {
                tanggal between(tanggalMulaiSearch, tanggalSelesaiSearch)
            } else {
                nomor like("%${nomorSearch}%")
            }
            if (konsumenSearch) {
                and()
                namaKonsumen like("%${konsumenSearch}%")
            }
            if (sudahDiprosesSearch != null) {
                and()
                sudahDiproses eq(sudahDiprosesSearch)
            }
        }
    }

	public ReturJual buat(ReturJual returJual) {
		if (returJual.nomor && findReturJualByNomor(returJual.nomor)) {
			throw new DataDuplikat(returJual)
		}
        if (returJual.items.any { ItemRetur i -> i.klaims.empty }) {
            throw new IllegalStateException('Terdapat item retur yang belum memiliki informasi klaim!')
        }
        if (returJual instanceof ReturJualOlehSales) {
            returJual = buatReturOlehSales(returJual)
        } else if (returJual instanceof ReturJualEceran) {
            returJual = buatReturEceran(returJual)
        }

        def app = ApplicationHolder.application

        // Khusus untuk retur jual yang bukan kirim dari gudang utama, barang yang ditukar dianggap langsung dikirim.
        if (returJual instanceof ReturJualOlehSales && !returJual.gudang.utama && !returJual.getKlaimsTukar().empty) {
            tukar(returJual)
        }

        if (returJual.bisaDijualKembali) {

            // Khusus untuk barang retur yang masih dijual kembali, tidak perlu mempengaruhi qty retur (retur beli).
            // Barang retur dalam kondisi bagus sehingga masuk kedalam inventory untuk dijual kembali.
            ReferensiStok ref = new ReferensiStokBuilder(returJual).buat()
            app?.event(new PerubahanStok(returJual.toDaftarBarang(), ref))

        } else {

            // Barang retur perlu menambah qty retur beli karena perlu diproses untuk di-retur beli.
            app?.event(new PerubahanRetur(returJual))

        }

        app?.event(new PesanStok(returJual))

        // Periksa apakah klaim servis bisa dilakukan (bila ada klaim servis)
        DaftarBarang daftarKlaimServis = returJual.getDaftarBarangServis(true)
        if (!daftarKlaimServis.items.empty) {
            daftarKlaimServis.items.each { ItemBarang i ->
                if (i.jumlah > i.produk.jumlahTukar) {
                    throw new StokTidakCukup(i.produk.nama, i.jumlah, i.produk.jumlahTukar, null, StokTidakCukup.JENIS_STOK.STOK_TUKAR)
                }
            }
            app?.event(new PerubahanStokTukar(daftarKlaimServis))
            returJual.prosesKlaimServis()
        }

        // Proses tukar tambah dan tukar uang (bila ada)
        returJual.getKlaims(KlaimTambahBayaran).each { KlaimTambahBayaran k ->
            app?.event(new TransaksiSistem(k.jumlah, returJual.nomor, KATEGORI_SISTEM.PENDAPATAN_TUKAR_BARANG))
            returJual.proses(k)
        }
        returJual.getKlaims(KlaimTukarUang).each { KlaimTukarUang k ->
            app?.event(new TransaksiSistem(k.jumlah, returJual.nomor, KATEGORI_SISTEM.PENGELUARAN_TUKAR_BARANG))
            returJual.proses(k)
        }

        returJual
	}

    private ReturJualOlehSales buatReturOlehSales(ReturJualOlehSales returJualOlehSales) {
        // Periksa apakah barang yang di-klaim tersedia
        returJualOlehSales.getKlaimsTukar().each { KlaimTukar k ->
            Produk produk = findProdukById(k.produk.id)
            if (returJualOlehSales.gudang.utama) {
                if (!produk.tersediaUntuk(k.jumlah)) {
                    throw new StokTidakCukup(produk.nama, k.jumlah, produk.jumlahReadyGudangUtama(), returJualOlehSales.gudang)
                }
            } else {
                int jumlahStokGudang = produk.stok(returJualOlehSales.gudang).jumlah
                if (k.jumlah > jumlahStokGudang) {
                    throw new StokTidakCukup(produk.nama, k.jumlah, jumlahStokGudang, returJualOlehSales.gudang)
                }
            }
        }
        returJualOlehSales.nomor = nomorService.buatNomor(NomorService.TIPE.RETUR_JUAL_SALES)
        returJualOlehSales.konsumen = findKonsumenById(returJualOlehSales.konsumen.id)
        returJualOlehSales.items.each { it.produk = findProdukById(it.produk.id) }
        persist(returJualOlehSales)
        returJualOlehSales.potongPiutang()
        returJualOlehSales
    }

    private ReturJualEceran buatReturEceran(ReturJualEceran returJualEceran) {
        // Periksa apakah barang yang di-klaim tersedia
        returJualEceran.getKlaimsTukar().each { KlaimTukar k ->
            Produk produk = findProdukById(k.produk.id)
            if (!produk.tersediaUntuk(k.jumlah)) {
                throw new StokTidakCukup(produk.nama, k.jumlah, produk.jumlahReadyGudangUtama(), null)
            }
        }
        returJualEceran.nomor = nomorService.buatNomor(NomorService.TIPE.RETUR_JUAL_ECERAN)
        returJualEceran.items.each { it.produk = findProdukById(it.produk.id) }
        persist(returJualEceran)
        returJualEceran
    }

	public ReturJual update(ReturJual returJual) {
		ReturJual mergedRetur = findReturJualById(returJual.id)
		if (!mergedRetur) {
			throw new DataTidakBolehDiubah(returJual)
		}
		mergedRetur.with {
			nomor = returJual.nomor
			tanggal = returJual.tanggal
			keterangan = returJual.keterangan
		}
		mergedRetur
	}

    public ReturJual hapus(ReturJual returJual) {
        returJual = findReturJualById(returJual.id)
        if (!returJual) {
            throw new DataTidakBolehDiubah(returJual)
        }
        if (returJual.pengeluaranBarang != null) {
            throw new DataTidakBolehDiubah(returJual)
        }
        def app = ApplicationHolder.application
        app?.event(new PerubahanRetur(returJual, true))
        if (returJual instanceof ReturJualOlehSales) {
            // Hapus piutang khusus untuk retur jual oleh sales
            returJual.konsumen = findKonsumenById(returJual.konsumen.id)
            returJual.fakturPotongPiutang.each { Referensi r ->
                FakturJualOlehSales f = findFakturJualOlehSalesByNomor(r.nomor)
                f.hapusPembayaran(returJual.nomor)
            }
        }
        app?.event(new PesanStok(returJual, true))
        DaftarBarang daftarKlaimServis = returJual.getDaftarBarangServis()
        if (!daftarKlaimServis.items.empty) {
            app?.event(new PerubahanStokTukar(daftarKlaimServis, true))
        }

        // Hapus tukar tambah dan tukar uang (bila ada)
        returJual.getKlaims(KlaimTambahBayaran).each { KlaimTambahBayaran k ->
            app?.event(new TransaksiSistem(k.jumlah, "Invers hapus [${returJual.nomor}]", KATEGORI_SISTEM.PENDAPATAN_TUKAR_BARANG, true))
        }
        returJual.getKlaims(KlaimTukarUang).each { KlaimTukarUang k ->
            app?.event(new TransaksiSistem(k.jumlah, "Invers hapus [${returJual.nomor}]", KATEGORI_SISTEM.PENGELUARAN_TUKAR_BARANG, true))
        }

        returJual.deleted = 'Y'
        returJual
    }

    public ReturJual hapusPengeluaranBarang(ReturJual returJual) {
        returJual = findReturJualById(returJual.id)
        PengeluaranBarang pengeluaranBarang = returJual.pengeluaranBarang
        ReferensiStok ref = new ReferensiStokBuilder(pengeluaranBarang, returJual).buat()
        ApplicationHolder.application?.event(new PerubahanStok(pengeluaranBarang, ref, true, true))
        returJual.hapusPenukaran()
        returJual
    }

    public ReturJual tukar(ReturJual returJual) {
        returJual = findReturJualById(returJual.id)
        returJual.getKlaimsTukar(true).each { it.produk = findProdukById(it.produk.id) }
        PengeluaranBarang pengeluaranBarang = returJual.tukar()
        persist(pengeluaranBarang)
        returJual
    }

}

