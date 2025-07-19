package org.cataract.web.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.DataDownloadService;
import org.cataract.web.domain.Patient;
import org.cataract.web.domain.Report;
import org.cataract.web.helper.AgeHelper;
import org.cataract.web.infra.InstitutionRepository;
import org.cataract.web.infra.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class LocalDataDownloadService implements DataDownloadService {

    private final InstitutionRepository institutionRepository;

    private final PatientRepository patientRepository;

    @Value("${app.image.filepath}")
    private String reportPath;


    @Value("${app.image.baseurl}")
    private String imageUrlPath;

    @Value("${app.host.url}")
    private String hostUrl;

    @Value("${app.host.port}")
    private String hostPort;


    public void downloadImageData(List<Integer> institutionIds, OutputStream out) {

        Map<String, List<Patient>> groupedByInstitution = new HashMap<>();
        for (Integer institutionId : institutionIds) {
            institutionRepository.findById(institutionId).ifPresent(institution -> {
                groupedByInstitution.put(institution.getName(), patientRepository.findAllByInstitution(institution));
            });
        }
        // TODO: 파일 확실히 기관별 폴더에 밀어놓고 확인하기.
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
            String baseUrlPath = new StringBuilder(hostUrl).append(":").append(hostPort).append(imageUrlPath).toString();

            for (Map.Entry<String, List<Patient>> entry : groupedByInstitution.entrySet()) {
                String institutionName = entry.getKey();
                List<Patient> institutionPatients = entry.getValue();

                String folder = institutionName + "/";
                String csvContent = buildCsv(entry.getValue());
                // Add CSV entry
                zipOutputStream.putNextEntry(new ZipEntry(folder + "patients_reports.csv"));
                zipOutputStream.write(csvContent.getBytes(StandardCharsets.UTF_8));
                zipOutputStream.closeEntry();

                Path rightImagePath = null;
                Path leftImagePath = null;
                String imagePath = "";
                // Add photos
                for (Patient patient : institutionPatients) {
                    for (Report report : patient.getReports()) {
                        if (report.getRImagePath() != null) {
                            imagePath = report.getRImagePath().replace(baseUrlPath.toString(), reportPath);
                            rightImagePath = Path.of(imagePath);
                            if (Files.exists(rightImagePath)) {
                                zipOutputStream.putNextEntry(
                                        new ZipEntry(folder + imagePath
                                                .substring(imagePath.lastIndexOf('/')+1)));
                                try (InputStream in = Files.newInputStream(rightImagePath)) {
                                    in.transferTo(zipOutputStream);
                                }
                                zipOutputStream.closeEntry();
                            }
                        }
                        if (report.getLImagePath() != null) {
                            imagePath = report.getLImagePath().replace(baseUrlPath.toString(), reportPath);
                            leftImagePath = Path.of(imagePath);
                            if (Files.exists(leftImagePath)) {
                                zipOutputStream.putNextEntry(
                                        new ZipEntry(folder + imagePath
                                                .substring(imagePath.lastIndexOf('/')+1)));
                                try (InputStream in = Files.newInputStream(leftImagePath)) {
                                    in.transferTo(zipOutputStream);
                                }
                                zipOutputStream.closeEntry();
                            }
                        }
                    }
                }
            }
            zipOutputStream.finish();


        } catch (Exception e) {
            log.error("error making the downloaded report", e);
            e.printStackTrace();
        }
    }


    public byte[] downloadImageDataByteArr(List<Integer> institutionIds) {


        Map<String, List<Patient>> groupedByInstitution = new HashMap<>();
        for (Integer institutionId : institutionIds) {
            institutionRepository.findById(institutionId).ifPresent(institution -> {
                groupedByInstitution.put(institution.getName(), patientRepository.findAllByInstitution(institution));
            });
        }
        // TODO: 파일 확실히 기관별 폴더에 밀어놓고 확인하기.
        try (ByteArrayOutputStream zipBuffer = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(zipBuffer)) {
            String baseUrlPath = new StringBuilder(hostUrl).append(":").append(hostPort).append(imageUrlPath).toString();

            for (Map.Entry<String, List<Patient>> entry : groupedByInstitution.entrySet()) {
                String institutionName = entry.getKey();
                List<Patient> institutionPatients = entry.getValue();

                String folder = institutionName + "/";
                String csvContent = buildCsv(entry.getValue());
                // Add CSV entry
                zipOutputStream.putNextEntry(new ZipEntry(folder + institutionName + "_patients_reports.csv"));
                zipOutputStream.write(csvContent.getBytes(StandardCharsets.UTF_8));
                zipOutputStream.closeEntry();

                Path rightImagePath = null;
                Path leftImagePath = null;
                String imagePath = "";
                // Add photos
                for (Patient patient : institutionPatients) {
                    for (Report report : patient.getReports()) {
                        if (report.getRImagePath() != null) {
                            imagePath = report.getRImagePath().replace(baseUrlPath.toString(), reportPath);
                            rightImagePath = Path.of(imagePath);
                            if (Files.exists(rightImagePath)) {
                                zipOutputStream.putNextEntry(
                                        new ZipEntry(folder + imagePath
                                                .substring(imagePath.lastIndexOf('/')+1)));
                                try (InputStream in = Files.newInputStream(rightImagePath)) {
                                    in.transferTo(zipOutputStream);
                                }
                                zipOutputStream.closeEntry();
                            }
                        }
                        if (report.getLImagePath() != null) {
                            imagePath = report.getLImagePath().replace(baseUrlPath.toString(), reportPath);
                            leftImagePath = Path.of(imagePath);
                            if (Files.exists(leftImagePath)) {
                                zipOutputStream.putNextEntry(
                                        new ZipEntry(folder + imagePath
                                                .substring(imagePath.lastIndexOf('/')+1)));
                                try (InputStream in = Files.newInputStream(leftImagePath)) {
                                    in.transferTo(zipOutputStream);
                                }
                                zipOutputStream.closeEntry();
                            }
                        }
                    }
                }
            }

            zipOutputStream.finish();
            return zipBuffer.toByteArray();

        } catch (Exception e) {
            log.error("error making the downloaded report", e);
            e.printStackTrace();
        }
        return null;
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
