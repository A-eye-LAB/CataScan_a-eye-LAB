// ignore_for_file: avoid_public_notifier_properties
import 'dart:async';
import 'package:cata_scan_flutter/core/providers/api_client_provider.dart';
import 'package:cata_scan_flutter/services/api/api_client.dart';
import 'package:cata_scan_flutter/services/patient_info/patient_list_provider.dart';
import 'package:cata_scan_flutter/services/storage/patient_info_storage.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cata_scan_flutter/models/patient_info.dart';
import 'package:gap/gap.dart';

final patientListUploadProvider = AutoDisposeAsyncNotifierProvider<PatientListUploadNotifier, String>(() {
  return PatientListUploadNotifier();
});

class PatientListUploadNotifier extends AutoDisposeAsyncNotifier<String> {
  late final ApiClient _apiClient;
  late final PatientInfoStorage _storage;

  List<String> successPatientIds = [];

  @override
  FutureOr<String> build() {
    _apiClient = ref.read(apiClientProvider);
    _storage = ref.read(patientInfoStorageProvider);
    return 'waiting...';
  }

  Future<void> uploadListData(BuildContext context, List<PatientInfo> dataList) async {
    try {
      state = const AsyncValue.loading();

      if (context.mounted) {
        showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return PopScope(
              canPop: false,
              child: Consumer(
                builder: (context, ref, _) {
                  final uploadState = ref.watch(patientListUploadProvider);
                  return AlertDialog(
                    content: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const CircularProgressIndicator(),
                        const Gap(16),
                        Text(
                          uploadState.when(
                            data: (message) => message,
                            loading: () => 'Preparing upload...',
                            error: (error, _) => error.toString(),
                          ),
                        ),
                      ],
                    ),
                  );
                },
              ),
            );
          },
        );
      }

      for (int i = 0; i < dataList.length; i++) {
        if (!context.mounted) break;

        final data = dataList[i];
        try {
          state = AsyncValue.data('Uploading ${i + 1} of ${dataList.length}');

          await _apiClient.uploadEyeImage(
            leftFile: data.eyeScanData.leftEyeScanPath,
            rightFile: data.eyeScanData.rightEyeScanPath,
            imageId: data.eyeScanData.id,
            leftResult: data.eyeScanData.leftStatus.toString(),
            rightResult: data.eyeScanData.rightStatus.toString(),
          );

          await Future.delayed(const Duration(milliseconds: 500));

          state = AsyncValue.data('Completed: ${i + 1}/${dataList.length} (${((i + 1) / dataList.length * 100).toInt()}%)');

          successPatientIds.add(data.eyeScanData.id);

          await Future.delayed(const Duration(milliseconds: 300));
        } catch (e, stack) {
          state = AsyncValue.error('Failed: ${i + 1}/${dataList.length} - ${e.toString()}', stack);

          await Future.delayed(const Duration(seconds: 1));
          return;
        }
      }

      state = const AsyncValue.data('Upload complete!');
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('All uploads completed successfully!')),
        );
      }
    } finally {
      if (successPatientIds.isNotEmpty) {
        for (final id in successPatientIds) {
          await _storage.deletePatientInfo(id);
        }
        successPatientIds.clear();
        await ref.read(patientListProvider.notifier).refresh();
      }
      await Future.delayed(const Duration(seconds: 1));
      if (context.mounted) {
        Navigator.pop(context);
      }
    }
  }
}
