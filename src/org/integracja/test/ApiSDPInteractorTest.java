package org.integracja.test;

import org.integracja.ApiSDPInteractor;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;

class ApiSDPInteractorTest {

    @org.junit.jupiter.api.Test
    void getPostionNames() {
        HashMap<Integer, String> pozycje_nazwy = ApiSDPInteractor.getPostionNames();
        Assertions.assertEquals(399, pozycje_nazwy.size());
    }

    @org.junit.jupiter.api.Test
    void getFormattedDataWojewodztwa() {
        HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, 2023);
        Assertions.assertEquals(16, wartosci.size());
    }

    @org.junit.jupiter.api.Test
    void getFormattedDataWojewodztwaDifferentYears() throws InterruptedException {
        for (int i = 2000; i < 2024; i++) {
            HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, i);
            Assertions.assertEquals(16, wartosci.size());
        }
    }

    @org.junit.jupiter.api.Test
    void getFormattedDataPolska() {
        HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.POLSKA, 2023);
        Assertions.assertEquals(1, wartosci.size());
    }

    @org.junit.jupiter.api.Test
    void getFormattedDataPowiaty() {
        HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.POWIATY, 2023);
        System.out.println(wartosci);
        Assertions.assertEquals(370, wartosci.size());
    }
}