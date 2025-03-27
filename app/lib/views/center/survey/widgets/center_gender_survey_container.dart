import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/views/common/button.dart';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:cata_scan_flutter/models/gender.dart';
import 'package:cata_scan_flutter/services/patient_info/patient_info_provider.dart';

class CenterGenderSurveyContainer extends ConsumerStatefulWidget {
  const CenterGenderSurveyContainer({
    super.key,
    required this.onPrevious,
    required this.onConfirm,
  });
  final Function() onPrevious;
  final Function() onConfirm;

  @override
  ConsumerState<CenterGenderSurveyContainer> createState() =>
      _CenterGenderSurveyContainerState();
}

class _CenterGenderSurveyContainerState
    extends ConsumerState<CenterGenderSurveyContainer> {
  Gender? selectedGender;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Text(
          'Select Patient\'s Sex',
          style: TextStyle(
            fontSize: 28,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
        ),
        const Gap(50),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: selectedGender == null
                ? [
                    Expanded(child: buildGenderDefaultButton(Gender.male)),
                    const Gap(12),
                    Expanded(child: buildGenderDefaultButton(Gender.female)),
                    const Gap(12),
                    Expanded(child: buildGenderDefaultButton(Gender.others)),
                  ]
                : [
                    Expanded(
                        child: buildGenderButton(
                            Gender.male, selectedGender == Gender.male)),
                    const Gap(12),
                    Expanded(
                        child: buildGenderButton(
                            Gender.female, selectedGender == Gender.female)),
                    const Gap(12),
                    Expanded(
                        child: buildGenderButton(
                            Gender.others, selectedGender == Gender.others)),
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
                    if (selectedGender == null) {
                      return;
                    }
                    await ref
                        .read(patientProvider.notifier)
                        .updateGender(selectedGender!);
                    widget.onConfirm();
                  },
                  text: 'Confirm',
                  isDisabled: selectedGender == null,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget buildGenderDefaultButton(Gender gender) {
    return GestureDetector(
      onTap: () {
        setState(() {
          selectedGender = gender;
        });
      },
      child: Container(
        width: double.infinity,
        height: 66,
        decoration: BoxDecoration(
          color: AppColor.button3BackgroundDefault,
          borderRadius: BorderRadius.circular(8),
        ),
        child: Center(
          child: Text(
            gender.text,
            style: TextStyle(
              fontSize: 18,
              fontWeight: AppFontWeight.regular,
              color: AppColor.button3TextDefault,
            ),
          ),
        ),
      ),
    );
  }

  Widget buildGenderButton(Gender gender, bool isSelected) {
    return GestureDetector(
      onTap: () {
        setState(() {
          selectedGender = gender;
        });
      },
      child: Opacity(
        opacity: isSelected ? 1.0 : 0.35,
        child: Container(
          width: double.infinity,
          height: 66,
          decoration: isSelected
              ? BoxDecoration(
                  gradient: AppColor.button3Gradient,
                  borderRadius: BorderRadius.circular(8),
                )
              : BoxDecoration(
                  color: AppColor.button3BackgroundDisabled,
                  borderRadius: BorderRadius.circular(8),
                ),
          child: Center(
            child: Text(
              gender.text,
              style: TextStyle(
                fontSize: 18,
                fontWeight:
                    isSelected ? AppFontWeight.semiBold : AppFontWeight.regular,
                color: isSelected
                    ? AppColor.button3Text
                    : AppColor.button3TextDisabled,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
