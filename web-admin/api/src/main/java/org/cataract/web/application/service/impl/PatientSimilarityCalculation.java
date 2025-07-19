package org.cataract.web.application.service.impl;

import org.cataract.web.domain.Patient;

public record PatientSimilarityCalculation(Patient patient, int distance) {

}
