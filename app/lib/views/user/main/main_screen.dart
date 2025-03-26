import 'dart:io';

import 'package:cata_scan_flutter/core/providers/permission_service_provider.dart';
import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/models/user_info.dart';
import 'package:cata_scan_flutter/services/cataract_diagnosis/analysis_data_storage_provider.dart';
import 'package:cata_scan_flutter/services/user_info/user_info_provider.dart';
import 'package:cata_scan_flutter/views/common/camera/camera_select_screen.dart';
import 'package:cata_scan_flutter/views/common/user_info/user_info_edit_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_svg/flutter_svg.dart';
import '../../../models/eye_scan_data.dart';
import 'package:intl/intl.dart';

class MainScreen extends ConsumerStatefulWidget {
  const MainScreen({super.key});
  static const String routeName = '/main';

  @override
  ConsumerState<MainScreen> createState() => MainScreenState();
}

class MainScreenState extends ConsumerState<MainScreen> {
  @override
  Widget build(BuildContext context) {
    final AsyncValue<List<EyeScanData>> eyeScanDataList =
        ref.watch(analysisDataStorageProvider);
    final AsyncValue<(bool, UserInfo?)> userInfo = ref.watch(userInfoProvider);
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
                  userInfo.when(
                    data: (data) => _buildHeader(data.$2?.name ?? ''),
                    loading: () => _buildHeader(''),
                    error: (error, stack) => _buildHeader(''),
                  ),
                  const Gap(40),
                  eyeScanDataList.when(
                    data: (data) => _buildBanner(data),
                    loading: () => _buildBanner([]),
                    error: (error, stack) => Center(
                      child: Text('Error: $error'),
                    ),
                  ),
                  const Gap(20),
                  eyeScanDataList.when(
                    data: (data) => data.isEmpty
                        ? _buildInfoCard()
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
                                    itemCount: data.length,
                                    itemBuilder: (context, index) {
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
                                        child: _historyContainer(data, index),
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
                  ),
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
                child: Align(
                  alignment: Alignment.bottomCenter,
                  child: _buildScanButton(context),
                ),
              ),
            ),
          ],
        );
      }),
    );
  }

  Widget _historyContainer(List<EyeScanData> data, int index) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          DateFormat('dd MMM, yyyy').format(data[index].timestamp),
          style: const TextStyle(
            fontSize: 12,
            fontWeight: AppFontWeight.regular,
            color: AppColor.textBlack,
          ),
        ),
        IntrinsicHeight(
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Expanded(
                flex: 1,
                child: Row(
                  children: [
                    ClipRRect(
                      borderRadius: BorderRadius.circular(8),
                      child: Image.file(
                        File(data[index].leftEyeScanPath),
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
                              eyeStatus: data[index].leftStatus),
                        ],
                      ),
                    ),
                    const Gap(16),
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
                  children: [
                    const Gap(16),
                    ClipRRect(
                      borderRadius: BorderRadius.circular(8),
                      child: Image.file(
                        File(data[index].rightEyeScanPath),
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
                              eyeStatus: data[index].rightStatus),
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
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      height: 80,
      width: double.infinity,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Hello $name,\nCheck your Eye!',
            style:
                const TextStyle(fontSize: 28, fontWeight: AppFontWeight.bold),
          ),
          GestureDetector(
            onTap: () => context.push(UserInfoEditScreen.routeName),
            child:
                SvgPicture.asset(VectorAsset.personIcon, width: 40, height: 40),
          ),
        ],
      ),
    );
  }

  Widget _buildBanner(List<EyeScanData> eyeScanDataList) {
    final isNormal = !_hasAnyAbnormalStatus(eyeScanDataList);
    final backgroundColor = isNormal
        ? AppColor.bannerBackgroundNormal
        : AppColor.bannerBackgroundAbnormal;

    const String noScanMessage =
        'Ready for your first eye health check?\nScan now to get started!';
    const String healthyMessage =
        'Your eyes appear to be healthy!\nKeep up the good work!';
    const String riskMessage =
        'Potential cataract risk detected.\nWe recommend visiting your eye doctor.';

    final text = eyeScanDataList.isEmpty
        ? noScanMessage
        : isNormal
            ? healthyMessage
            : riskMessage;

    final textColor =
        isNormal ? AppColor.bannerTextNormal : AppColor.bannerTextAbnormal;
    final icon = isNormal ? VectorAsset.chatIcon : VectorAsset.dangerIcon;

    return Container(
      height: 70,
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.symmetric(horizontal: 12),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10),
        color: backgroundColor,
      ),
      child: Row(
        children: [
          SvgPicture.asset(icon, width: 24, height: 24),
          const SizedBox(width: 16),
          Expanded(
            child: Text(
              text,
              style: TextStyle(
                color: textColor,
                fontSize: 14,
                fontWeight: AppFontWeight.semiBold,
              ),
              softWrap: true,
              maxLines: 2,
            ),
          ),
        ],
      ),
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
          context.push(CameraSelectScreen.routeName);
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
            const SizedBox(width: 8),
            const Text(
              'Check Your Eye',
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

  bool _hasAnyAbnormalStatus(List<EyeScanData> scanList) {
    if (scanList.isEmpty) {
      return false;
    }
    return scanList.any((scan) =>
        scan.leftStatus == EyeStatus.abnormal ||
        scan.rightStatus == EyeStatus.abnormal);
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
