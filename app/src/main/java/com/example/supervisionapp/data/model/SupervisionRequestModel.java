package com.example.supervisionapp.data.model;

public class SupervisionRequestModel {
    private final long thesisId;
    private final long requestingUserId;
    private final String title;
    private final String subTitle;
    private final String studentName;
    private final String firstSupervisorName;
    private final SupervisionRequestTypeModel requestType;
    private final String description;
    private final String expose;

    public SupervisionRequestModel(
            long thesisId,
            long requestingUserId,
            String title,
            String subTitle,
            String studentName,
            String firstSupervisorName,
            SupervisionRequestTypeModel requestType,
            String description,
            String expose) {
        this.thesisId = thesisId;
        this.requestingUserId = requestingUserId;
        this.title = title;
        this.subTitle = subTitle;
        this.studentName = studentName;
        this.firstSupervisorName = firstSupervisorName;
        this.expose = expose;
        this.requestType = requestType;
        this.description = description;
    }

    public long getThesisId() {
        return thesisId;
    }

    public long getRequestingUserId() {
        return requestingUserId;
    }

    public String getTitle() {
        return title;
    }

    public String getStudentName() {
        return studentName;
    }

    public SupervisionRequestTypeModel getRequestType() {
        return requestType;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getFirstSupervisorName() {
        return firstSupervisorName;
    }

    public String getExpose() {
        return expose;
    }

    public String getDescription() {
        return description;
    }
}
