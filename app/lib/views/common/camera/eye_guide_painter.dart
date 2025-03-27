import 'package:flutter/material.dart';

class EyeGuidePainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.white
      ..strokeWidth = 4
      ..style = PaintingStyle.stroke;

    final dashedPaint = Paint()
      ..color = Colors.white.withOpacity(0.30)
      ..strokeWidth = 1
      ..style = PaintingStyle.stroke;

    final centerX = size.width / 2;
    final centerY = size.height / 2;
    const dashWidth = 10.0;
    const dashSpace = 10.0;

    canvas.drawLine(
      Offset(centerX - 22.5, centerY),
      Offset(centerX + 22.5, centerY),
      paint,
    );

    canvas.drawLine(
      Offset(centerX, centerY - 22.5),
      Offset(centerX, centerY + 22.5),
      paint,
    );

    double startX = 0;
    while (startX < centerX - 22.5) {
      canvas.drawLine(
        Offset(startX, centerY),
        Offset(startX + dashWidth, centerY),
        dashedPaint,
      );
      startX += dashWidth + dashSpace;
    }

    startX = centerX + 22.5;
    while (startX < size.width) {
      canvas.drawLine(
        Offset(startX, centerY),
        Offset(startX + dashWidth, centerY),
        dashedPaint,
      );
      startX += dashWidth + dashSpace;
    }

    double startY = 0;
    while (startY < centerY - 22.5) {
      canvas.drawLine(
        Offset(centerX, startY),
        Offset(centerX, startY + dashWidth),
        dashedPaint,
      );
      startY += dashWidth + dashSpace;
    }

    // 세로 점선 (아래쪽)
    startY = centerY + 22.5;
    while (startY < size.height) {
      canvas.drawLine(
        Offset(centerX, startY),
        Offset(centerX, startY + dashWidth),
        dashedPaint,
      );
      startY += dashWidth + dashSpace;
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
