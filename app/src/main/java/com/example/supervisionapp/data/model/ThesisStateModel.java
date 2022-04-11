package com.example.supervisionapp.data.model;

import com.example.supervisionapp.R;

public enum ThesisStateModel {
    ADVERTISED(0, R.string.ThesisStateModel_ADVERTISED),
    IN_PROGRESS(1, R.string.ThesisStateModel_IN_PROGRESS),
    TURNED_IN(2, R.string.ThesisStateModel_TURNED_IN),
    RATED(3, R.string.ThesisStateModel_RATED),
    FINISHED(4, R.string.ThesisStateModel_FINISHED);

    private final int resourceId;
    private final int sortPosition;

    ThesisStateModel(int sortPosition, int resourceId) {
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
