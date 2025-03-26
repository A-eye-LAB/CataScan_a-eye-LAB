import 'dart:ui';

class CataractDiagnosisModelConfig {
  static const modelUrl = "assets/model/cataract_detection_0_0_2.onnx";
  static const inputSize = Size(224, 224);
  static const tensorShape = [1, 3, 224, 224];
  static const inputName = "input";
}

class EyeDetectionModelConfig {
  static const modelUrl = "assets/model/eye_detection_0_0_2.onnx";
  static const inputSize = Size(224, 224);
  static const tensorShape = [1, 3, 224, 224];
  static const inputName = "input";
}
