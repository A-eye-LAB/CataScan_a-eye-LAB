import 'package:cata_scan_flutter/core/constants/model_constants.dart';
import 'package:cata_scan_flutter/models/prediction_result.dart';
import 'package:cata_scan_flutter/models/processed_image.dart';
import 'package:cata_scan_flutter/services/eye_detection/eye_detection_model_session_provider.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:onnxruntime/onnxruntime.dart';

final eyeDetectionServiceProvider = FutureProvider.autoDispose<EyeDetectionService>((ref) async {
  final session = await ref.read(eyeDetectionModelSessionProvider.future);
  return EyeDetectionService(session);
});

class EyeDetectionService {
  final OrtSession session;

  EyeDetectionService(this.session);

  Future<EyePredictionResult> predictSingleEye(ProcessedImage image) async {
    final runOptions = OrtRunOptions();
    try {
      final inputTensor = OrtValueTensor.createTensorWithDataList(
        image.normalizedBytes,
        CataractDiagnosisModelConfig.tensorShape,
      );

      final outputs = await session.runAsync(runOptions, {CataractDiagnosisModelConfig.inputName: inputTensor});

      if (outputs == null) {
        throw Exception("No outputs from model");
      }

      final predictedClass = (outputs[0]!.value as List)[0] as int;
      final probability = (outputs[0]!.value as List)[1] as double;

      return EyePredictionResult(
        probability: probability,
        predictedClass: predictedClass,
      );
    } finally {
      runOptions.release();
    }
  }
}
