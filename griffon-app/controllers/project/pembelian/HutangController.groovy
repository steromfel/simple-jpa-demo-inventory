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
package project.pembelian

import domain.Container
import domain.pembelian.PurchaseOrder
import domain.pembelian.PurchaseOrderRepository
import org.joda.time.LocalDate
import simplejpa.swing.DialogUtils

import javax.swing.event.ListSelectionEvent
import java.awt.Dimension

class HutangController {

    HutangModel model
    def view

    PurchaseOrderRepository purchaseOrderRepository

    void mvcGroupInit(Map args) {
        purchaseOrderRepository = Container.app.purchaseOrderRepository
        init()
        search()
    }

    def init = {
        execInsideUISync {
            model.tanggalMulaiSearch = LocalDate.now().minusMonths(1)
            model.tanggalSelesaiSearch = LocalDate.now()
            model.statusSearch.selectedItem = PurchaseOrderRepository.StatusHutangSearch.BELUM_LUNAS
        }
    }

    def search = {
        List result = purchaseOrderRepository.cariHutang(model.tanggalMulaiSearch, model.tanggalSelesaiSearch,
            model.nomorSearch, model.supplierSearch, model.chkJatuhTempo? model.jatuhTempoSearch: null, model.statusSearch.selectedItem)
        execInsideUISync {
            model.purchaseOrderList.clear()
            model.purchaseOrderList.addAll(result)
        }
    }

    def showPembayaran = {
        execInsideUISync {
            def args = [faktur: view.table.selectionModel.selected[0], listPembayaran: model.listPembayaranHutang]
            def dialogProps = [title: 'Pembayaran Hutang', size: new Dimension(900, 420)]
            DialogUtils.showMVCGroup('pembayaranAsChild', args, app, view, dialogProps) { m, v, c ->
                model.listPembayaranHutang.clear()
                model.listPembayaranHutang.addAll(m.pembayaranList)
                view.table.selectionModel.selected[0] = m.faktur
            }
        }
    }

    def clear = {
        execInsideUISync {
            model.id = null
            model.listPembayaranHutang.clear()

            model.errors.clear()
            view.table.selectionModel.clearSelection()
        }
    }

    def tableSelectionChanged = { ListSelectionEvent event ->
        execInsideUISync {
            if (view.table.selectionModel.isSelectionEmpty()) {
                clear()
            } else {
                PurchaseOrder selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.listPembayaranHutang.clear()
                if (selected?.fakturBeli?.hutang) {
                    model.listPembayaranHutang.addAll(selected.fakturBeli.hutang.listPembayaran)
                }
            }
        }
    }

}