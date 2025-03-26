import 'package:cata_scan_flutter/models/patient_info.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:cata_scan_flutter/core/providers/flutter_secure_storage_provider.dart';
import 'dart:convert';

final patientInfoStorageProvider = Provider<PatientInfoStorage>((ref) {
  final secureStorage = ref.read(flutterSecureStorageProvider);
  return PatientInfoStorage(secureStorage);
});

class PatientInfoStorage {
  final FlutterSecureStorage _secureStorage;
  PatientInfoStorage(this._secureStorage);

  static const String _patientInfoListKey = 'patient_info_list';

  Future<bool> savePatientInfo(PatientInfo patientInfo) async {
    try {
      final exisitDataJson = await _secureStorage.read(key: _patientInfoListKey);
      List<PatientInfo> patientInfoList = [];

      if (exisitDataJson != null) {
        final List<dynamic> existingList = jsonDecode(exisitDataJson);
        patientInfoList = existingList.map((json) => PatientInfo.fromJson(json)).toList();
      }

      patientInfoList.add(patientInfo);
      final updatedListJson = jsonEncode(patientInfoList.map((data) => data.toJson()).toList());
      await _secureStorage.write(key: _patientInfoListKey, value: updatedListJson);
      return true;
    } catch (e) {
      return false;
    }
  }

  Future<bool> deletePatientInfo(String patientId) async {
    try {
      final existDataJson = await _secureStorage.read(key: _patientInfoListKey);
      if (existDataJson == null) {
        return false;
      }

      final List<dynamic> existingList = await jsonDecode(existDataJson);
      List<PatientInfo> patientInfoList = existingList.map((json) => PatientInfo.fromJson(json)).cast<PatientInfo>().toList();

      final initialLength = patientInfoList.length;
      patientInfoList.removeWhere((patient) => patient.eyeScanData.id == patientId);

      if (patientInfoList.length == initialLength) {
        return false;
      }

      final updatedListJson = jsonEncode(patientInfoList.map((data) => data.toJson()).toList());
      await _secureStorage.write(key: _patientInfoListKey, value: updatedListJson);

      return true;
    } catch (e) {
      return false;
    }
  }

  Future<List<PatientInfo>> getPatientInfoList() async {
    try {
      final exisitDataJson = await _secureStorage.read(key: _patientInfoListKey);
      List<PatientInfo> patientInfoList = [];

      if (exisitDataJson != null) {
        final List<dynamic> existingList = await jsonDecode(exisitDataJson);
        patientInfoList = existingList.map((json) => PatientInfo.fromJson(json)).toList();
      }
      return patientInfoList;
    } catch (e) {
      rethrow;
    }
  }
}
