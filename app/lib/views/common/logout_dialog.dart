import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/services/cataract_diagnosis/analysis_data_storage_provider.dart';
import 'package:cata_scan_flutter/services/storage/user_info_storage.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/welcome_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';

class LogoutDialog extends ConsumerWidget {
  const LogoutDialog({super.key});
  static const String routeName = '/logout_dialog';

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Stack(
        children: [
          Positioned.fill(
            child: GestureDetector(
              onTap: () {
                context.pop();
              },
              child: Container(
                color: Colors.black.withOpacity(0.38),
              ),
            ),
          ),
          Align(
            alignment: Alignment.center,
            child: Container(
              width: 340,
              padding: const EdgeInsets.only(left: 20, right: 20, top: 55, bottom: 20),
              decoration: BoxDecoration(
                color: AppColor.backgroundWhite,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  const Text(
                    'Are you Leaving?',
                    style: TextStyle(
                      fontSize: 32,
                      fontWeight: AppFontWeight.semiBold,
                      color: AppColor.textBlack,
                    ),
                  ),
                  const Gap(34),
                  const Text(
                    'Are you sure you want to logout?\nYour data will not be saved.',
                    style: TextStyle(
                      fontSize: 17,
                      fontWeight: AppFontWeight.regular,
                      color: AppColor.textBlack,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const Gap(40),
                  SecondaryButton(
                    onTap: () {
                      context.pop();
                    },
                    text: 'Cancel',
                  ),
                  const Gap(8),
                  PrimaryButton(
                    onTap: () async {
                      await ref.read(analysisDataStorageProvider.notifier).clearEyeScanData();
                      await ref.read(userInfoStorageProvider).clearUserInfo();
                      if (context.mounted) {
                        context.go(WelcomeScreen.routeName);
                      }
                    },
                    text: 'Logout',
                    isDisabled: false,
                  ),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }
}
