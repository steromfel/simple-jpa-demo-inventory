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
import domain.exception.DataDuplikat
import domain.exception.DataTidakBolehDiubah
import domain.inventory.DaftarBarangSementara
import domain.inventory.ItemBarang
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
    
    List<ReturJual> cari(LocalDate tanggalMulaiSearch, LocalDate tanggalSelesaiSearch, String nomorSearch, String konsumenSearch, Boolean sudahDiprosesSearch) {
        findAllReturJualByDsl([orderBy: 'tanggal,nomor', excludeDeleted: false]) {
            tanggal between(tanggalMulaiSearch, tanggalSelesaiSearch)
            if (nomorSearch) {
                and()
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

	public ReturJual buat(ReturJual returJual) {
		if (findReturJualByNomor(returJual.nomor)) {
			throw new DataDuplikat(returJual)
		}

        // Validasi barang yang ditukar harus sama atau kurang dari barang yang diretur
        List<ItemBarang> items = returJual.normalisasi()
        List<ItemBarang> klaims = new DaftarBarangSementara(returJual.listKlaimRetur).normalisasi()
        klaims.each { ItemBarang itemKlaim ->
            if (itemKlaim.produk) {
                if (items.find { (it.produk == itemKlaim.produk) && (it.jumlah >= itemKlaim.jumlah) } == null) {
                    throw new IllegalStateException("Tidak dapat menukar lebih dari yang di-retur: ${itemKlaim.produk.nama} sejumlah ${itemKlaim.jumlah}")
                }
            }
        }

        returJual.nomor = nomorService.buatNomor(NomorService.TIPE.RETUR_JUAL)
        returJual.konsumen = findKonsumenById(returJual.konsumen.id)
        returJual.items.each { it.produk = findProdukById(it.produk.id) }
		persist(returJual)
        returJual.potongPiutang()
        ApplicationHolder.application?.event(new PerubahanRetur(returJual))
		returJual
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
            gudang = returJual.gudang
		}
		mergedRetur
	}

    public ReturJual hapus(ReturJual returJual) {
        returJual = findReturJualById(returJual.id)
        if (!returJual || returJual.pengeluaranBarang) {
            throw new DataTidakBolehDiubah(returJual)
        }
        ApplicationHolder.application?.event(new PerubahanRetur(returJual, true))
        returJual.konsumen = findKonsumenById(returJual.konsumen.id)
        fakturJualRepository.cariPiutang(returJual.nomor).each { FakturJualOlehSales f ->
            f.hapusPembayaran(returJual.nomor)
        }
        returJual.deleted = 'Y'
        returJual
    }

    public ReturJual tukar(ReturJual returJual) {
        returJual = findReturJualById(returJual.id)
        PengeluaranBarang pengeluaranBarang = returJual.tukar()
        persist(pengeluaranBarang)
        ApplicationHolder.application?.event(new PerubahanStok(pengeluaranBarang, null))
        returJual.sudahDiproses = true
        returJual
    }

}

