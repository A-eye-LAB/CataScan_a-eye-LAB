package org.cataract.web.domain;

public enum AiResult {

    UNGRADABLE("ungradable"),
    REQUIRES_ATTENTION("requiresAttention"),
    LOW_RISK("lowRisk"),
    NEED_FOR_CAUTION("needForCaution");

    private final String label;

    AiResult(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static AiResult fromLabel(String label) {
        for (AiResult aiResult : values()) {
            if (aiResult.label.equalsIgnoreCase(label)) {
                return aiResult;
            }
        }
        throw new IllegalArgumentException("Invalid AiResult: " + label);
    }

    @Override
    public String toString() {
        return this.getLabel();
    }
}
