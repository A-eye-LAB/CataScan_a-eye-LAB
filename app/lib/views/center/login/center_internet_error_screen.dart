import 'package:cata_scan_flutter/services/login/login_center_provider.dart';
import 'package:cata_scan_flutter/views/center/login/center_login_screen.dart';
import 'package:cata_scan_flutter/views/center/main/center_main_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import 'package:gap/gap.dart';
import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/core/styles/app_button_styles.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// ignore: must_be_immutable
class CenterInternetErrorScreen extends ConsumerStatefulWidget {
  CenterInternetErrorScreen({super.key});
  static const String routeName = '/internet_error';
  bool isLoading = false;
  @override
  ConsumerState<CenterInternetErrorScreen> createState() =>
      _CenterInternetErrorScreenState();
}

class _CenterInternetErrorScreenState extends ConsumerState<CenterInternetErrorScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
          child: Center(
        child: Column(
          children: [
            const Gap(130),
            SvgPicture.asset(
              VectorAsset.wifioff,
              width: 150,
              height: 150,
            ),
            const Gap(20),
            const Text(
              'Ooops!',
              style: TextStyle(
                  fontFamily: 'SF Pro',
                  fontSize: 34,
                  fontWeight: AppFontWeight.semiBold),
            ),
            const Gap(20),
            const Text(
              'No Internet Connection found.\nPlease Check your Connection',
              style: TextStyle(
                  fontFamily: 'SF Pro',
                  fontSize: 19,
                  fontWeight: AppFontWeight.regular),
            ),
            const Spacer(),
            Padding(
              padding: const EdgeInsets.all(24),
              child: ElevatedButton(
                style: AppButtonStyles.primaryStyle(double.infinity, 60),
                onPressed: widget.isLoading == true
                    ? null
                    : () async {
                        widget.isLoading = true;
                        setState(() {});
                        final checkStatus =
                            await Connectivity().checkConnectivity();
                        if (checkStatus.contains(ConnectivityResult.none)) {
                        } else {
                          if (context.mounted) {
                            final hasError = await ref
                                .read(loginCenterProvider.notifier)
                                .autoLogin();
                            if (context.mounted) {
                              if (hasError) {
                                context.go(CenterLoginCenterScreen.routeName);
                              } else {
                                context.go(CenterMainScreen.routeName);
                              }
                            }
                          }
                        }
                        widget.isLoading = false;
                        setState(() {});
                      },
                child: widget.isLoading == true
                    ? const Center(
                        child: CircularProgressIndicator(),
                      )
                    : const Text('try again'),
              ),
            ),
          ],
        ),
      )),
    );
  }
}
