package com.example.supervisionapp.data.model;

public class ThesisModel {
    private final long thesisId;
    private final String title;
    private final String subTitle;
    private final SupervisoryStateModel supervisoryState;
    private final SupervisoryTypeModel supervisoryType;
    private final String studentName;
    private final String expose;
    private final ThesisStateModel thesisState;
    private final boolean hasSecondSupervisor;
    private final InvoiceStateModel invoiceState;

    public ThesisModel(long thesisId, String title, String subTitle, SupervisoryStateModel supervisoryState, SupervisoryTypeModel supervisoryType, String studentName, String expose, ThesisStateModel thesisState, boolean hasSecondSupervisor, InvoiceStateModel invoiceState) {
        this.thesisId = thesisId;
        this.title = title;
        this.subTitle = subTitle;
        this.supervisoryState = supervisoryState;
        this.supervisoryType = supervisoryType;
        this.studentName = studentName;
        this.expose = expose;
        this.thesisState = thesisState;
        this.hasSecondSupervisor = hasSecondSupervisor;
        this.invoiceState = invoiceState;
    }

    public long getThesisId() {
        return thesisId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public SupervisoryStateModel getSupervisoryState() {
        return supervisoryState;
    }

    public SupervisoryTypeModel getSupervisoryType() {
        return supervisoryType;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getExpose() {
        return expose;
    }

    public ThesisStateModel getThesisState() {
        return thesisState;
    }

    public boolean hasSecondSupervisor() {
        return hasSecondSupervisor;
    }

    public InvoiceStateModel getInvoiceState() {
        return invoiceState;
    }
}
