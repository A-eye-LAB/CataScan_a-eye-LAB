import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/views/common/analysis/analysis_loading_screen.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'dart:io';
import 'package:intl/intl.dart';
import 'package:go_router/go_router.dart';

class ShootingResultScreen extends ConsumerStatefulWidget {
  const ShootingResultScreen({
    super.key,
    required this.leftEyePath,
    required this.rightEyePath,
  });
  static const String routeName = '/shooting_result';
  final String leftEyePath;
  final String rightEyePath;

  @override
  ConsumerState<ShootingResultScreen> createState() =>
      _ShootingResultScreenState();
}

class _ShootingResultScreenState extends ConsumerState<ShootingResultScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColor.backgroundLightGray,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            color: AppColor.backgroundWhite,
            height: 40 + MediaQuery.paddingOf(context).top,
            padding: EdgeInsets.only(top: MediaQuery.paddingOf(context).top),
            child: const Center(
              child: Text(
                'Check Photo',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: AppFontWeight.medium,
                  color: AppColor.textBlack,
                ),
              ),
            ),
          ),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Column(
                children: [
                  const Gap(8),
                  Expanded(
                      child: _buildEyeSection('Left Eye', widget.leftEyePath)),
                  const Gap(12),
                  Expanded(
                      child:
                          _buildEyeSection('Right Eye', widget.rightEyePath)),
                  const Gap(18),
                  PrimaryButton(
                    onTap: () {
                      context.pushReplacement(
                        AnalysisLoadingScreen.routeName,
                        extra: {
                          'leftEyePath': widget.leftEyePath,
                          'rightEyePath': widget.rightEyePath,
                        },
                      );
                    },
                    text: 'Analysis',
                    isDisabled: false,
                  ),
                ],
              ),
            ),
          ),
          Gap(28 + MediaQuery.paddingOf(context).bottom),
        ],
      ),
    );
  }

  Widget _buildEyeSection(String title, String? imagePath) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppColor.backgroundWhite,
        border: Border.all(color: const Color(0xFFECECEC), width: 1),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                title,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: AppFontWeight.semiBold,
                  color: AppColor.textBlack,
                ),
              ),
              //TODO: Retry 버튼 활성화 시킬때 다시 로직만들기
              Text(
                DateFormat('dd MMM yyyy, hh:mm a').format(DateTime.now()),
                style: const TextStyle(
                  fontSize: 10,
                  fontWeight: AppFontWeight.regular,
                  color: Color(0xFF646464),
                ),
              ),
            ],
          ),
          const Gap(12),
          if (imagePath != null)
            ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: Image.file(
                File(imagePath),
                height: 216,
                width: 216,
                fit: BoxFit.cover,
              ),
            )
          else
            Container(
              height: 216,
              width: 216,
              decoration: BoxDecoration(
                color: AppColor.backgroundLightGray,
                borderRadius: BorderRadius.circular(8),
              ),
            ),
          //const Gap(16),
          //_SecondaryButton(onTap: () {}),
        ],
      ),
    );
  }
}

// class _SecondaryButton extends StatelessWidget {
//   const _SecondaryButton({required this.onTap});
//   final Function() onTap;

//   @override
//   Widget build(BuildContext context) {
//     const backgroundColor = AppColor.button2Background;
//     const textColor = AppColor.button2Text;
//     const borderColor = AppColor.button2Text;
//     return GestureDetector(
//       onTap: onTap,
//       child: Container(
//         width: double.infinity,
//         height: 40,
//         decoration: BoxDecoration(
//           color: backgroundColor,
//           borderRadius: BorderRadius.circular(8),
//           border: Border.all(color: borderColor, width: 1),
//         ),
//         child: const Center(
//           child: Text(
//             "Retry",
//             style: TextStyle(
//               color: textColor,
//               fontWeight: AppFontWeight.semiBold,
//               fontSize: 16,
//             ),
//           ),
//         ),
//       ),
//     );
//   }
// }
