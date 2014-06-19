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
package project.main

import java.awt.FlowLayout
import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.SINGLE_COLUMN

application() {
    panel(id: 'mainPanel') {
        borderLayout()

        panel(constraints: PAGE_START) {
            button('Refresh', actionPerformed: controller.refresh)
        }

        scrollPane(constraints: CENTER) {
            glazedTable(id: 'table', list: model.pesanList, sortingStrategy: SINGLE_COLUMN) {
                glazedColumn(name: 'Tanggal', property: 'tanggal', width: 140) {
                    templateRenderer(exp: { it?.toString('dd-MM-yyyy HH:mm:ss') })
                }
                glazedColumn(name: 'Prioritas', property: 'prioritas', width: 100)
                glazedColumn(name: 'Pesan', property: 'pesan')
            }
        }

        panel(constraints: PAGE_END) {
            flowLayout(alignment: FlowLayout.LEADING)
            button('Hapus', actionPerformed: controller.hapus, visible: bind{table.isRowSelected})
        }
    }
}