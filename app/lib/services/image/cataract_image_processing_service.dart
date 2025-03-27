import 'dart:typed_data';
import 'dart:ui';

import 'package:cata_scan_flutter/core/constants/model_constants.dart';
import 'package:cata_scan_flutter/models/processed_image.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:image/image.dart' as img;

final imageProcessingServiceProvider = Provider.autoDispose<ImageProcessingService>((ref) {
  return ImageProcessingService();
});

class ImageProcessingService {
  ProcessedImage processImageForPrediction(Uint8List imageBytes) {
    final originalImage = img.decodeImage(imageBytes);
    if (originalImage == null) {
      throw Exception('Failed to decode image');
    }
    final srcSize = Size(originalImage.width.toDouble(), originalImage.height.toDouble());
    final dstSize = Size(CataractDiagnosisModelConfig.inputSize.width.toDouble(), CataractDiagnosisModelConfig.inputSize.height.toDouble());
    final resizedBytes = _resizeToModelInputSize(imageBytes, srcSize, dstSize);
    final normalizedBytes = _normalizeImage(resizedBytes);
    return ProcessedImage(normalizedBytes: normalizedBytes);
  }

  Uint8List _resizeToModelInputSize(Uint8List imageBytes, Size srcSize, Size dstSize) {
    img.Image? srcImage = img.decodeImage(imageBytes);
    if (srcImage == null) {
      throw Exception('Invalid image data');
    }
    img.Image resizedImage = img.copyResize(
      srcImage,
      width: dstSize.width.toInt(),
      height: dstSize.height.toInt(),
      interpolation: img.Interpolation.cubic,
    );
    return Uint8List.fromList(resizedImage.getBytes());
  }

  Float32List _normalizeImage(Uint8List imageBytes) {
    final length = CataractDiagnosisModelConfig.inputSize.height * CataractDiagnosisModelConfig.inputSize.width * 3; // channels
    if (imageBytes.length != length) {
      throw Exception('Invalid image size');
    }
    final height = CataractDiagnosisModelConfig.inputSize.height.toInt();
    final width = CataractDiagnosisModelConfig.inputSize.width.toInt();
    const channels = 3;
    const batchSize = 1;

    var normalized = Float32List(imageBytes.length);
    // First normalization: divide by 255
    for (int i = 0; i < imageBytes.length; i++) {
      normalized[i] = (imageBytes[i] / 255.0);
    }

    // Second normalization: ImageNet mean and std
    final means = [0.485, 0.456, 0.406];
    final stds = [0.229, 0.224, 0.225];
    for (int i = 0; i < normalized.length; i++) {
      int channelIndex = i % 3; // RGB channels (0, 1, 2)
      normalized[i] = (normalized[i] - means[channelIndex]) / stds[channelIndex];
    }

    var result = Float32List(batchSize * channels * height * width);
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        for (int c = 0; c < channels; c++) {
          final srcIdx = (h * width + w) * channels + c;
          final dstIdx = c * height * width + h * width + w;
          result[dstIdx] = normalized[srcIdx];
        }
      }
    }

    return result;
  }
}
