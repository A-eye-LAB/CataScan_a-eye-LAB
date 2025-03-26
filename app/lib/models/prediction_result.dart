class CataractPredictionResult {
  final List<double> probabilities;
  final int predictedClass;

  CataractPredictionResult({
    required this.probabilities,
    required this.predictedClass,
  });

  bool get isCataract => predictedClass == 1;

  @override
  String toString() {
    return "PredictionResult(probabilities: $probabilities, predictedClass: $predictedClass)";
  }
}

class EyesCataractPredictionResult {
  final CataractPredictionResult leftEye;
  final CataractPredictionResult rightEye;

  const EyesCataractPredictionResult({
    required this.leftEye,
    required this.rightEye,
  });

  @override
  String toString() {
    return "EyePredictionResult(leftEye: $leftEye, rightEye: $rightEye)";
  }
}

class EyePredictionResult {
  final double probability;
  final int predictedClass;

  EyePredictionResult({
    required this.probability,
    required this.predictedClass,
  });

  bool get isEye => predictedClass == 1;
}

class EyesPredictionResult {
  final EyePredictionResult leftEye;
  final EyePredictionResult rightEye;

  EyesPredictionResult({
    required this.leftEye,
    required this.rightEye,
  });
}
