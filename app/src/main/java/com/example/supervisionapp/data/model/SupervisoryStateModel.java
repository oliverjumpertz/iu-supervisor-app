package com.example.supervisionapp.data.model;

import com.example.supervisionapp.R;

public enum SupervisoryStateModel {
    DRAFT(0, R.string.SupervisoryStateModel_DRAFT),
    SUPERVISED(1, R.string.SupervisoryStateModel_SUPERVISED);

    private final int resourceId;
    private final int sortPosition;

    SupervisoryStateModel(int sortPosition, int resourceId) {
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
