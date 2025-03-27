import 'package:flutter/material.dart';

class AppColor {
  static const Color backgroundWhite = Color(0xFFFFFFFF);
  static const Color backgroundBlack = Color(0xFF000000);
  static const Color backgroundGradientBlack = Color(0xFF000E31);
  static const Color backgroundGradientBlue = Color(0xFF02309A);
  static const Color backgroundLightGray = Color(0xFFF7F7FA);

  static const Color button1Background = Color(0xFF007AFF);
  static const Color button1Text = Color(0xFFF7F7FA);
  static const Color button1BackgroundDisabled = Color(0xFFF7F7FA);
  static final Color button1TextDisabled = const Color(0xFF000000).withOpacity(0.38);

  static const Color button2Background = Color(0xFFFFFFFF);
  static const Color button2Text = Color(0xFF027AFA);
  static const Color button2Border = Color(0xFFDADADA);

  static const Color _button3GradientPrimary = Color(0xFF007AFF);
  static const Color _button3GradientSecondary = Color(0xFF8DC4FF);
  static const LinearGradient button3Gradient = LinearGradient(
    colors: [_button3GradientPrimary, _button3GradientSecondary],
    begin: Alignment.centerLeft,
    end: Alignment.centerRight,
  );
  static const Color button3Text = Color(0xFFFFFFFF);
  static final Color button3BackgroundDisabled = const Color(0xFF000000).withOpacity(0.04);
  static final Color button3TextDisabled = const Color(0xFF212121).withOpacity(0.20);

  static const Color button3BackgroundDefault = Color(0xFFF7F7FA);
  static final Color button3TextDefault = const Color(0xFF212121).withOpacity(0.80);

  static final Gradient button4GradientDefault = LinearGradient(
    colors: [const Color(0xFFFFFFFF), const Color(0xFFFFFFFF).withOpacity(0.0)],
    begin: Alignment.bottomCenter,
    end: Alignment.topCenter,
  );

  static final Color button5Background = const Color(0xFF000000).withOpacity(0.2);
  static const Color button5Text = Color(0xFFFFFFFF);
  static final Color button5Active = const Color(0xFF007AFF).withOpacity(0.9);

  static const Color bannerBackgroundNormal = Color(0xFFEDF2FF);
  static const Color bannerTextNormal = Color(0xFF007AFF);
  static const Color bannerBackgroundAbnormal = Color(0xFFFFF1F4);
  static const Color bannerTextAbnormal = Color(0xFFDB0016);

  static const Color textBlack = Color(0xFF212121);
  static const Color textWhite = Color(0xFFF7F7FA);
  static const Color textPrimary = Color(0xFF007AFF);
  static const Color textHint = Color(0xFFC7C7C7);
  static const Color textGray = Color(0xFF999999);

  static const Color indicatorOn = Color(0xFF007AFF);
  static const Color indicatorOff = Color(0xFFF4F4F7);

  static final Color textCaption = const Color(0xFF007AFF).withOpacity(0.5);

  static const Color resultPatientInfo = Color(0xFFF7F7FA);

  static final LinearGradient resultFalseGradient = LinearGradient(
    colors: [const Color(0xFF007AFF), const Color(0xFF007AFF).withOpacity(0.5)],
    begin: Alignment.centerLeft,
    end: Alignment.centerRight,
  );

  static final LinearGradient resultTrueGradient = LinearGradient(
    colors: [const Color(0xFFFF3449), const Color(0xFFFF3449).withOpacity(0.5)],
    begin: Alignment.centerLeft,
    end: Alignment.centerRight,
  );
}
