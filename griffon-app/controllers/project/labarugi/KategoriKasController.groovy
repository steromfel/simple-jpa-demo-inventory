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
package project.labarugi

import ast.NeedSupervisorPassword
import domain.labarugi.*
import simplejpa.exception.DuplicateEntityException
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.validation.groups.Default

@SuppressWarnings("GroovyUnusedDeclaration")
class KategoriKasController {

	KategoriKasModel model
	def view
    KategoriKasRepository kategoriKasRepository

	void mvcGroupInit(Map args) {
		execInsideUISync {
			model.namaSearch = null
			model.jenisSearch.selectedItem = null
		}
		search()
	}

	def search = {
        List result = kategoriKasRepository.cari(model.namaSearch, model.jenisSearch.selectedItem)
        execInsideUISync {
            model.kategoriKasList.clear()
            model.kategoriKasList.addAll(result)
        }
	}

	def save = {
        if (model.id!=null) {
            if (JOptionPane.showConfirmDialog(view.mainPanel, app.getMessage("simplejpa.dialog.update.message"), app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                return
            }
        }
		KategoriKas kategoriKas = new KategoriKas(id: model.id, nama: model.nama, jenis: model.jenis.selectedItem, sistem: false, dipakaiDiLaporan: model.dipakaiDiLaporan)

		if (!kategoriKasRepository.validate(kategoriKas, Default, model)) return

        try {
		    if (model.id == null) {
			    // Insert operation
                kategoriKasRepository.buat(kategoriKas)
                execInsideUISync {
                    model.kategoriKasList << kategoriKas
                    view.table.changeSelection(model.kategoriKasList.size()-1, 0, false, false)
                }
            } else {
                // Update operation
                kategoriKas = kategoriKasRepository.update(kategoriKas)
			    execInsideUISync { view.table.selectionModel.selected[0] = kategoriKas }
		    }
        } catch (DuplicateEntityException ex) {
            model.errors['nama'] = app.getMessage('simplejpa.error.alreadyExist.message')
        }
		execInsideUISync {
            clear()
            view.form.getFocusTraversalPolicy().getFirstComponent(view.form).requestFocusInWindow()
        }
	}

    @NeedSupervisorPassword
    def delete = {
        if (JOptionPane.showConfirmDialog(view.mainPanel, app.getMessage("simplejpa.dialog.delete.message"), app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return
        }
        KategoriKas kategoriKas = view.table.selectionModel.selected[0]
        kategoriKas = kategoriKasRepository.hapus(kategoriKas)
        execInsideUISync {
            view.table.selectionModel.selected[0] = kategoriKas
            clear()
        }
    }

	def clear = {
		execInsideUISync {
			model.id = null
			model.nama = null
			model.jenis.selectedItem = null
			model.dipakaiDiLaporan = false
			model.created = null
			model.createdBy = null
			model.modified = null
			model.modifiedBy = null
            model.errors.clear()
			view.table.selectionModel.clearSelection()
		}
	}

	def tableSelectionChanged = { ListSelectionEvent event ->
		execInsideUISync {
			if (view.table.selectionModel.isSelectionEmpty()) {
				clear()
			} else {
				KategoriKas selected = view.table.selectionModel.selected[0]
				model.errors.clear()
				model.id = selected.id
				model.nama = selected.nama
				model.jenis.selectedItem = selected.jenis
				model.dipakaiDiLaporan = selected.dipakaiDiLaporan
				model.created = selected.createdDate
				model.createdBy = selected.createdBy?'('+selected.createdBy+')':null
				model.modified = selected.modifiedDate
				model.modifiedBy = selected.modifiedBy?'('+selected.modifiedBy+')':null
			}
		}
	}

}