import 'dart:math';

import 'package:cata_scan_flutter/core/constants/model_constants.dart';
import 'package:cata_scan_flutter/models/prediction_result.dart';
import 'package:cata_scan_flutter/models/processed_image.dart';
import 'package:cata_scan_flutter/services/cataract_diagnosis/cataract_diagnosis_model_session_provider.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:onnxruntime/onnxruntime.dart';

final cataractDiagnosisServiceProvider = FutureProvider.autoDispose<CataractDiagnosisService>((ref) async {
  final session = await ref.read(cataractDiagnosisModelSessionProvider.future);
  return CataractDiagnosisService(session);
});

class CataractDiagnosisService {
  final OrtSession session;

  CataractDiagnosisService(this.session);

  Future<CataractPredictionResult> predictSingleEye(ProcessedImage image) async {
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
      final result = (outputs[0]!.value as List)[0] as List<double>;
      final probabilities = _softmax(result);
      return CataractPredictionResult(
        probabilities: probabilities,
        predictedClass: _getPredictedClass(probabilities),
      );
    } finally {
      runOptions.release();
    }
  }

  int _getPredictedClass(List<double> probabilities) {
    return probabilities.indexOf(probabilities.reduce((a, b) => a > b ? a : b));
  }

  List<double> _softmax(List<double> values) {
    final maxLogit = values.reduce((a, b) => a > b ? a : b);
    final expValues = values.map((value) => exp(value - maxLogit)).toList();
    final sumExpValues = expValues.reduce((a, b) => a + b);
    return expValues.map((value) => value / sumExpValues).toList();
  }
}
