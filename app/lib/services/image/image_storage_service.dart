import 'dart:io';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as path;

class ImageStorageService {
  static Future<String> saveImagePermanently(String tempPath) async {
    final directory = await getApplicationDocumentsDirectory();
    final fileName = 'eye_photo_${DateTime.now().millisecondsSinceEpoch}.jpeg';
    final permanentPath = path.join(directory.path, fileName);

    final File tempFile = File(tempPath);
    final File savedFile = await tempFile.copy(permanentPath);

    await tempFile.delete();

    return savedFile.path;
  }
}
