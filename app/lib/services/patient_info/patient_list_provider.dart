import 'dart:async';
import 'dart:io';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cata_scan_flutter/models/patient_info.dart';
import 'package:cata_scan_flutter/services/storage/patient_info_storage.dart';

final patientListProvider =
    AsyncNotifierProvider<PatientListNotifier, List<PatientInfo>>(() {
  return PatientListNotifier();
});

class PatientListNotifier extends AsyncNotifier<List<PatientInfo>> {
  late final PatientInfoStorage _storage;

  @override
  FutureOr<List<PatientInfo>> build() async {
    _storage = ref.read(patientInfoStorageProvider);
    return _getValidPatientList();
  }

  Future<List<PatientInfo>> _getValidPatientList() async {
    final result = await _storage.getPatientInfoList();
    
    // 이미지 파일 유효성 검사
    List<PatientInfo> validPatients = [];
    List<String> invalidPatientIds = [];
    
    for (var patient in result) {
      final leftImageFile = File(patient.eyeScanData.leftEyeScanPath);
      final rightImageFile = File(patient.eyeScanData.rightEyeScanPath);
      
      if (await leftImageFile.exists() && await rightImageFile.exists()) {
        validPatients.add(patient);
      } else {
        invalidPatientIds.add(patient.eyeScanData.id);
      }
    }
    
    // 유효하지 않은 데이터 삭제
    for (var id in invalidPatientIds) {
      await _storage.deletePatientInfo(id);
    }
    
    return validPatients;
  }

  Future<void> refresh() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() => _getValidPatientList());
  }
}
