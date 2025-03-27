import 'dart:io';
import 'package:image/image.dart' as img;

class ImageCropperService {
  Future<String> cropImage({
    required String imagePath,
    required double pictureHeight,
    required double pictureWidth,
    required double topPadding,
    required double bottomPadding,
  }) async {
    try {
      final File imageFile = File(imagePath);
      final bytes = await imageFile.readAsBytes();
      final originalImage = img.decodeImage(bytes);

      if (originalImage == null) {
        throw Exception('Failed to decode image');
      }

      final scale = originalImage.width / pictureWidth;

      final totalHeight = (pictureHeight * scale).toInt();
      final cropSize = (originalImage.width * 0.8).toInt();

      final startX = (originalImage.width - cropSize) ~/ 2;
      final startY = (topPadding * scale + (totalHeight - cropSize) / 2).toInt();

      final croppedImage = img.copyCrop(
        originalImage,
        x: startX,
        y: startY,
        width: cropSize,
        height: cropSize,
      );

      final croppedBytes = img.encodeJpg(croppedImage);
      final croppedPath = imagePath.replaceAll('.jpg', '_cropped.jpg');
      await File(croppedPath).writeAsBytes(croppedBytes);

      return croppedPath;
    } catch (e) {
      throw Exception('Failed to crop image: $e');
    }
  }
}
