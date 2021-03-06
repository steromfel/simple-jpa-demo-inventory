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
package project.faktur

import net.miginfocom.swing.MigLayout
import java.awt.FlowLayout
import java.awt.event.KeyEvent
import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.SINGLE_COLUMN
import static javax.swing.SwingConstants.RIGHT

panel(id: 'mainPanel') {
    borderLayout()

    panel(constraints: CENTER) {
        borderLayout()
        scrollPane(constraints: CENTER) {
            glazedTable(id: 'table', list: model.itemFakturList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                glazedColumn(name: 'Produk', property: 'produk', width: [200, 200, 400]) {
                    templateRenderer('${it.nama}')
                }
                glazedColumn(name: 'Qty', property: 'jumlah', columnClass: Integer, width: 40)
                glazedColumn(name: 'Satuan', expression: { it.produk.satuan.singkatan }, width: 40)
                glazedColumn(name: 'Harga', property: 'harga', columnClass: Integer, visible: bind { model.showHarga }) {
                    templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                }
                glazedColumn(name: 'Keterangan', property: 'keterangan', width: [40, 40, 500])
                glazedColumn(name: 'Diskon', property: 'diskon', columnClass: Integer, visible: bind { model.showHarga }, width: [40, 100, 100])
                glazedColumn(name: 'Jumlah Diskon', expression: {it.diskon?.jumlah(it.harga)?.multiply(it.jumlah)},
                        columnClass: Integer, visible: bind { model.showHarga }) {
                    templateRenderer("\${it==null?'-':currencyFormat(it)}", horizontalAlignment: RIGHT)
                }
                glazedColumn(name: 'Total', expression: {it.total()}, columnClass: Integer, visible: bind { model.showHarga }) {
                    templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                }
            }
        }
    }

    taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), visible: bind { model.editable }, constraints: PAGE_END) {
        label('Produk:')
        panel {
            label(text: bind {model.produk?: '- kosong -'})
            button('Cari Produk...', id: 'cariProduk', errorPath: 'produk', mnemonic: KeyEvent.VK_P, actionPerformed: controller.showProduk)
        }
        errorLabel(path: 'produk', constraints: 'wrap')
        label('Qty:')
        numberTextField(id: 'jumlah', columns: 10, bindTo: 'jumlah', errorPath: 'jumlah')
        errorLabel(path: 'jumlah', constraints: 'wrap')
        label('Harga:')
        decimalTextField(id: 'harga', columns: 20, bindTo: 'harga', nfParseBigDecimal: true, errorPath: 'harga')
        errorLabel(path: 'harga', constraints: 'wrap')
        label('Diskon:')
        panel(layout: new FlowLayout(FlowLayout.LEADING, 0, 0)) {
            decimalTextField(id: 'diskonPotonganPersen', columns: 5, bindTo: 'diskonPotonganPersen', errorPath: 'diskonPotonganPersen')
            label('% dan Potongan Langsung Rp')
            decimalTextField(id: 'diskonPotonganLangsung', columns: 20, bindTo: 'diskonPotonganLangsung', errorPath: 'diskonPotonganLangsung')
        }
        errorLabel(path: 'diskon', constraints: 'wrap')
        label('Keterangan:')
        textField(id: 'keterangan', columns: 60, text: bind('keterangan', target: model, mutual: true), errorPath: 'keterangan')
        errorLabel(path: 'keterangan', constraints: 'wrap')

        panel(constraints: 'span, growx, wrap') {
            flowLayout(alignment: FlowLayout.LEADING)
            button('Simpan', actionPerformed: {
                if (!view.table.selectionModel.selectionEmpty) {
                    if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.update.message"),
                            app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                        return
                    }
                }
                controller.save()
                cariProduk.requestFocusInWindow()
            }, visible: bind{model.editable}, mnemonic: KeyEvent.VK_S)
            button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind('isRowSelected', source: table, converter: {it && model.editable}), actionPerformed: controller.clear, mnemonic: KeyEvent.VK_B)
            button(app.getMessage("simplejpa.dialog.delete.button"), visible: bind('isRowSelected', source: table, converter: {it && model.editable}), actionPerformed: {
                if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.delete.message"),
                        app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    controller.delete()
                }
            }, mnemonic: KeyEvent.VK_H)
            button(app.getMessage("simplejpa.dialog.close.button"), actionPerformed: {
                SwingUtilities.getWindowAncestor(mainPanel)?.dispose()
            }, mnemonic: KeyEvent.VK_T)
        }
    }
}