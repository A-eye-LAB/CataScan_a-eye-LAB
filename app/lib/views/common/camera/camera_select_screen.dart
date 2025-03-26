import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/views/common/camera/shooting_screen.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:go_router/go_router.dart';
import 'package:lottie/lottie.dart';

class CameraSelectScreen extends ConsumerWidget {
  const CameraSelectScreen({super.key});
  static const String routeName = '/camera_select';

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Lottie.asset(
              LottieAsset.loading,
              width: 140,
              fit: BoxFit.fitWidth,
            ),
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 20),
              child: Text(
                'Are you Scanning\nby yourself?',
                style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  color: AppColor.textBlack,
                ),
              ),
            ),
            const Spacer(flex: 54),
            Center(
              child: Container(
                decoration: const BoxDecoration(
                  color: AppColor.backgroundLightGray,
                  shape: BoxShape.circle,
                ),
                width: 200,
                height: 200,
                child: Center(
                  child: SvgPicture.asset(
                    VectorAsset.eyeIconBlue,
                    fit: BoxFit.fitWidth,
                    height: 60,
                    width: 60,
                  ),
                ),
              ),
            ),
            const Spacer(flex: 134),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: SecondaryButton(
                onTap: () {
                  context.pushReplacement(
                    ShootingScreen.routeName,
                    extra: {
                      'cameraLensDirection': CameraLensDirection.front,
                    },
                  );
                },
                text: 'Yes, I\'m Scanning by Myself',
                textStyle: const TextStyle(
                  color: AppColor.textPrimary,
                  fontWeight: AppFontWeight.semiBold,
                  fontSize: 16,
                ),
              ),
            ),
            const Gap(8),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: PrimaryButton(
                onTap: () async {
                  context.pushReplacement(
                    ShootingScreen.routeName,
                    extra: {
                      'cameraLensDirection': CameraLensDirection.back,
                    },
                  );
                },
                text: 'I Have Someone to Scan Me',
                isDisabled: false,
                textStyle: const TextStyle(
                  color: AppColor.textWhite,
                  fontWeight: AppFontWeight.semiBold,
                  fontSize: 16,
                ),
              ),
            ),
            const Gap(20),
          ],
        ),
      ),
    );
  }
}
