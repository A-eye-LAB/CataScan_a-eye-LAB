import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:permission_handler/permission_handler.dart';

final permissionServiceProvider = Provider<PermissionService>((ref) => PermissionService());

class PermissionService {
  Future<bool> getCameraPermission() async {
    var cameraStatus = await Permission.camera.request();
    return cameraStatus.isGranted;
  }

  Future<PermissionStatus> getCameraPermissionStatus() async {
    return await Permission.camera.status;
  }

  Future<bool> openAppSettings() async {
    return await openAppSettings();
  }
}
