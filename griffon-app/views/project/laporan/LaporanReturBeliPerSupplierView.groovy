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

import net.miginfocom.swing.MigLayout

panel(id: 'mainPanel', layout: new MigLayout('hidemode 2', '[right][left,grow]', '')) {
    label('Periode')
    panel(constraints: 'wrap') {
        flowLayout()
        dateTimePicker(id: 'tanggalMulaiCari', localDate: bind('tanggalMulaiCari', target: model, mutual: true),
                dateVisible: true, timeVisible: false)
        label(" s/d ")
        dateTimePicker(id: 'tanggalSelesaiCari', localDate: bind('tanggalSelesaiCari', target: model, mutual: true),
                dateVisible: true, timeVisible: false)
    }

    label('Dan', constraints: 'wrap')
    label('Nama Supplier')
    textField(text: bind('supplierSearch', target: model, mutual: true), columns: 20, constraints: 'wrap')

    panel(constraints: 'span, growx, wrap') {
        button('OK', actionPerformed: controller.tampilkanLaporan)
        button('Batal', actionPerformed: controller.batal)
    }
}