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
package domain.pembelian

import domain.faktur.Faktur
import domain.inventory.DaftarBarang
import domain.inventory.ItemBarang
import domain.validation.TanpaGudang
import groovy.transform.*
import simplejpa.DomainClass
import javax.persistence.*
import org.hibernate.annotations.Type
import javax.validation.constraints.*
import org.hibernate.validator.constraints.*
import org.joda.time.*

import javax.validation.groups.Default

@DomainClass @Entity @Canonical
class PenerimaanBarang extends DaftarBarang {

    @NotNull(groups=[Default,TanpaGudang]) @ManyToOne
    Supplier supplier

    @Override
    int faktor() {
        1
    }

    PenerimaanBarang plus(PenerimaanBarang penerimaanBarangLain) {
        PenerimaanBarang hasil = new PenerimaanBarang(nomor: this.nomor, tanggal: this.tanggal, keterangan: this.keterangan,
            faktur: this.faktur, supplier: this.supplier)

        hasil.listItemBarang = this.listItemBarang + penerimaanBarangLain.listItemBarang
        hasil.listItemBarang = hasil.normalisasi()

        hasil
    }

    boolean isiSamaDengan(DaftarBarang daftarBarangLain) {
        normalisasi().toSet() == daftarBarangLain.normalisasi().toSet()
    }

    boolean isiSamaDengan(Faktur faktur) {
        normalisasi().toSet() == faktur.normalisasi().toSet()
    }

}

