import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/models/user_info.dart';
import 'package:cata_scan_flutter/services/storage/user_info_storage.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/user/main/main_screen.dart';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';

class AgeGroupSurveyContainer extends ConsumerStatefulWidget {
  const AgeGroupSurveyContainer({super.key, required this.onPrevious});
  final Function() onPrevious;

  @override
  ConsumerState<AgeGroupSurveyContainer> createState() => _AgeGroupSurveyContainerState();
}

class _AgeGroupSurveyContainerState extends ConsumerState<AgeGroupSurveyContainer> {
  AgeGroup? selectedAgeGroup;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Text(
          'Enter your age group',
          style: TextStyle(
            fontSize: 28,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
        ),
        const Gap(50),
        Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            children: selectedAgeGroup == null
                ? [
                    buildAgeGruopDefaultButton(AgeGroup.under10),
                    const Gap(12),
                    buildAgeGruopDefaultButton(AgeGroup.under20),
                    const Gap(12),
                    buildAgeGruopDefaultButton(AgeGroup.under30),
                    const Gap(12),
                    buildAgeGruopDefaultButton(AgeGroup.under40),
                    const Gap(12),
                    buildAgeGruopDefaultButton(AgeGroup.under50),
                    const Gap(12),
                    buildAgeGruopDefaultButton(AgeGroup.over50),
                  ]
                : [
                    buildAgeGroupButton(AgeGroup.under10, selectedAgeGroup == AgeGroup.under10),
                    const Gap(12),
                    buildAgeGroupButton(AgeGroup.under20, selectedAgeGroup == AgeGroup.under20),
                    const Gap(12),
                    buildAgeGroupButton(AgeGroup.under30, selectedAgeGroup == AgeGroup.under30),
                    const Gap(12),
                    buildAgeGroupButton(AgeGroup.under40, selectedAgeGroup == AgeGroup.under40),
                    const Gap(12),
                    buildAgeGroupButton(AgeGroup.under50, selectedAgeGroup == AgeGroup.under50),
                    const Gap(12),
                    buildAgeGroupButton(AgeGroup.over50, selectedAgeGroup == AgeGroup.over50),
                  ],
          ),
        ),
        const Spacer(),
        Padding(
          padding: const EdgeInsets.only(bottom: 20, left: 20, right: 20),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                child: SecondaryButton(
                  onTap: () {
                    widget.onPrevious();
                  },
                  text: 'Previous',
                ),
              ),
              const Gap(10),
              Flexible(
                child: PrimaryButton(
                  onTap: () async {
                    if (selectedAgeGroup == null) {
                      return;
                    }
                    await ref.read(userInfoStorageProvider).saveAgeRange(selectedAgeGroup!);
                    if (context.mounted) {
                      context.go(MainScreen.routeName);
                    }
                  },
                  text: 'Confirm',
                  isDisabled: selectedAgeGroup == null,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget buildAgeGroupButton(AgeGroup ageGroup, bool isSelected) {
    return GestureDetector(
      onTap: () {
        setState(() {
          selectedAgeGroup = ageGroup;
        });
      },
      child: Opacity(
        opacity: isSelected ? 1.0 : 0.35,
        child: Container(
          width: double.infinity,
          height: 52,
          decoration: isSelected
              ? BoxDecoration(
                  gradient: AppColor.button3Gradient,
                  borderRadius: BorderRadius.circular(8),
                )
              : BoxDecoration(
                  color: AppColor.button3BackgroundDisabled,
                  borderRadius: BorderRadius.circular(8),
                ),
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                ageGroup.text,
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: isSelected ? AppFontWeight.bold : AppFontWeight.regular,
                  color: isSelected ? AppColor.button3Text : AppColor.button3TextDisabled,
                ),
              ),
              SizedBox(
                width: 24,
                height: 24,
                child: isSelected
                    ? const Icon(
                        Icons.check,
                        color: AppColor.button3Text,
                      )
                    : const SizedBox.shrink(),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget buildAgeGruopDefaultButton(AgeGroup ageGroup) {
    return GestureDetector(
      onTap: () {
        setState(() {
          selectedAgeGroup = ageGroup;
        });
      },
      child: Container(
        width: double.infinity,
        height: 52,
        decoration: BoxDecoration(
          color: AppColor.button3BackgroundDefault,
          borderRadius: BorderRadius.circular(8),
        ),
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Text(
              ageGroup.text,
              style: TextStyle(
                fontSize: 18,
                fontWeight: AppFontWeight.regular,
                color: AppColor.button3TextDefault,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
