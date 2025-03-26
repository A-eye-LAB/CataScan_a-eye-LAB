import 'package:cata_scan_flutter/core/providers/api_client_provider.dart';
import 'package:cata_scan_flutter/models/eye_scan_data.dart';
import 'package:cata_scan_flutter/services/patient_info/patient_list_provider.dart';
import 'package:cata_scan_flutter/services/storage/patient_info_storage.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cata_scan_flutter/models/patient_info.dart';
import 'package:cata_scan_flutter/models/gender.dart';
import 'package:cata_scan_flutter/services/api/api_client.dart';

final patientProvider = AsyncNotifierProvider<PatientNotifier, PatientInfo>(() {
  return PatientNotifier();
});

class PatientNotifier extends AsyncNotifier<PatientInfo> {
  late final PatientInfoStorage _storage;
  late final ApiClient _apiClient;

  @override
  Future<PatientInfo> build() async {
    _storage = ref.read(patientInfoStorageProvider);
    _apiClient = ref.read(apiClientProvider);

    return PatientInfo.initial();
  }

  Future<bool> uploadPatient(String leftEyePath, String rightEyePath,
      leftEyeResult, rightEyeResult) async {
    final String id =
        '${state.value?.name ?? ''}=${state.value?.gender?.text ?? ''}';
    await updateEyeScanData(
        id, leftEyePath, rightEyePath, leftEyeResult, rightEyeResult);
    final PatientInfo currentPatient = state.value!;

    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      await _apiClient.uploadEyeImage(
          leftFile: leftEyePath,
          rightFile: rightEyePath,
          imageId: id,
          leftResult: leftEyeResult.toString(),
          rightResult: rightEyeResult.toString());
      return PatientInfo.initial();
    });
    if (state.hasError) {
      await _storage.savePatientInfo(currentPatient);
      await ref.read(patientListProvider.notifier).refresh();
    }
    return state.hasError;
  }

  Future<void> updateName(String name) async {
    state = await AsyncValue.guard(() async {
      final current = state.value ?? PatientInfo.initial();
      return current.copyWith(name: name);
    });
  }

  Future<void> updateGender(Gender gender) async {
    state = AsyncData(state.value!.copyWith(gender: gender));
  }

  Future<void> updateComment(String comment) async {
    state = AsyncData(state.value!.copyWith(comment: comment));
  }

  Future<void> updateEyeScanData(String id, String leftEyePath,
      String rightEyePath, leftEyeResult, rightEyeResult) async {
    final eyeScanData = EyeScanData(
        id: id,
        rightEyeScanPath: rightEyePath,
        leftEyeScanPath: leftEyePath,
        rightStatus: rightEyeResult,
        leftStatus: leftEyeResult,
        timestamp: DateTime.now());
    state = AsyncData(state.value!.copyWith(eyeScanData: eyeScanData));
  }

  void initial() async {
    state = AsyncData(PatientInfo.initial());
  }
}
