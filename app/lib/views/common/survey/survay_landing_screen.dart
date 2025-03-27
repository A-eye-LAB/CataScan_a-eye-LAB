import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/user/survey/survey_screen.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class SurveyLandingScreen extends StatefulWidget {
  const SurveyLandingScreen({super.key});
  static const String routeName = '/survey-landing';

  @override
  State<SurveyLandingScreen> createState() => SurveyLandingScreenState();
}

class SurveyLandingScreenState extends State<SurveyLandingScreen> {
  @override
  Widget build(BuildContext context) {
    precacheImage(const AssetImage(RasterAsset.surveyLoading), context);
    return Scaffold(
      backgroundColor: AppColor.backgroundWhite,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // const Spacer(flex: 153),
              // SizedBox(
              //   height: 121,
              //   width: double.infinity,
              //   child: Center(
              //     child: Container(
              //       height: 78,
              //       width: 78,
              //       decoration: const BoxDecoration(
              //         color: AppColor.backgroundBlack,
              //       ),
              //     ),
              //   ),
              // ),
              const Spacer(flex: 110),
              Image.asset(
                RasterAsset.surveyLoading,
                width: 360,
                fit: BoxFit.fitWidth,
              ),
              // const Spacer(flex: 8),
              const Text(
                'Here are\n3 questions\nfor you.',
                style: TextStyle(
                  fontSize: 34,
                  fontWeight: AppFontWeight.semiBold,
                  color: AppColor.textBlack,
                ),
                textAlign: TextAlign.center,
              ),
              const Spacer(flex: 31),
              const Text(
                'Please enter your basic information',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: AppFontWeight.regular,
                  color: AppColor.textBlack,
                ),
              ),
              const Spacer(flex: 207),
              PrimaryButton(
                text: 'Start Survey',
                isDisabled: false,
                onTap: () {
                  context.go(SurveyScreen.routeName);
                },
              ),
              const Spacer(flex: 30),
            ],
          ),
        ),
      ),
    );
  }
}
