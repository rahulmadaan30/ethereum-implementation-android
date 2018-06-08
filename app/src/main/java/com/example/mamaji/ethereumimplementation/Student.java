package com.example.mamaji.ethereumimplementation;

import de.petendi.ethereum.android.contract.PendingTransaction;

public interface Student {

    default void get() {


    }

    PendingTransaction<Void> set(String data);
}
