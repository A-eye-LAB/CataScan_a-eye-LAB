package org.cataract.web.domain.exception;

public class ReportNotFoundException extends RuntimeException{
    public ReportNotFoundException(String message) {
        super(message);
    }

    public ReportNotFoundException() {
        super("Report not found or not part of your institution");
    }

}
