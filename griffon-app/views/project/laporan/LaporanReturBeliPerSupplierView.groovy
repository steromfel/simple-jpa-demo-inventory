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

import java.awt.event.KeyEvent

actions {
    action(id: 'reset', name: 'Reset', closure: controller.reset, mnemonic: KeyEvent.VK_R)
}

panel(id: 'mainPanel', layout: new MigLayout('hidemode 2', '[right][left,grow]', '')) {
    label('Periode')
    panel(constraints: 'wrap') {
        flowLayout()
        dateTimePicker(id: 'tanggalMulaiCari', localDate: bind('tanggalMulaiCari', target: model, mutual: true),
                dateVisible: true, timeVisible: false, focusable: true)
        label(" s/d ")
        dateTimePicker(id: 'tanggalSelesaiCari', localDate: bind('tanggalSelesaiCari', target: model, mutual: true),
                dateVisible: true, timeVisible: false, focusable: true)
    }

    label('Dan', constraints: 'wrap')

    label('Nama Supplier')
    comboBox(id: 'supplier', model: model.supplier, templateRenderer: '${value.nama}', constraints: 'wrap')

    panel(constraints: 'span, growx, wrap') {
        button('OK', id: 'defaultButton', defaultCapable: true, actionPerformed: controller.tampilkanLaporan)
        button(action: reset)
        button('Batal', actionPerformed: controller.batal)
    }
}