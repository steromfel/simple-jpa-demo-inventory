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
package laporan

import groovy.transform.Canonical

@Canonical
class PenjualanSales {

    String namaSales
    BigDecimal bulan1
    BigDecimal bulan2
    BigDecimal bulan3

    BigDecimal jumlahSelisih() {
        (bulan3?:0) - (bulan2?:0)
    }

    BigDecimal persenSelisih() {
        if (bulan2 == null) {
            return 1
        } else if (bulan2 == 0) {
            return 0
        } else {
            return (bulan3 - bulan2) / bulan2
        }
    }

}
