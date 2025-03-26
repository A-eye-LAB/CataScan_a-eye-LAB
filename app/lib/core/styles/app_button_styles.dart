import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:flutter/material.dart';

class AppButtonStyles {
  final double width, height;
  AppButtonStyles({required this.width, required this.height});

  static ButtonStyle primaryStyle(double width, double height) {
    return ElevatedButton.styleFrom(
      minimumSize: Size(width, height),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8),
      ),
      foregroundColor: AppColor.button1Text,
      backgroundColor: AppColor.button1Background,
    );
  }

  static ButtonStyle secondStyle(double width, double height) {
    return OutlinedButton.styleFrom(
      minimumSize: Size(width, height),

      side: const BorderSide(
        color: Colors.grey, // 테두리 색상
        width: 1, // 테두리 두께
      ),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8), // 모서리 둥글기
      ),
      foregroundColor: const Color.fromARGB(255, 0, 122, 255), // 텍스트 색상
      backgroundColor: Colors.white,
      //padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
    );
  }

  static ButtonStyle toggleButtonStyle(double width, double height) {
    return ElevatedButton.styleFrom(
      minimumSize: Size(width, height),
      backgroundColor: const Color.fromARGB(255, 0, 122, 255), // 활성화됐을 때 색상
      foregroundColor: Colors.white, // 텍스트 색상
      disabledBackgroundColor: Colors.grey[200], // 비활성화됐을 때 배경색
      disabledForegroundColor: Colors.grey[400], // 비활성화됐을 때 텍스트 색상
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8), // 모서리 둥글기
      ),
    );
  }

  static TextStyle titleStyle() {
    return const TextStyle(
      fontSize: 32,
      fontWeight: FontWeight.w500,
    );
  }
}
