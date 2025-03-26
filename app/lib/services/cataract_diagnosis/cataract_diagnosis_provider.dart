import 'package:cata_scan_flutter/models/prediction_result.dart';
import 'package:camera/camera.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'package:cata_scan_flutter/services/cataract_diagnosis/cataract_diagnosis_service.dart';
import 'package:cata_scan_flutter/services/image/cataract_image_processing_service.dart';

final cataractDiagnosisProvider =
    AsyncNotifierProvider.family.autoDispose<CataractDiagnosisNotifier, EyesCataractPredictionResult, ({String leftEyePath, String rightEyePath})>(CataractDiagnosisNotifier.new);

class CataractDiagnosisNotifier extends AutoDisposeFamilyAsyncNotifier<EyesCataractPredictionResult, ({String leftEyePath, String rightEyePath})> {
  @override
  Future<EyesCataractPredictionResult> build(({String leftEyePath, String rightEyePath}) arg) async {
    try {
      final diagnosisService = await ref.read(cataractDiagnosisServiceProvider.future);

      final results = await Future.wait([
        Future.delayed(const Duration(seconds: 3)),
        _processPrediction(arg, diagnosisService),
      ]);

      return results[1] as EyesCataractPredictionResult;
    } catch (error, stackTrace) {
      throw AsyncError('Failed to process predictions: $error', stackTrace);
    }
  }

  Future<EyesCataractPredictionResult> _processPrediction(
    ({String leftEyePath, String rightEyePath}) arg,
    CataractDiagnosisService diagnosisService,
  ) async {
    final imageService = ref.read(imageProcessingServiceProvider);

    try {
      final leftEyeBytes = await XFile(arg.leftEyePath).readAsBytes();
      final rightEyeBytes = await XFile(arg.rightEyePath).readAsBytes();

      final leftProcessed = imageService.processImageForPrediction(leftEyeBytes);
      final rightProcessed = imageService.processImageForPrediction(rightEyeBytes);

      final leftPrediction = await diagnosisService.predictSingleEye(leftProcessed);
      final rightPrediction = await diagnosisService.predictSingleEye(rightProcessed);

      return EyesCataractPredictionResult(
        leftEye: leftPrediction,
        rightEye: rightPrediction,
      );
    } catch (e) {
      rethrow;
    }
  }
}
