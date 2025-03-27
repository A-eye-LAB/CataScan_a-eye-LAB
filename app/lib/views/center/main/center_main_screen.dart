import 'dart:io';

import 'package:cata_scan_flutter/core/providers/permission_service_provider.dart';
import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/views/center/survey/center_survey_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:cata_scan_flutter/models/eye_scan_data.dart';
import 'package:cata_scan_flutter/services/login/login_center_provider.dart';
import 'package:cata_scan_flutter/models/patient_info.dart';
import 'package:cata_scan_flutter/core/utils/pst_time_zone_converter.dart';
import 'package:cata_scan_flutter/services/patient_info/patient_list_provider.dart';
import 'package:cata_scan_flutter/services/patient_info/patient_list_upload_provider.dart';
import 'package:cata_scan_flutter/views/center/login/center_info_screen.dart';
import 'package:permission_handler/permission_handler.dart';

class CenterMainScreen extends ConsumerStatefulWidget {
  const CenterMainScreen({super.key});
  static const String routeName = '/center_main';

  @override
  ConsumerState<CenterMainScreen> createState() => CenterMainScreenState();
}

class CenterMainScreenState extends ConsumerState<CenterMainScreen> {
  @override
  void initState() {
    super.initState();
  }

  void _showPermissionDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: const Row(
          children: [
            Icon(Icons.camera_alt_outlined, color: Color(0xFF1A1A1A)),
            SizedBox(width: 20),
            Text(
              'Camera Access',
              style: TextStyle(
                fontSize: 26,
                fontWeight: AppFontWeight.semiBold,
                color: AppColor.textBlack,
              ),
            ),
          ],
        ),
        content: const Text(
          'Camera access is required to diagnose cataracts. Please enable camera permission in Settings to continue.',
          style: TextStyle(
            fontSize: 16,
            fontWeight: AppFontWeight.regular,
            color: AppColor.textBlack,
          ),
        ),
        actions: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                },
                child: const Text(
                  'Cancel',
                  style: TextStyle(
                    color: AppColor.textBlack,
                    fontSize: 14,
                  ),
                ),
              ),
              TextButton(
                onPressed: () async {
                  Navigator.pop(context);
                  await openAppSettings();
                },
                child: const Text(
                  'Open Settings',
                  style: TextStyle(
                    color: AppColor.textBlack,
                    fontSize: 14,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final AsyncValue<List<PatientInfo>>? patientListState =
        ref.watch(patientListProvider);
    return Scaffold(
      backgroundColor: AppColor.backgroundWhite,
      body: LayoutBuilder(builder: (context, constraints) {
        final maxWidth = constraints.maxWidth;
        final maxHeight = constraints.maxHeight;
        return Stack(
          children: [
            Container(
              width: maxWidth,
              height: maxHeight,
              color: AppColor.backgroundWhite,
              child: Column(
                children: [
                  Gap(MediaQuery.paddingOf(context).top),
                  const Gap(24),
                  _buildHeader(''),
                  const Gap(40),
                  patientListState!.when(
                    data: (patientList) => patientList.isEmpty
                        ? Column(
                            children: [
                              const Gap(60),
                              _buildInfoCard(),
                            ],
                          )
                        : Expanded(
                            child: Column(
                              children: [
                                _historyHeader(),
                                const Gap(10),
                                Expanded(
                                  child: ListView.builder(
                                    padding: EdgeInsets.only(
                                        left: 20,
                                        right: 20,
                                        bottom: 150 +
                                            MediaQuery.paddingOf(context)
                                                .bottom),
                                    itemCount: patientList.length,
                                    itemBuilder: (context, index) {
                                      final patientInfo = patientList[index];
                                      return Container(
                                        margin:
                                            const EdgeInsets.only(bottom: 16),
                                        padding: const EdgeInsets.symmetric(
                                            horizontal: 16, vertical: 14),
                                        height: 120,
                                        width: double.infinity,
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(8),
                                          border: Border.all(
                                              color: const Color(0xFFECECEC),
                                              width: 1),
                                        ),
                                        child: _buildPatientHistoryContainer(
                                            patientInfo),
                                      );
                                    },
                                  ),
                                ),
                              ],
                            ),
                          ),
                    loading: () =>
                        const Center(child: CircularProgressIndicator()),
                    error: (error, stack) => Center(
                      child: Text('Error: $error'),
                    ),
                  )
                ],
              ),
            ),
            Align(
              alignment: Alignment.bottomCenter,
              child: Container(
                width: maxWidth,
                height: 150 + MediaQuery.paddingOf(context).bottom,
                padding: EdgeInsets.only(
                    left: 20,
                    right: 20,
                    bottom: 20 + MediaQuery.paddingOf(context).bottom),
                decoration: BoxDecoration(
                  gradient: AppColor.button4GradientDefault,
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    (patientListState.value != null &&
                            patientListState.value!.isNotEmpty)
                        ? _buildUploadButton(context, patientListState.value!)
                        : const SizedBox.shrink(),
                    const Gap(12),
                    _buildScanButton(context),
                  ],
                ),
              ),
            ),
          ],
        );
      }),
    );
  }

  Container _historyHeader() {
    return Container(
      height: 60,
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: const Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Text(
            "History",
            style: TextStyle(fontSize: 20, fontWeight: AppFontWeight.semiBold),
          ),
        ],
      ),
    );
  }

  Widget _buildHeader(String name) {
    final loginState = ref.watch(loginCenterProvider);

    return loginState.when(
      loading: () => const Center(
        child: CircularProgressIndicator(),
      ),
      error: (error, stack) => Center(
        child: Text(error.toString()),
      ),
      data: (data) {
        if (data.id?.isEmpty ?? true) {
          return const SizedBox.shrink();
        }

        return Container(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          height: 80,
          width: double.infinity,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Hello,\n${data.id}',
                style: const TextStyle(
                    fontSize: 28, fontWeight: AppFontWeight.bold),
              ),
              GestureDetector(
                onTap: () => context.push(CenterInfoScreen.routeName),
                child: SvgPicture.asset(VectorAsset.personIcon,
                    width: 40, height: 40),
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildInfoCard() {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 32),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(8),
        color: AppColor.backgroundLightGray,
      ),
      child: Column(
        children: [
          SvgPicture.asset(
            VectorAsset.mainDataEmpty,
            width: 177,
            fit: BoxFit.fitWidth,
          ),
          const Gap(26),
          const Text(
            'We can help assess\nyour risk of cataracts.',
            style: TextStyle(
              color: AppColor.textBlack,
              fontSize: 19,
              fontWeight: AppFontWeight.regular,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Widget _buildScanButton(BuildContext context) {
    return GestureDetector(
      onTap: () async {
        final permissionService = ref.read(permissionServiceProvider);
        final isGranted = await permissionService.getCameraPermission();
        if (isGranted && context.mounted) {
          context.push(CenterSurveyScreen.routeName);
        } else if (context.mounted) {
          _showPermissionDialog();
        }
      },
      child: Container(
        width: double.infinity,
        height: 52,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          color: AppColor.button1Background,
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            SvgPicture.asset(
              VectorAsset.eyeIconWhite,
              height: 30,
              width: 30,
            ),
            const Gap(8),
            const Text(
              'Add Scan Data',
              style: TextStyle(
                color: AppColor.button1Text,
                fontSize: 17,
                fontWeight: AppFontWeight.semiBold,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUploadButton(BuildContext context, List<PatientInfo> dataList) {
    final uploadState = ref.watch(patientListUploadProvider);

    return GestureDetector(
      onTap: uploadState.isLoading
          ? null
          : () async {
              await ref
                  .read(patientListUploadProvider.notifier)
                  .uploadListData(context, dataList);
            },
      child: Container(
        width: double.infinity,
        height: 52,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          color: uploadState.isLoading
              ? AppColor.button2Background.withOpacity(0.7)
              : AppColor.button2Background,
          border: Border.all(
            color: AppColor.button2Border,
            width: 1,
          ),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            uploadState.when(
              data: (message) => SvgPicture.asset(
                VectorAsset.upload,
                height: 30,
                width: 30,
              ),
              loading: () => const SizedBox(
                height: 30,
                width: 30,
                child: CircularProgressIndicator(),
              ),
              error: (_, __) => const Icon(
                Icons.error_outline,
                color: Colors.red,
                size: 30,
              ),
            ),
            const SizedBox(width: 8),
            Text(
              uploadState.when(
                data: (message) => 'Upload to Web',
                loading: () => 'Uploading...',
                error: (error, _) => 'Upload Failed',
              ),
              style: const TextStyle(
                color: AppColor.button2Text,
                fontSize: 17,
                fontWeight: AppFontWeight.semiBold,
              ),
            ),
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
        overflow: TextOverflow.ellipsis,
        maxLines: 1,
      ),
    );
  }
}

Widget _buildPatientHistoryContainer(PatientInfo patient) {
  return Column(
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            PSTTimeZoneConverter.convertToPST(patient.eyeScanData.timestamp),
            style: const TextStyle(
              fontSize: 12,
              fontWeight: AppFontWeight.regular,
              color: AppColor.textBlack,
            ),
          ),
          Text('${patient.name ?? ''} / ${patient.gender?.text[0] ?? ''}')
        ],
      ),
      IntrinsicHeight(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              flex: 1,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: Image.file(
                      File(patient.eyeScanData.leftEyeScanPath),
                      width: 64,
                      height: 64,
                    ),
                  ),
                  const Gap(8),
                  Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          "Left",
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: AppFontWeight.regular,
                            color: Color(0xFF646464),
                          ),
                        ),
                        _AnalysisResultChip(
                            eyeStatus: patient.eyeScanData.leftStatus),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            VerticalDivider(
              color: AppColor.textBlack.withOpacity(0.3),
              thickness: 1,
              width: 1,
            ),
            Expanded(
              flex: 1,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Gap(16),
                  ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: Image.file(
                      File(patient.eyeScanData.rightEyeScanPath),
                      width: 64,
                      height: 64,
                    ),
                  ),
                  const Gap(8),
                  Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          "Right",
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: AppFontWeight.regular,
                            color: Color(0xFF646464),
                          ),
                        ),
                        _AnalysisResultChip(
                            eyeStatus: patient.eyeScanData.rightStatus),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    ],
  );
}
