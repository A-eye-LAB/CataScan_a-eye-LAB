package org.cataract.web.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.DataDownloadService;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.Patient;
import org.cataract.web.domain.Report;
import org.cataract.web.helper.AgeHelper;
import org.cataract.web.infra.InstitutionRepository;
import org.cataract.web.infra.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@Profile("prod")
public class DataDownloadServiceImpl implements DataDownloadService {

    private final InstitutionRepository institutionRepository;

    private final PatientRepository patientRepository;

    public DataDownloadServiceImpl(InstitutionRepository institutionRepository, PatientRepository patientRepository) {

        this.institutionRepository = institutionRepository;
        this.patientRepository = patientRepository;
    }

    @Value("${app.s3.bucket}")
    private String s3Bucket;

    @Override
    public void downloadImageData(List<Integer> institutionIds, OutputStream outputStream) {

        S3Client s3Client;
        Map<Institution, List<Patient>> groupedByInstitution = new HashMap<>();
        for (Integer institutionId : institutionIds) {
            institutionRepository.findById(institutionId).ifPresent(institution -> {
                groupedByInstitution.put(institution, patientRepository.findAllByInstitution(institution));
            });
        }
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (Map.Entry<Institution, List<Patient>> entry : groupedByInstitution.entrySet()) {
                String institutionName = entry.getKey().getName();
                List<Patient> institutionPatients = entry.getValue();
                s3Client =  S3Client.builder().region(Region.of(entry.getKey().getImageStorage().getBucketRegion())).build();
                String folder = institutionName + "/";
                String csvContent = buildCsv(entry.getValue());
                // Add CSV entry
                zipOutputStream.putNextEntry(new ZipEntry(folder + institutionName + "patients_reports.csv"));
                zipOutputStream.write(csvContent.getBytes(StandardCharsets.UTF_8));
                zipOutputStream.closeEntry();

                Path rightImagePath = null;
                Path leftImagePath = null;
                // Add photos
                for (Patient patient : institutionPatients) {
                    for (Report report : patient.getReports()) {
                        if (report.getRImagePath() != null) {
                            String s3Key = report.getRImagePath(); // S3 path from DB
                            String filename = Paths.get(s3Key).getFileName().toString();
                            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder()
                                    .bucket(s3Bucket)
                                    .key(s3Key)
                                    .build());

                            zipOutputStream.putNextEntry(new ZipEntry(folder + filename));
                            s3Object.transferTo(zipOutputStream);
                            try (InputStream in = Files.newInputStream(rightImagePath)) {
                                in.transferTo(zipOutputStream);
                            }
                            zipOutputStream.closeEntry();
                        }
                        if (report.getLImagePath() != null) {
                            String s3Key = report.getLImagePath(); // S3 path from DB
                            String filename = Paths.get(s3Key).getFileName().toString();
                            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder()
                                    .bucket(s3Bucket)
                                    .key(s3Key)
                                    .build());

                            zipOutputStream.putNextEntry(new ZipEntry(folder + filename));
                            s3Object.transferTo(zipOutputStream);
                            try (InputStream in = Files.newInputStream(leftImagePath)) {
                                in.transferTo(zipOutputStream);
                            }
                            zipOutputStream.closeEntry();
                        }
                    }
                }
            }
            zipOutputStream.finish();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }


    @Override
    public byte[] downloadImageDataByteArr(List<Integer> institutionIds) {
        return new byte[0];
    }


    private String buildCsv(List<Patient> patients) {
        StringBuilder sb = new StringBuilder();
        sb.append("patient_pk,file_name,age,sex,eye_side,doctor_diagnosis,ai_diagnosis,uploaded_at\n");
        // TODO: CSV 파일 정보에 맞춰서 만들기.
        for (Patient patient : patients) {
            if (patient.getReports() != null && !patient.getReports().isEmpty()) {
                int thenAge = -1;
                for (Report report : patient.getReports()) {
                    thenAge = AgeHelper.calculateAge(patient.getDateOfBirth(), report.getScanDate().toLocalDate());
                    if (report.getLImagePath() != null) {
                        sb.append(String.format("%d,%s,%d,%s,%s,%s,%s\n",
                                patient.getPatientPk(),
                                report.getLImagePath().substring(report.getLImagePath().lastIndexOf('/')),
                                thenAge,
                                patient.getSex(),
                                "Left",
                                report.getLDiagnosis(),
                                report.getLAiResult(),
                                report.getScanDate().toString()
                        ));
                    }
                    if (report.getRImagePath() != null) {
                        sb.append(String.format("%d,%s,%d,%s,%s,%s,%s\n",
                                patient.getPatientPk(),
                                report.getRImagePath().substring(report.getRImagePath().lastIndexOf('/')),
                                thenAge,
                                patient.getSex(),
                                "Right",
                                report.getRDiagnosis(),
                                report.getRAiResult(),
                                report.getScanDate().toString()
                        ));
                    }
                }
            }
        }

        return sb.toString();
    }
}
