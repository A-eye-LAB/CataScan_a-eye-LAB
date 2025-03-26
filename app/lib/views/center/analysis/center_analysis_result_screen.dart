import 'dart:io';

import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/models/eye_scan_data.dart';
import 'package:cata_scan_flutter/models/patient_info.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/center/main/center_main_screen.dart';
import 'package:cata_scan_flutter/views/center/survey/center_survey_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import 'package:uuid/uuid.dart';

import 'package:cata_scan_flutter/services/patient_info/patient_info_provider.dart';

class CenterAnalysisResultScreen extends ConsumerWidget {
  const CenterAnalysisResultScreen({
    super.key,
    required this.leftEyePath,
    required this.leftEyeStatus,
    required this.rightEyePath,
    required this.rightEyeStatus,
  });
  static const routeName = '/center_analysis_result';
  final String leftEyePath;
  final EyeStatus leftEyeStatus;
  final String rightEyePath;
  final EyeStatus rightEyeStatus;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final patientState = ref.watch(patientProvider);
    final isLeftEyeRisky = leftEyeStatus != EyeStatus.normal;
    final isRightEyeRisky = rightEyeStatus != EyeStatus.normal;
    final isRisky = isLeftEyeRisky || isRightEyeRisky;
    return Scaffold(
      backgroundColor: AppColor.backgroundWhite,
      body: SafeArea(
        child: Column(
          children: [
            const SizedBox(
              height: 40,
              child: Center(
                child: Text(
                  'Result',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: AppFontWeight.medium,
                    color: AppColor.textBlack,
                  ),
                ),
              ),
            ),
            const Gap(20),
            Container(
              height: 84,
              width: double.infinity,
              margin: const EdgeInsets.symmetric(horizontal: 20),
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                gradient: isRisky
                    ? AppColor.resultTrueGradient
                    : AppColor.resultFalseGradient,
                borderRadius: BorderRadius.circular(10),
              ),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Container(
                    width: 45,
                    height: 45,
                    decoration: BoxDecoration(
                      color: AppColor.backgroundWhite.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Center(
                      child: isRisky
                          ? Image.asset(RasterAsset.resultTrue,
                              width: 30, height: 30)
                          : Image.asset(RasterAsset.resultFalse,
                              width: 30, height: 30),
                    ),
                  ),
                  const Gap(12),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        "Your Cataracts Status is",
                        style: TextStyle(
                          color: AppColor.textWhite,
                          fontSize: 16,
                          fontWeight: AppFontWeight.medium,
                        ),
                      ),
                      Text(
                        isRisky ? "Require Attention" : "Low Risk",
                        style: const TextStyle(
                          color: AppColor.textWhite,
                          fontSize: 20,
                          fontWeight: AppFontWeight.bold,
                        ),
                      ),
                    ],
                  )
                ],
              ),
            ),
            _CenterPatientState(
              patientState: patientState,
            ),
            const Gap(16),
            Container(
              width: double.infinity,
              margin: const EdgeInsets.symmetric(horizontal: 20),
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColor.backgroundWhite,
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: const Color(0xFFECECEC), width: 1),
              ),
              child: Column(
                children: [
                  const Align(
                    alignment: Alignment.centerLeft,
                    child: Text(
                      "Cataracts Status Result",
                      style: TextStyle(
                        color: AppColor.textBlack,
                        fontSize: 16,
                        fontWeight: AppFontWeight.semiBold,
                      ),
                    ),
                  ),
                  const Gap(12),
                  Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Container(
                              width: double.infinity,
                              decoration: BoxDecoration(
                                color: AppColor.backgroundLightGray,
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: Image.file(
                                File(leftEyePath),
                                fit: BoxFit.fitWidth,
                              ),
                            ),
                            const Gap(12),
                            const Text(
                              "Left Eye",
                              style: TextStyle(
                                fontSize: 12,
                                fontWeight: AppFontWeight.medium,
                                color: AppColor.textBlack,
                              ),
                            ),
                            const Gap(4),
                            Text(
                              DateFormat('dd MMM yyyy, hh:mm a')
                                  .format(DateTime.now()),
                              style: const TextStyle(
                                fontSize: 10,
                                fontWeight: AppFontWeight.regular,
                                color: Color(0xFF646464),
                              ),
                            ),
                            const Gap(8),
                            _AnalysisResultChip(eyeStatus: leftEyeStatus),
                          ],
                        ),
                      ),
                      const Gap(16),
                      const VerticalDivider(
                        color: AppColor.textGray,
                        thickness: 1,
                        width: 1,
                      ),
                      const Gap(16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Container(
                              width: double.infinity,
                              decoration: BoxDecoration(
                                color: AppColor.backgroundLightGray,
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: Image.file(
                                File(rightEyePath),
                                fit: BoxFit.fitWidth,
                              ),
                            ),
                            const Gap(12),
                            const Text(
                              "Right Eye",
                              style: TextStyle(
                                fontSize: 12,
                                fontWeight: AppFontWeight.medium,
                                color: AppColor.textBlack,
                              ),
                            ),
                            const Gap(4),
                            Text(
                              DateFormat('dd MMM yyyy, hh:mm a')
                                  .format(DateTime.now()),
                              style: const TextStyle(
                                fontSize: 10,
                                fontWeight: AppFontWeight.regular,
                                color: Color(0xFF646464),
                              ),
                            ),
                            const Gap(8),
                            _AnalysisResultChip(eyeStatus: rightEyeStatus),
                          ],
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const Gap(32),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: SecondaryButton(
                onTap: () {
                  ref.read(patientProvider.notifier).initial();
                  if (context.mounted) {
                    context.go(CenterMainScreen.routeName);
                  }
                },
                text: 'Exit Report',
              ),
            ),
            const Gap(8),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: PrimaryButton(
                onTap: () async {
                  final hasError =
                      await ref.read(patientProvider.notifier).uploadPatient(
                            leftEyePath,
                            rightEyePath,
                            leftEyeStatus,
                            rightEyeStatus,
                          );
                  if (context.mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text(
                          hasError
                              ? "Failed to upload patient data. Please try again."
                              : "Successfully uploaded patient data!",
                          style: const TextStyle(
                            color: AppColor.textWhite,
                            fontWeight: AppFontWeight.medium,
                          ),
                        ),
                        backgroundColor: hasError
                            ? const Color(0xffDA0016)
                            : const Color(0xff4378FF),
                        behavior: SnackBarBehavior.floating,
                        duration: const Duration(milliseconds: 2000),
                        dismissDirection: DismissDirection.down,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                      ),
                    );
                    if (hasError) {
                      context.go(CenterMainScreen.routeName);
                    } else {
                      context.go(CenterSurveyScreen.routeName);
                    }
                  }
                },
                text: "Start Another Scan",
                isDisabled: false,
              ),
            ),
            const Gap(30),
          ],
        ),
      ),
    );
  }
}

class _AnalysisResultChip extends StatelessWidget {
  const _AnalysisResultChip({required this.eyeStatus});
  final EyeStatus eyeStatus;

  @override
  Widget build(BuildContext context) {
    bool isRisky = eyeStatus != EyeStatus.normal;
    final backgroundColor = isRisky
        ? const Color(0xFFFFF1F4)
        : const Color(0xff4378FF).withOpacity(0.1);
    final textColor =
        isRisky ? const Color(0xffDA0016) : const Color(0xff4378FF);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(999),
      ),
      child: Text(
        isRisky ? "Require Attention" : "Low Risk",
        style: TextStyle(
          color: textColor,
          fontSize: 10,
          fontWeight: AppFontWeight.semiBold,
        ),
      ),
    );
  }
}

class _CenterPatientState extends StatelessWidget {
  final AsyncValue<PatientInfo>? patientState;

  const _CenterPatientState({this.patientState});

  @override
  Widget build(BuildContext context) {
    if (patientState == null) {
      return const Gap(0);
    }

    return Column(
      children: [
        const Gap(16),
        patientState!.when(
          loading: () => const Center(
            child: CircularProgressIndicator(),
          ),
          error: (error, stack) => Center(
            child: Text(error.toString()),
          ),
          data: (patientInfo) => Container(
            height: 130,
            width: double.infinity,
            margin: const EdgeInsets.symmetric(horizontal: 20),
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(10),
              color: AppColor.resultPatientInfo,
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Patient Information',
                  style: TextStyle(
                    color: Colors.black,
                    fontSize: 15,
                    fontWeight: AppFontWeight.semiBold,
                  ),
                  textAlign: TextAlign.left,
                ),
                const Gap(8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'ID',
                      style: TextStyle(
                        color: Color(0xFF999999),
                        fontSize: 13,
                        fontWeight: AppFontWeight.semiBold,
                      ),
                    ),
                    Text(
                      const Uuid().v4().substring(0, 16),
                      style: const TextStyle(
                        color: Colors.black,
                        fontSize: 13,
                        fontWeight: AppFontWeight.medium,
                      ),
                    ),
                  ],
                ),
                const Gap(4),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'Name',
                      style: TextStyle(
                        color: Color(0xFF999999),
                        fontSize: 13,
                        fontWeight: AppFontWeight.semiBold,
                      ),
                    ),
                    Text(
                      patientInfo.name ?? '',
                      style: const TextStyle(
                        color: Colors.black,
                        fontSize: 13,
                        fontWeight: AppFontWeight.medium,
                      ),
                    ),
                  ],
                ),
                const Gap(4),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'Gender',
                      style: TextStyle(
                        color: Color(0xFF999999),
                        fontSize: 13,
                        fontWeight: AppFontWeight.semiBold,
                      ),
                    ),
                    Text(
                      patientInfo.gender?.text ?? '',
                      style: const TextStyle(
                        color: Colors.black,
                        fontSize: 13,
                        fontWeight: AppFontWeight.medium,
                      ),
                    ),
                  ],
                ),
                const Gap(4),
                // Text(
                //   'TODO Comment',
                //   textAlign: TextAlign.right,
                // ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
