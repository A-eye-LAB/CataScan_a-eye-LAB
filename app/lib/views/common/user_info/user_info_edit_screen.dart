import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/models/user_info.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/common/logout_dialog.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:cata_scan_flutter/services/user_info/user_info_provider.dart';
import 'package:cata_scan_flutter/models/gender.dart';

class UserInfoEditScreen extends ConsumerStatefulWidget {
  const UserInfoEditScreen({super.key});
  static const String routeName = '/user_info_edit';

  @override
  ConsumerState<UserInfoEditScreen> createState() => _InfoEditScreenState();
}

class _InfoEditScreenState extends ConsumerState<UserInfoEditScreen> {
  final TextEditingController _nameController = TextEditingController();
  final FocusNode _nameFocusNode = FocusNode();

  String? _tempName;
  Gender? _tempGender;
  AgeGroup? _tempAgeGroup;

  String? _initialName;
  Gender? _initialGender;
  AgeGroup? _initialAgeGroup;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      final (result, userInfo) = await ref.read(userInfoProvider.future);
      if (result && userInfo != null) {
        setState(() {
          _initialName = userInfo.name;
          _initialGender = userInfo.gender;
          _initialAgeGroup = userInfo.ageRange;

          _tempName = userInfo.name;
          _tempGender = userInfo.gender;
          _tempAgeGroup = userInfo.ageRange;
          _nameController.text = userInfo.name;
        });
      }
    });
  }

  bool get _isModified {
    return _tempName != _initialName ||
        _tempGender != _initialGender ||
        _tempAgeGroup != _initialAgeGroup;
  }

  @override
  void dispose() {
    _nameController.dispose();
    _nameFocusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final userInfo = ref.watch(userInfoProvider);
    final bottomInset = MediaQuery.viewInsetsOf(context).bottom;
    return Scaffold(
      backgroundColor: AppColor.backgroundWhite,
      resizeToAvoidBottomInset: false,
      body: GestureDetector(
        onTap: () => FocusScope.of(context).unfocus(),
        child: SafeArea(
          child: LayoutBuilder(builder: (context, constraints) {
            final maxHeight = constraints.maxHeight + bottomInset;
            return userInfo.when(
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, stack) =>
                  const Center(child: Text('Error loading user info')),
              data: ((bool, UserInfo?) data) {
                final (_, userInfo) = data;
                if (userInfo == null) {
                  return const Center(child: Text('No user info'));
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
                                icon: const Icon(
                                    Icons.arrow_back_ios_new_rounded),
                              ),
                              const Spacer(),
                              TextButton(
                                onPressed: () => showDialog(
                                  context: context,
                                  barrierDismissible: false,
                                  useSafeArea: false,
                                  builder: (context) => const LogoutDialog(),
                                ),
                                child: const Text(
                                  'Logout',
                                  style: TextStyle(
                                    fontSize: 16,
                                    color: AppColor.textPrimary,
                                    fontWeight: AppFontWeight.semiBold,
                                  ),
                                ),
                              ),
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
                                userInfo.name[0].toUpperCase(),
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
                        _nameField(userInfo),
                        const Gap(32),
                        _ageGroupField(userInfo),
                        const Gap(32),
                        _sexField(userInfo),
                        const Gap(32),
                        const Spacer(),
                        _logoutButton(),
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

  Column _sexField(UserInfo userInfo) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          "Sex",
          style: TextStyle(
            fontSize: 20,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
        ),
        const Gap(12),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: const Color(0xFFDADADA)),
            borderRadius: BorderRadius.circular(8),
          ),
          child: DropdownButtonHideUnderline(
            child: DropdownButton<String>(
              value: _tempGender?.text ?? userInfo.gender.text,
              isExpanded: true,
              isDense: true,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 13),
              icon: const Icon(Icons.keyboard_arrow_down_rounded),
              iconSize: 26,
              items:
                  Gender.values.map<DropdownMenuItem<String>>((Gender value) {
                return DropdownMenuItem<String>(
                  value: value.text,
                  child: Text(
                    value.text,
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: AppFontWeight.regular,
                      color: AppColor.textBlack.withOpacity(0.8),
                    ),
                  ),
                );
              }).toList(),
              onChanged: (value) {
                _tempGender = Gender.values.firstWhere((e) => e.text == value);
                setState(() {});
              },
            ),
          ),
        ),
      ],
    );
  }

  Column _ageGroupField(UserInfo userInfo) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          "Age Group",
          style: TextStyle(
            fontSize: 20,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
        ),
        const Gap(12),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: const Color(0xFFDADADA)),
            borderRadius: BorderRadius.circular(8),
          ),
          child: DropdownButtonHideUnderline(
            child: DropdownButton<String>(
              value: _tempAgeGroup?.text ?? userInfo.ageRange.text,
              isExpanded: true,
              isDense: true,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 13),
              icon: const Icon(Icons.keyboard_arrow_down_rounded),
              iconSize: 26,
              items: AgeGroup.values
                  .map<DropdownMenuItem<String>>((AgeGroup value) {
                return DropdownMenuItem<String>(
                  value: value.text,
                  child: Text(
                    value.text,
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: AppFontWeight.regular,
                      color: AppColor.textBlack.withOpacity(0.8),
                      height: 1.2,
                    ),
                  ),
                );
              }).toList(),
              onChanged: (value) {
                _tempAgeGroup =
                    AgeGroup.values.firstWhere((e) => e.text == value);
                setState(() {});
              },
            ),
          ),
        ),
      ],
    );
  }

  Widget _logoutButton() {
    return Padding(
      padding: const EdgeInsets.only(bottom: 20),
      child: PrimaryButton(
        onTap: () async {
          try {
            if (_tempName != null) {
              await ref.read(userInfoProvider.notifier).updateName(_tempName!);
            }
            if (_tempGender != null) {
              await ref
                  .read(userInfoProvider.notifier)
                  .updateGender(_tempGender!);
            }
            if (_tempAgeGroup != null) {
              await ref
                  .read(userInfoProvider.notifier)
                  .updateAgeRange(_tempAgeGroup!);
            }

            if (!context.mounted) return;

            setState(() {
              _initialName = _tempName;
              _initialGender = _tempGender;
              _initialAgeGroup = _tempAgeGroup;
            });

            if (!mounted) return;

            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Container(
                  height: 56,
                  padding: const EdgeInsets.symmetric(horizontal: 20),
                  child: const Center(
                    child: Text(
                      'Successfully updated',
                      style: TextStyle(fontSize: 16, color: AppColor.textWhite),
                    ),
                  ),
                ),
                backgroundColor: AppColor.textBlack,
                duration: const Duration(seconds: 2),
                padding: EdgeInsets.zero,
                margin: const EdgeInsets.only(
                  bottom: 20,
                  left: 20,
                  right: 20,
                ),
                behavior: SnackBarBehavior.floating,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
            );
            ref.invalidate(userInfoProvider);
          } catch (e) {
            if (!context.mounted) return;

            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Container(
                  height: 56,
                  padding: const EdgeInsets.symmetric(horizontal: 20),
                  child: const Center(
                    child: Text(
                      'Failed to update information',
                      style: TextStyle(fontSize: 16, color: AppColor.textWhite),
                    ),
                  ),
                ),
                backgroundColor: AppColor.textBlack,
                duration: const Duration(seconds: 2),
                padding: EdgeInsets.zero,
                margin: const EdgeInsets.only(
                  bottom: 20,
                  left: 20,
                  right: 20,
                ),
                behavior: SnackBarBehavior.floating,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
            );
          }
        },
        text: 'Save',
        isDisabled: !_isModified,
      ),
    );
  }

  Column _nameField(UserInfo userInfo) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          "Name",
          style: TextStyle(
            fontSize: 20,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
        ),
        const Gap(12),
        TextField(
          controller: _nameController,
          onChanged: (value) {
            setState(() {
              _tempName = value;
            });
          },
          focusNode: _nameFocusNode,
          textInputAction: TextInputAction.done,
          decoration: InputDecoration(
            contentPadding:
                const EdgeInsets.symmetric(horizontal: 16, vertical: 13),
            isDense: true,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(
                color: Color(0xFFDADADA),
              ),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(
                color: Color(0xFFDADADA),
              ),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(8),
              borderSide: const BorderSide(
                color: AppColor.textPrimary,
              ),
            ),
          ),
          cursorColor: AppColor.textPrimary,
          cursorWidth: 2,
          style: TextStyle(
            fontSize: 20,
            fontWeight: AppFontWeight.regular,
            color: AppColor.textBlack.withOpacity(0.8),
          ),
        ),
      ],
    );
  }
}
