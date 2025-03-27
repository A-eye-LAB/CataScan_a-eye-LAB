import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:flutter/material.dart';

class PrimaryButton extends StatelessWidget {
  const PrimaryButton({super.key, required this.onTap, required this.text, required this.isDisabled, this.textStyle});
  final String text;
  final bool isDisabled;
  final Function() onTap;
  final TextStyle? textStyle;

  @override
  Widget build(BuildContext context) {
    final backgroundColor = isDisabled ? AppColor.button1BackgroundDisabled : AppColor.button1Background;
    final textColor = isDisabled ? AppColor.button1TextDisabled : AppColor.button1Text;
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: double.infinity,
        height: 52,
        decoration: BoxDecoration(
          color: backgroundColor,
          borderRadius: BorderRadius.circular(8),
        ),
        child: Center(
          child: Text(
            text,
            style: textStyle ??
                TextStyle(
                  color: textColor,
                  fontWeight: AppFontWeight.semiBold,
                  fontSize: 17,
                ),
          ),
        ),
      ),
    );
  }
}

class SecondaryButton extends StatelessWidget {
  const SecondaryButton({super.key, required this.onTap, required this.text, this.textStyle});
  final String text;
  final Function() onTap;
  final TextStyle? textStyle;

  @override
  Widget build(BuildContext context) {
    const backgroundColor = AppColor.button2Background;
    const textColor = AppColor.button2Text;
    const borderColor = AppColor.button2Border;
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: double.infinity,
        height: 50,
        decoration: BoxDecoration(
          color: backgroundColor,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: borderColor, width: 1),
        ),
        child: Center(
          child: Text(
            text,
            style: textStyle ??
                const TextStyle(
                  color: textColor,
                  fontWeight: AppFontWeight.semiBold,
                  fontSize: 17,
                ),
          ),
        ),
      ),
    );
  }
}
