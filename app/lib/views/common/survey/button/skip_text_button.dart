import 'package:flutter/material.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';

class SkipTextButton extends StatelessWidget {
  const SkipTextButton({super.key, required this.onPressed});
  final Function() onPressed;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onPressed,
      child: const MouseRegion(
          cursor: SystemMouseCursors.click,
          child: Text(
            'Skip for now',
            style: TextStyle(color: AppColor.textPrimary, fontWeight: AppFontWeight.bold),
          )),
    );
  }
}
