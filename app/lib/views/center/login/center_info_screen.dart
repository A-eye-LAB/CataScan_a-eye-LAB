import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/services/login/login_center_provider.dart';
import 'package:cata_scan_flutter/views/welcome_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';

import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/views/common/button.dart';

class CenterInfoScreen extends ConsumerWidget {
  const CenterInfoScreen({super.key});
  static const String routeName = '/center_info';

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final loginState = ref.watch(loginCenterProvider);
    final bottomInset = MediaQuery.viewInsetsOf(context).bottom;
    return Scaffold(
      backgroundColor: AppColor.backgroundWhite,
      resizeToAvoidBottomInset: false,
      body: GestureDetector(
        onTap: () => FocusScope.of(context).unfocus(),
        child: SafeArea(
          child: LayoutBuilder(builder: (context, constraints) {
            final maxHeight = constraints.maxHeight + bottomInset;
            return loginState.when(
              loading: () => const CircularProgressIndicator(),
              error: (error, stack) => Center(child: Text(error.toString())),
              data: (data) {
                if (data.id?.isEmpty ?? true) {
                  return const Center(child: Text('No Login id'));
                }
                return SingleChildScrollView(
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    height: maxHeight,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        SizedBox(
                          height: 50,
                          child: Row(
                            children: [
                              IconButton(
                                onPressed: () {
                                  context.pop();
                                },
                                icon: const Icon(Icons.arrow_back_ios_new_rounded),
                              ),
                              const Spacer(),
                            ],
                          ),
                        ),
                        const Gap(20),
                        Center(
                          child: Container(
                            width: 100,
                            height: 100,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              border: Border.all(
                                color: const Color(0xFFDADADA),
                                width: 1,
                              ),
                            ),
                            child: Center(
                              child: Text(
                                data.id![0].toUpperCase(),
                                style: const TextStyle(
                                  fontSize: 47,
                                  color: AppColor.textPrimary,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                            ),
                          ),
                        ),
                        const Gap(60),
                        const Center(
                          child: Text(
                            'Center Name',
                            style: TextStyle(
                              fontFamily: 'SF Pro',
                              fontWeight: AppFontWeight.semiBold,
                              fontSize: 20,
                            ),
                          ),
                        ),
                        const Gap(8),
                        Center(
                            child: Text(
                          data.id!,
                          style: const TextStyle(fontFamily: 'SF Pro', fontWeight: AppFontWeight.regular, fontSize: 14, color: AppColor.textGray),
                        )),
                        const Gap(32),
                        const Spacer(),
                        Padding(
                          padding: const EdgeInsets.only(bottom: 20),
                          child: SecondaryButton(
                            onTap: () async {
                              final hasError = await ref.read(loginCenterProvider.notifier).logout();

                              if (hasError && context.mounted) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  const SnackBar(content: Text('Logout fail')),
                                );
                              } else {
                                if (context.mounted) {
                                  context.go(WelcomeScreen.routeName);
                                }
                              }
                            },
                            text: 'Logout',
                          ),
                        ),
                        Gap(bottomInset),
                      ],
                    ),
                  ),
                );
              },
            );
          }),
        ),
      ),
    );
  }
}
