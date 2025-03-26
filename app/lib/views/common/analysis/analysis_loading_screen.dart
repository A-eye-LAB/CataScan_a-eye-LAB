import 'package:cata_scan_flutter/core/providers/is_center_mode_provider.dart';
import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/models/eye_scan_data.dart';
import 'package:cata_scan_flutter/services/cataract_diagnosis/cataract_diagnosis_provider.dart';
import 'package:cata_scan_flutter/views/user/analysis/analysis_result_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:cata_scan_flutter/views/center/analysis/center_analysis_result_screen.dart';

class AnalysisLoadingScreen extends ConsumerWidget {
  const AnalysisLoadingScreen({
    super.key,
    required this.leftEyePath,
    required this.rightEyePath,
  });
  final String leftEyePath;
  final String rightEyePath;

  static const routeName = '/analysis_loading';

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final paths = (leftEyePath: leftEyePath, rightEyePath: rightEyePath);
    final predictionResult = ref.watch(cataractDiagnosisProvider(paths));
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Image.asset(
                RasterAsset.analysisLoading,
                width: 150,
                fit: BoxFit.fitWidth,
              ),
              const Gap(18),
              predictionResult.when(
                data: (data) {
                  final leftEyeStatus = data.leftEye.isCataract
                      ? EyeStatus.abnormal
                      : EyeStatus.normal;
                  final rightEyeStatus = data.rightEye.isCataract
                      ? EyeStatus.abnormal
                      : EyeStatus.normal;

                  if (ref.read(isCenterModeProvider.notifier).state == true) {
                    context.pushReplacement(
                      CenterAnalysisResultScreen.routeName,
                      extra: {
                        'leftEyePath': leftEyePath,
                        'leftEyeStatus': leftEyeStatus,
                        'rightEyePath': rightEyePath,
                        'rightEyeStatus': rightEyeStatus,
                      },
                    );
                  } else {
                    context.pushReplacement(
                      AnalysisResultScreen.routeName,
                      extra: {
                        'leftEyePath': leftEyePath,
                        'leftEyeStatus': leftEyeStatus,
                        'rightEyePath': rightEyePath,
                        'rightEyeStatus': rightEyeStatus,
                      },
                    );
                  }

                  return const Text(
                    'Analysis completed',
                    style: TextStyle(
                      fontSize: 34,
                      fontWeight: AppFontWeight.semiBold,
                      color: AppColor.textBlack,
                    ),
                  );
                },
                error: (error, stackTrace) => Text(error.toString()),
                loading: () => const LoadingText(
                  text: 'Analyzing',
                  style: TextStyle(
                    fontSize: 34,
                    fontWeight: AppFontWeight.semiBold,
                    color: AppColor.textBlack,
                  ),
                ),
              ),
              const Gap(32),
              const Text(
                'Cataracts can be treated\neffectively if detected early.',
                style: TextStyle(
                  fontSize: 19,
                  fontWeight: AppFontWeight.regular,
                  color: AppColor.textBlack,
                ),
                textAlign: TextAlign.center,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class LoadingText extends StatefulWidget {
  final String text;
  final TextStyle style;

  const LoadingText({
    super.key,
    required this.text,
    required this.style,
  });

  @override
  State<LoadingText> createState() => _LoadingTextState();
}

class _LoadingTextState extends State<LoadingText>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  int _dotCount = 0;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500),
    )..addStatusListener((status) {
        if (status == AnimationStatus.completed) {
          setState(() {
            _dotCount = (_dotCount + 1) % 4;
          });
          _controller.reset();
          _controller.forward();
        }
      });
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Text(
      '${widget.text}${List.filled(_dotCount, '.').join()}',
      style: widget.style,
      textAlign: TextAlign.center,
    );
  }
}
