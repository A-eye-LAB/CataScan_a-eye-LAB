import 'package:cata_scan_flutter/models/prediction_result.dart';
import 'package:cata_scan_flutter/services/eye_detection/eye_detection_service.dart';
import 'package:cata_scan_flutter/services/image/cataract_image_processing_service.dart';
import 'package:camera/camera.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

final eyeDetectionProvider = AsyncNotifierProvider.family.autoDispose<EyeDetectionNotifier, EyePredictionResult, ({String leftEyePath, String rightEyePath})>(EyeDetectionNotifier.new);

class EyeDetectionNotifier extends AutoDisposeFamilyAsyncNotifier<EyesPredictionResult, ({String leftEyePath, String rightEyePath})> {
  @override
  Future<EyesPredictionResult> build(({String leftEyePath, String rightEyePath}) arg) async {
    try {
      final imageService = ref.read(imageProcessingServiceProvider);

      final leftEyeBytes = await XFile(arg.leftEyePath).readAsBytes();
      final rightEyeBytes = await XFile(arg.rightEyePath).readAsBytes();

      final leftProcessed = imageService.processImageForPrediction(leftEyeBytes);
      final rightProcessed = imageService.processImageForPrediction(rightEyeBytes);
      final detectionService = await ref.read(eyeDetectionServiceProvider.future);

      final leftPrediction = await detectionService.predictSingleEye(leftProcessed);
      final rightPrediction = await detectionService.predictSingleEye(rightProcessed);

      return EyesPredictionResult(leftEye: leftPrediction, rightEye: rightPrediction);
    } catch (error, stackTrace) {
      throw AsyncError('Failed to process predictions: $error', stackTrace);
    }
  }
}
