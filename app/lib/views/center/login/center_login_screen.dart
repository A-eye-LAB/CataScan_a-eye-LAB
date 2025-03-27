import 'package:cata_scan_flutter/views/center/main/center_main_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cata_scan_flutter/core/styles/app_button_styles.dart';
import 'package:go_router/go_router.dart';
import 'package:gap/gap.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/providers/api_error_state_provider.dart';
import 'package:cata_scan_flutter/services/login/login_center_provider.dart';
import 'package:cata_scan_flutter/core/styles/app_asset.dart';

class CenterLoginCenterScreen extends ConsumerStatefulWidget {
  static const routeName = '/login_center';
  const CenterLoginCenterScreen({super.key});

  @override
  ConsumerState<CenterLoginCenterScreen> createState() =>
      _CenterLoginCenterState();
}

class _CenterLoginCenterState extends ConsumerState<CenterLoginCenterScreen> {
  final centerIdController = TextEditingController();
  final passwordController = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    centerIdController.dispose();
    passwordController.dispose();
    super.dispose();
  }

  Future<void> _handleLogin(BuildContext context) async {
    if (centerIdController.text.isEmpty || passwordController.text.isEmpty) {
      ref.read(apiErrorStateProvider.notifier).state =
          'Please fill in all fields';
      return;
    }

    final hasError = await ref.read(loginCenterProvider.notifier).login(
          centerIdController.text,
          passwordController.text,
        );
    if (context.mounted) {
      if (hasError) {
        //TODO: 우선은 디자인팀 요구사항으로 아래메시지 출력. 추후 백엔드팀의 에러메시지 받아서 맞게 수정 필요함
        //ref.read(apiErrorStateProvider.notifier).state = e.toString();
        ref.read(apiErrorStateProvider.notifier).state =
            'Your Center ID or password is incorrect. Please check and try again.';
      } else {
        ref.read(apiErrorStateProvider.notifier).state = null;
        context.go(CenterMainScreen.routeName);
      }
    }
  }

  Widget showLoginResult(String message) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Container(
        height: 64,
        decoration: BoxDecoration(
            color: const Color(0xFFFFDAE2),
            borderRadius: BorderRadius.circular(8.0)),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(flex: 2, child: Image.asset(IconAsset.alarm)),
            Expanded(
              flex: 9,
              child: Text(
                message,
                style: const TextStyle(
                  color: Color(0xFFDA0015),
                  fontFamily: 'SF Pro',
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                  height: 1.4,
                  letterSpacing: 0.23,
                ),
              ),
            )
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final apiErrorMessage = ref.watch(apiErrorStateProvider);

    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: ListView(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                children: [
                  const Gap(100),
                  const Text(
                    'CataScan for\nHealth Center.',
                    style: TextStyle(
                        fontSize: 40,
                        fontWeight: FontWeight.bold,
                        fontFamily: 'SF Pro'),
                  ),
                  const Gap(60),
                  UserTextField(
                    label: 'Center ID',
                    controller: centerIdController,
                  ),
                  const Gap(16),
                  UserTextField(
                    label: 'Password',
                    controller: passwordController,
                    isPassword: true,
                  ),
                  const Gap(24),
                  if (apiErrorMessage != null) showLoginResult(apiErrorMessage),
                ],
              ),
            ),
            Consumer(
              builder: (context, ref, child) {
                final loginState = ref.watch(loginCenterProvider);

                return Padding(
                  padding: const EdgeInsets.all(24),
                  child: ElevatedButton(
                    style: AppButtonStyles.primaryStyle(double.infinity, 60),
                    onPressed: loginState.isLoading
                        ? null
                        : () async {
                            await _handleLogin(context);
                          },
                    child: loginState.isLoading
                        ? const CircularProgressIndicator()
                        : const Text('Sign in'),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}

class UserTextField extends StatelessWidget {
  final String label;
  final TextEditingController controller;
  final bool isPassword;
  const UserTextField(
      {super.key,
      required this.label,
      required this.controller,
      this.isPassword = false});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 16),
          child: Text(
            label,
            style: const TextStyle(fontSize: 24, fontWeight: FontWeight.normal),
          ),
        ),
        const Gap(8),
        Padding(
          padding: const EdgeInsets.only(left: 24.0, right: 24),
          child: TextField(
            controller: controller,
            obscureText: isPassword,
            decoration: InputDecoration(
              hintText: 'Enter the $label',
              hintStyle: const TextStyle(
                fontFamily: 'SF Pro',
                color: AppColor.textGray,
                fontSize: 16,
              ),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10),
              ),
              contentPadding: const EdgeInsets.symmetric(
                horizontal: 16,
                vertical: 12,
              ),
              enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10),
                borderSide: const BorderSide(
                  color: AppColor.textGray,
                  width: 1.0,
                ),
              ),
              focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10),
                borderSide: const BorderSide(
                  color: Colors.blue,
                  width: 2.0,
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
