package com.example.supervisionapp.data.model;

import com.example.supervisionapp.R;

public enum InvoiceStateModel {
    UNFINISHED(0, R.string.InvoiceStateModel_UNFINISHED),
    INVOICE_SENT(1, R.string.InvoiceStateModel_INVOICE_SENT);

    private final int resourceId;
    private final int sortPosition;

    InvoiceStateModel(int sortPosition, int resourceId) {
        this.sortPosition = sortPosition;
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getSortPosition() {
        return sortPosition;
    }
}
