package com.example.supervisionapp.data.model;

import com.example.supervisionapp.R;

public enum SupervisionRequestTypeModel {
    SUPERVISION(R.string.SupervisionRequestTypeModel_SUPERVISION),
    SECOND_SUPERVISOR(R.string.SupervisionRequestTypeModel_SECOND_SUPERVISOR);

    private final int resourceId;

    SupervisionRequestTypeModel(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
