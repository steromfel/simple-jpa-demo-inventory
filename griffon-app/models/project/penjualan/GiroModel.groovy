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

import ca.odell.glazedlists.BasicEventList
import domain.faktur.BilyetGiro
import groovy.beans.Bindable
import org.joda.time.LocalDate

class GiroModel {

    @Bindable Long id
    @Bindable String nomorSeri
    @Bindable BigDecimal nominal
    @Bindable LocalDate tanggalPenerbitan
    @Bindable LocalDate tanggalEfektif
    @Bindable LocalDate tanggalPencairan

    @Bindable String nomorSeriSearch
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch

    BasicEventList<BilyetGiro> bilyetGiroList = new BasicEventList<>()

    @Bindable boolean popupMode

}