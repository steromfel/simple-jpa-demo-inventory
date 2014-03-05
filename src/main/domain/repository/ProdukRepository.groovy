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

package domain.repository

import domain.PeriodeItemStok
import domain.Produk
import domain.StokProduk
import domain.exception.DataDuplikat
import simplejpa.transaction.Transaction

@Transaction
class ProdukRepository {

    /**
     * Menghapus data item stok lama dan memberi tanda bahwa operasi untuk masa waktu stok tersebut
     * tidak boleh dilakukan lagi.  Setelah pengarsipan, data <code>PeriodeItemStok</code> menjadi
     * tidak memiliki <code>ItemStok</code> (dan tidak dapat dimodifikasi lagi).
     *
     * @param deltaTahun adalah rentang waktu tahun yang akan diarsip, paling cepat adalah 3 tahun yang lalu.
     */
    public arsipItemStok(int deltaTahun) {
        if (deltaTahun < 3) {
            throw new IllegalArgumentException('Masa pengarsipan paling cepat adalah 3 tahun yang lalu')
        }

        List daftarProduk = findAllProduk()
        for (Produk p: daftarProduk) {
            for (StokProduk s: p.daftarStok) {
                for (PeriodeItemStok pi: s.periodeUntukArsip(deltaTahun)) {
                    pi.arsip = true
                    pi.listItemStok.clear()
                }
            }
        }
    }

    public Produk buat(Produk produk) {
        if (findProdukByNama(produk.nama)) {
            throw new DataDuplikat(produk)
        }
        persist(produk)
        produk
    }

}
