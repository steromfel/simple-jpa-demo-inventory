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
package project

import domain.Container
import domain.faktur.ItemFaktur
import domain.pembelian.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.ListComboBoxModel
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class FakturBeliModel {

    @Bindable Long id
    @Bindable String nomor
    @Bindable LocalDate tanggal
    @Bindable BigDecimal diskonPotonganPersen
    @Bindable BigDecimal diskonPotonganLangsung
    @Bindable String keterangan
    BasicEventList<Supplier> supplierList = new BasicEventList<>()
    @Bindable DefaultEventComboBoxModel<Supplier> supplier = GlazedListsSwing.eventComboBoxModelWithThreadProxyList(supplierList)
    List<ItemFaktur> listItemFaktur = []

    BasicEventList<FakturBeli> fakturBeliList = new BasicEventList<>()

    @Bindable String nomorSearch
    @Bindable String supplierSearch
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    ListComboBoxModel statusSearch = new ListComboBoxModel(Container.app.searchEnum(StatusFakturBeli))

    @Bindable String informasi

}