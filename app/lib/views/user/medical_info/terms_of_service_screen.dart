import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/core/providers/permission_service_provider.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/common/camera/camera_select_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:lottie/lottie.dart';

class TermsOfServiceScreen extends ConsumerWidget {
  const TermsOfServiceScreen({super.key});
  static const String routeName = '/terms_of_service';

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
                'Please agree to\nterms and conditions',
                style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  color: AppColor.textBlack,
                ),
              ),
            ),
            const Spacer(flex: 35),
            Container(
              height: 250,
              width: double.infinity,
              margin: const EdgeInsets.symmetric(horizontal: 20),
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColor.backgroundLightGray,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  RichText(
                      text: const TextSpan(
                          style: TextStyle(
                              color: AppColor.textBlack, fontSize: 19),
                          children: [
                        TextSpan(text: 'We collect your'),
                        TextSpan(
                            text:
                                ' eye scan images, diagnostic results, and basic profile information',
                            style: TextStyle(color: AppColor.textPrimary)),
                        TextSpan(
                            text:
                                ' to enhance our AI services. Your consent would be greatly appreciated.',
                            style: TextStyle(color: AppColor.textBlack)),
                      ])),
                  const Text(
                    '*If you do not agree, the verification process may be challenging.',
                    style: TextStyle(
                        color: AppColor.textGray,
                        fontWeight: AppFontWeight.regular,
                        fontSize: 14),
                  ),
                ],
              ),
            ),
            const Spacer(flex: 105),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: SecondaryButton(
                onTap: () {
                  context.pop();
                },
                text: 'Disagree and leave this page',
                textStyle: const TextStyle(
                    color: AppColor.textPrimary,
                    fontWeight: AppFontWeight.semiBold,
                    fontSize: 16),
              ),
            ),
            const Gap(8),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: PrimaryButton(
                onTap: () async {
                  final permissionService = ref.read(permissionServiceProvider);
                  final isGranted =
                      await permissionService.getCameraPermission();
                  if (isGranted && context.mounted) {
                    context.pushReplacement(CameraSelectScreen.routeName);
                  }
                },
                text: 'I Agree and Proceed',
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
        )));
  }
}
