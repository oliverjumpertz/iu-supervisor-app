package com.example.supervisionapp.persistence;

import com.example.supervisionapp.data.model.SupervisoryStateModel;

public interface SupervisoryStatePredicate {
    boolean test(SupervisoryStateModel supervisoryStateModel);
}
