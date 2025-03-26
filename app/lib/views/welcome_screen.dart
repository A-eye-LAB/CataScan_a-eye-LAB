import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/services/storage/user_info_storage.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/center/main/center_main_screen.dart';
import 'package:cata_scan_flutter/views/user/main/main_screen.dart';
import 'package:cata_scan_flutter/views/common/survey/survay_landing_screen.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';

import 'package:cata_scan_flutter/services/login/login_center_provider.dart';
import 'package:cata_scan_flutter/views/center/login/center_internet_error_screen.dart';
import 'package:cata_scan_flutter/views/center/login/center_login_screen.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:cata_scan_flutter/core/providers/is_center_mode_provider.dart';

class WelcomeScreen extends ConsumerWidget {
  const WelcomeScreen({super.key});
  static const String routeName = '/';

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    precacheImage(const AssetImage(RasterAsset.welcomeLoading), context);
    final topPadding = MediaQuery.paddingOf(context).top;
    final bottomPadding = MediaQuery.paddingOf(context).bottom;

    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light,
      ),
      child: Scaffold(
        backgroundColor: AppColor.backgroundGradientBlack,
        body: Column(
          children: [
            Expanded(
              flex: 281,
              child: Stack(
                fit: StackFit.expand,
                children: [
                  const Positioned.fill(
                      child:
                          ColoredBox(color: AppColor.backgroundGradientBlack)),
                  Column(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      SizedBox(height: topPadding),
                      const Spacer(),
                      Center(child: _buildLottieContainer()),
                      const Gap(5),
                    ],
                  ),
                ],
              ),
            ),
            Expanded(
              flex: 30 + 82 + 67 + 52 + 186 + 26 + 20,
              child: Stack(
                fit: StackFit.expand,
                children: [
                  Positioned.fill(child: _buildBackground()),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    child: Column(
                      children: [
                        const Gap(30),
                        const Text(
                          'Scan to\nSpot Cataracts',
                          textAlign: TextAlign.center,
                          style: TextStyle(
                              color: Colors.white,
                              fontSize: 32,
                              fontWeight: FontWeight.bold),
                        ),
                        const Spacer(flex: 67),
                        PrimaryButton(
                          text: 'Start Scan',
                          isDisabled: false,
                          onTap: () async {
                            final (success, _) = await ref
                                .read(userInfoStorageProvider)
                                .getUserInfo();
                            ref.read(isCenterModeProvider.notifier).state =
                                false;
                            if (success) {
                              context.go(MainScreen.routeName);
                            } else {
                              context.go(SurveyLandingScreen.routeName);
                            }
                          },
                        ),
                        const Gap(16),
                        RichText(
                          text: TextSpan(
                            children: [
                              const TextSpan(
                                  text:
                                      "Tapping 'Start Scan' means you accept our\n",
                                  style: TextStyle(
                                      color: AppColor.textWhite,
                                      fontSize: 14,
                                      fontWeight: AppFontWeight.regular,
                                      height: 1.4)),
                              TextSpan(
                                  text: 'Terms of Service',
                                  style: const TextStyle(
                                      color: AppColor.textWhite,
                                      fontSize: 14,
                                      fontWeight: AppFontWeight.semiBold,
                                      decoration: TextDecoration.underline),
                                  recognizer: TapGestureRecognizer()
                                    ..onTap = () {
                                      launchUrl(Uri.parse(
                                          'https://catascan.org/terms-of-service'));
                                    }),
                              const TextSpan(
                                  text: ' & ',
                                  style: TextStyle(
                                      color: AppColor.textWhite,
                                      fontSize: 14,
                                      fontWeight: AppFontWeight.regular,
                                      height: 1.4)),
                              TextSpan(
                                  text: 'Privacy Policy',
                                  style: const TextStyle(
                                      color: AppColor.textWhite,
                                      fontSize: 14,
                                      fontWeight: AppFontWeight.semiBold,
                                      decoration: TextDecoration.underline),
                                  recognizer: TapGestureRecognizer()
                                    ..onTap = () {
                                      launchUrl(Uri.parse(
                                          'https://catascan.org/privacy-policy'));
                                    }),
                            ],
                          ),
                          textAlign: TextAlign.center,
                        ),
                        const Spacer(flex: 140),
                        const _CenterButton(),
                        const Gap(20),
                        SizedBox(height: bottomPadding),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLottieContainer() {
    return Image.asset(
      RasterAsset.welcomeLoading,
      width: 360,
      height: 233,
      fit: BoxFit.fitWidth,
    );
  }

  Widget _buildBackground() {
    return Container(
      decoration: const BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [
            AppColor.backgroundGradientBlack,
            AppColor.backgroundGradientBlue,
          ],
        ),
      ),
    );
  }
}

class _CenterButton extends StatelessWidget {
  const _CenterButton();

  @override
  Widget build(BuildContext context) {
    return Consumer(
      builder: (context, ref, child) {
        final loginState = ref.watch(loginCenterProvider);
        final isLoading = loginState.isLoading;

        return TextButton(
          onPressed: () async {
            ref.read(isCenterModeProvider.notifier).state = true;
            final checkStatus = await Connectivity().checkConnectivity();
            if (checkStatus.contains(ConnectivityResult.none)) {
              if (context.mounted) {
                context.go(CenterInternetErrorScreen.routeName);
                return;
              }
            }

            final hasError =
                await ref.read(loginCenterProvider.notifier).autoLogin();
            if (context.mounted) {
              if (hasError) {
                context.go(CenterLoginCenterScreen.routeName);
              } else {
                context.go(CenterMainScreen.routeName);
              }
            }
          },
          child: isLoading
              ? const SizedBox(
                  width: 16,
                  height: 16,
                  child: CircularProgressIndicator(
                    color: Colors.white,
                    strokeWidth: 2,
                  ),
                )
              : const Text(
                  'For Vision Center  >',
                  style: TextStyle(
                    color: AppColor.textWhite,
                    fontSize: 17,
                    fontWeight: AppFontWeight.semiBold,
                  ),
                ),
        );
      },
    );
  }
}
