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
package project.penjualan

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import java.awt.*
import org.jdesktop.swingx.prompt.PromptSupport

panel(id: 'mainPanel') {
    borderLayout()

    panel(constraints: PAGE_START) {
        flowLayout(alignment: FlowLayout.LEADING)
        textField(id: 'namaSearch', columns: 20, text: bind('namaSearch', target: model, mutual: true), actionPerformed: controller.search)
        button(app.getMessage('simplejpa.search.label'), actionPerformed: controller.search)
    }

    scrollPane(constraints: CENTER) {
        glazedTable(id: 'table', list: model.salesList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
            glazedColumn(name: 'Nama', property: 'nama')
            glazedColumn(name: 'Nomor Telepon', property: 'nomorTelepon')
            glazedColumn(name: 'Gudang', property: 'gudang')
            glazedColumn(name: 'Kode', property: 'kode')
        }
    }

    panel(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), constraints: PAGE_END, focusCycleRoot: true) {
        label('Nama:')
        textField(id: 'nama', columns: 50, text: bind('nama', target: model, mutual: true), errorPath: 'nama')
        errorLabel(path: 'nama', constraints: 'wrap')
        label('Nomor Telepon:')
        textField(id: 'nomorTelepon', columns: 20, text: bind('nomorTelepon', target: model, mutual: true), errorPath: 'nomorTelepon')
        errorLabel(path: 'nomorTelepon', constraints: 'wrap')
        label('Gudang:')
        comboBox(id: 'gudang', model: model.gudang, templateRenderer: '${value}', errorPath: 'gudang')
        errorLabel(path: 'gudang', constraints: 'wrap')
        label('Kode:')
        textField(id: 'kode', columns: 15, text: bind('kode', target: model, mutual: true), errorPath: 'kode')
        errorLabel(path: 'kode', constraints: 'wrap')

        panel(constraints: 'span, growx, wrap') {
            flowLayout(alignment: FlowLayout.LEADING)
            button(app.getMessage("simplejpa.dialog.save.button"), actionPerformed: {
                if (model.id != null) {
                    if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.update.message"),
                            app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                        return
                    }
                }
                controller.save()
                form.getFocusTraversalPolicy().getFirstComponent(form).requestFocusInWindow()
            })
            button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind {
                table.isRowSelected
            }, actionPerformed: controller.clear)
            button(app.getMessage("simplejpa.dialog.delete.button"), visible: bind {
                table.isRowSelected
            }, actionPerformed: {
                if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.delete.message"),
                        app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    controller.delete()
                }
            })
        }
    }
}

PromptSupport.setPrompt("Cari Nama...", namaSearch)