package com.example.supervisionapp.data.model;

import com.example.supervisionapp.R;

public enum SupervisoryTypeModel {
    FIRST_SUPERVISOR(R.string.SupervisoryTypeList_FIRST_SUPERVISOR),
    SECOND_SUPERVISOR(R.string.SupervisoryTypeList_SECOND_SUPERVISOR);

    private final int resourceId;

    SupervisoryTypeModel(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
