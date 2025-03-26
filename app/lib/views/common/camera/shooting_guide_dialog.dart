import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:camera/camera.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';

class ShootingGuideDialog extends StatelessWidget {
  const ShootingGuideDialog({super.key, required this.cameraLensDirection});
  final CameraLensDirection cameraLensDirection;

  @override
  Widget build(BuildContext context) {
    return cameraLensDirection == CameraLensDirection.front ? const _FrontShootingGuideDialog() : const _BackShootingGuideDialog();
  }
}

class _FrontShootingGuideDialog extends StatelessWidget {
  const _FrontShootingGuideDialog();

  @override
  Widget build(BuildContext context) {
    final bottomPadding = MediaQuery.paddingOf(context).bottom;
    return Container(
      height: 455 + bottomPadding,
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.only(
          topLeft: Radius.circular(20),
          topRight: Radius.circular(20),
        ),
      ),
      padding: EdgeInsets.only(bottom: bottomPadding, left: 20, right: 20),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          const Gap(10),
          Container(
            width: 100,
            height: 4,
            decoration: BoxDecoration(
              color: Colors.black.withOpacity(0.05),
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const Gap(46),
          const Text(
            'Check your surroundings\nbefore scanning !',
            style: TextStyle(
              fontSize: 26,
              fontWeight: AppFontWeight.semiBold,
              color: AppColor.textBlack,
            ),
            textAlign: TextAlign.center,
          ),
          const Gap(60),
          SizedBox(
            width: 310,
            height: 90,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Image.asset(RasterAsset.electricBulb, width: 24, height: 24),
                    const Gap(16),
                    const Expanded(
                      child: Text(
                        'Make sure you\'re in a well-lit area.',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: AppFontWeight.regular,
                          color: AppColor.textBlack,
                        ),
                      ),
                    ),
                  ],
                ),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Image.asset(RasterAsset.eyeGlass, width: 24, height: 24),
                    const Gap(16),
                    const Expanded(
                      child: Text(
                        'Remove your glasses and adjust any hair covering your eyes.',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: AppFontWeight.regular,
                          color: AppColor.textBlack,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          const Spacer(),
          GestureDetector(
            onTap: () => context.pop(),
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
                  Image.asset(RasterAsset.photoCamera, width: 30, height: 30),
                  const SizedBox(width: 8),
                  const Text(
                    'Start Eye Scan',
                    style: TextStyle(
                      color: AppColor.button1Text,
                      fontSize: 17,
                      fontWeight: AppFontWeight.semiBold,
                    ),
                  ),
                ],
              ),
            ),
          ),
          const Gap(36),
        ],
      ),
    );
  }
}

class _BackShootingGuideDialog extends StatefulWidget {
  const _BackShootingGuideDialog();

  @override
  State<_BackShootingGuideDialog> createState() => _BackShootingGuideDialogState();
}

class _BackShootingGuideDialogState extends State<_BackShootingGuideDialog> {
  final PageController _pageController = PageController();
  int currentPageIndex = 0;
  int totalPageIndex = 2;

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final bottomPadding = MediaQuery.paddingOf(context).bottom;
    return Container(
      height: 704 + bottomPadding,
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.only(
          topLeft: Radius.circular(20),
          topRight: Radius.circular(20),
        ),
      ),
      padding: EdgeInsets.only(bottom: bottomPadding, left: 20, right: 20),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          const Gap(10),
          Container(
            width: 100,
            height: 4,
            decoration: BoxDecoration(
              color: Colors.black.withOpacity(0.05),
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const Gap(52),
          Expanded(
            child: PageView(
              controller: _pageController,
              onPageChanged: (index) => setState(() => currentPageIndex = index),
              children: [
                _firstPage(),
                _secondPage(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _firstPage() {
    return Column(
      children: [
        const Text(
          'Check your surroundings\nbefore scanning !',
          style: TextStyle(
            fontSize: 26,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
        ),
        const Gap(30),
        Image.asset(RasterAsset.dialogPhoto1, width: 170, height: 170),
        const Gap(30),
        const Text(
          "Please check if you are\ntoo close to the camera!",
          style: TextStyle(
            fontSize: 19,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
          textAlign: TextAlign.center,
        ),
        const Gap(30),
        SizedBox(
          width: 310,
          height: 90,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Image.asset(RasterAsset.electricBulb, width: 24, height: 24),
                  const Gap(16),
                  const Expanded(
                    child: Text(
                      'Make sure you\'re in a well-lit area.',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: AppFontWeight.regular,
                        color: AppColor.textBlack,
                      ),
                    ),
                  ),
                ],
              ),
              Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Image.asset(RasterAsset.eyeGlass, width: 24, height: 24),
                  const Gap(16),
                  const Expanded(
                    child: Text(
                      'Remove your glasses and adjust any hair covering your eyes.',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: AppFontWeight.regular,
                        color: AppColor.textBlack,
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
        const Spacer(),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 8,
              height: 8,
              decoration: const BoxDecoration(
                color: Color(0xff027AFA),
                shape: BoxShape.circle,
              ),
            ),
            const Gap(9),
            Container(
              width: 8,
              height: 8,
              decoration: const BoxDecoration(
                color: Color(0xffDBE8FE),
                shape: BoxShape.circle,
              ),
            ),
          ],
        ),
        const Gap(17),
        SecondaryButton(
          onTap: () => _pageController.nextPage(duration: const Duration(milliseconds: 300), curve: Curves.easeInOut),
          text: 'Next',
        ),
        const Gap(36),
      ],
    );
  }

  Widget _secondPage() {
    return Column(
      children: [
        const Text(
          'Check your left and right\nalignments of the eyes!',
          style: TextStyle(
            fontSize: 26,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
          textAlign: TextAlign.center,
        ),
        const Gap(30),
        Image.asset(RasterAsset.dialogPhoto2, width: 170, height: 170),
        const Gap(30),
        RichText(
          text: const TextSpan(
            children: [
              TextSpan(
                text: "Identify the left and right eye\nfrom the subject`s perspective\n",
                style: TextStyle(
                  fontSize: 19,
                  fontWeight: AppFontWeight.semiBold,
                  color: AppColor.textBlack,
                  height: 1.4,
                ),
              ),
              TextSpan(
                text: "not the photographer's.",
                style: TextStyle(
                  fontSize: 19,
                  fontWeight: AppFontWeight.semiBold,
                  color: AppColor.textPrimary,
                  height: 1.4,
                ),
              ),
            ],
          ),
          textAlign: TextAlign.center,
        ),
        const Spacer(),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 8,
              height: 8,
              decoration: const BoxDecoration(
                color: Color(0xffDBE8FE),
                shape: BoxShape.circle,
              ),
            ),
            const Gap(9),
            Container(
              width: 8,
              height: 8,
              decoration: const BoxDecoration(
                color: Color(0xff027AFA),
                shape: BoxShape.circle,
              ),
            ),
          ],
        ),
        const Gap(17),
        GestureDetector(
          onTap: () => context.pop(),
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
                Image.asset(RasterAsset.photoCamera, width: 30, height: 30),
                const SizedBox(width: 8),
                const Text(
                  'Start Eye Scan',
                  style: TextStyle(
                    color: AppColor.button1Text,
                    fontSize: 17,
                    fontWeight: AppFontWeight.semiBold,
                  ),
                ),
              ],
            ),
          ),
        ),
        const Gap(36),
      ],
    );
  }
}
