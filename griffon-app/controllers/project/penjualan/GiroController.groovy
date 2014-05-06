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

import domain.Container
import domain.exception.DataDuplikat
import domain.exception.DataTidakBolehDiubah
import domain.faktur.BilyetGiro
import domain.penjualan.BilyetGiroRepository
import org.joda.time.LocalDate

import javax.swing.JOptionPane
import javax.swing.event.ListSelectionEvent
import javax.validation.groups.Default

class GiroController {

    GiroModel model
    def view

    BilyetGiroRepository bilyetGiroRepository

    void mvcGroupInit(Map args) {
        bilyetGiroRepository = Container.app.bilyetGiroRepository
        model.popupMode = args.'popupMode'?: false
        init()
        search()
    }

    def init = {
        model.tanggalMulaiSearch = LocalDate.now().minusMonths(1)
        model.tanggalSelesaiSearch = LocalDate.now()
    }

    def search = {
        List bilyetGiro = bilyetGiroRepository.cari(model.tanggalMulaiSearch, model.tanggalSelesaiSearch, model.nomorSeriSearch)
        execInsideUISync {
            model.bilyetGiroList.clear()
            model.bilyetGiroList.addAll(bilyetGiro)
        }
    }

    def save = {
        BilyetGiro bilyetGiro = new BilyetGiro(id: model.id, nomorSeri: model.nomorSeri, tanggalPenerbitan: model.tanggalPenerbitan,
                                               nominal: model.nominal, tanggalEfektif: model.tanggalEfektif)

        if (!bilyetGiroRepository.validate(bilyetGiro, Default, model)) return

        try {
            if (bilyetGiro.id == null) {
                bilyetGiro = bilyetGiroRepository.buat(bilyetGiro)
                execInsideUISync {
                    model.bilyetGiroList << bilyetGiro
                    view.table.changeSelection(model.bilyetGiroList.size() - 1, 0, false, false)
                    clear()
                }
            } else {
                bilyetGiro = bilyetGiroRepository.update(bilyetGiro)
                execInsideUISync {
                    view.table.selectionModel.selected[0] = bilyetGiro
                    clear()
                }
            }
        } catch (DataDuplikat ex) {
            model.errors['nomorSeri'] = app.getMessage("simplejpa.error.alreadyExist.message")
        } catch (DataTidakBolehDiubah ex) {
            JOptionPane.showMessageDialog(view.mainPanel, 'Bilyet giro tidak boleh diubah karena sudah diproses!', 'Penyimpanan Gagal', JOptionPane.ERROR_MESSAGE)
        }
    }

    def cairkan = {
        if (JOptionPane.showConfirmDialog(view.mainPanel, 'Anda yakin sudah mencairkan bilyet giro ini?', 'Pencairan Bilyet Giro', JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return
        }
        try {
            BilyetGiro bilyetGiro = view.table.selectionModel.selected[0]
            bilyetGiro = bilyetGiroRepository.cairkan(bilyetGiro)
            execInsideUIAsync {
                view.table.selectionModel.selected[0] = bilyetGiro
            }
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(view.mainPanel, ex.message, 'Kesalahan Pencairan Giro', JOptionPane.ERROR_MESSAGE)
        }
    }

    def delete = {
        try {
            BilyetGiro bilyetGiro = view.table.selectionModel.selected[0]
            bilyetGiro = bilyetGiroRepository.hapus(bilyetGiro)

            execInsideUISync {
                view.table.selectionModel.selected[0] = bilyetGiro
                clear()
            }
        } catch (DataTidakBolehDiubah ex) {
            JOptionPane.showMessageDialog(view.mainPanel, ex.message, 'Kesalahan Hapus Giro', JOptionPane.ERROR_MESSAGE)
        }
    }

    def clear = {
        execInsideUISync {
            model.id = null
            model.nomorSeri = null
            model.tanggalPenerbitan = null
            model.tanggalEfektif = null
            model.tanggalPencairan = null
            model.nominal = null

            model.errors.clear()
            view.table.selectionModel.clearSelection()
        }
    }

    def tableSelectionChanged = { ListSelectionEvent event ->
        execInsideUISync {
            if (view.table.selectionModel.isSelectionEmpty()) {
                clear()
            } else {
                BilyetGiro selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.nomorSeri = selected.nomorSeri
                model.nominal = selected.nominal
                model.tanggalPenerbitan = selected.tanggalPenerbitan
                model.tanggalEfektif = selected.tanggalEfektif
                model.tanggalPencairan = selected.tanggalPencairan
            }
        }
    }

}