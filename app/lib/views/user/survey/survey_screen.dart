import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/models/user_info.dart';
import 'package:cata_scan_flutter/views/user/survey/widgets/age_group_survey_container.dart';
import 'package:cata_scan_flutter/views/user/survey/widgets/gender_survey_container.dart';
import 'package:cata_scan_flutter/views/user/survey/widgets/name_survey_container.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';

class SurveyScreen extends StatefulWidget {
  const SurveyScreen({super.key});
  static const String routeName = '/survey';

  @override
  State<SurveyScreen> createState() => _SurveyScreenState();
}

class _SurveyScreenState extends State<SurveyScreen> {
  final PageController _pageController = PageController();
  final TextEditingController _nameController = TextEditingController();
  int currentPageIndex = 0;
  int totalPageIndex = 3;

  AgeGroup? selectedAgeGroup;

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    _pageController.dispose();
    _nameController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColor.backgroundWhite,
      resizeToAvoidBottomInset: true,
      body: SafeArea(
        child: Column(
          children: [
            const Gap(39),
            buildProgressBar(currentPageIndex, totalPageIndex),
            const Gap(47),
            Expanded(
              child: PageView(
                controller: _pageController,
                onPageChanged: _onPageChanged,
                physics: const NeverScrollableScrollPhysics(),
                children: [
                  NameSurveyContainer(
                    nameController: _nameController,
                    onConfirm: _goToNextPage,
                  ),
                  GenderSurveyContainer(
                    onPrevious: _goToPreviousPage,
                    onConfirm: _goToNextPage,
                  ),
                  AgeGroupSurveyContainer(
                    onPrevious: _goToPreviousPage,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _onPageChanged(int pageIndex) {
    setState(() {
      currentPageIndex = pageIndex;
    });
  }

  void _goToNextPage() {
    if (currentPageIndex < totalPageIndex - 1) {
      _pageController.nextPage(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    }
  }

  void _goToPreviousPage() {
    if (currentPageIndex > 0) {
      _pageController.previousPage(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    }
  }

  Widget buildAgeGruopDefaultButton(AgeGroup ageGroup) {
    return Container(
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
    );
  }
}

Padding buildProgressBar(int currentPage, int totalPage) {
  return Padding(
    padding: const EdgeInsets.symmetric(horizontal: 20),
    child: Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(
        totalPage,
        (index) => Expanded(
          child: Container(
            decoration: BoxDecoration(
              color: index <= currentPage
                  ? AppColor.indicatorOn
                  : AppColor.indicatorOff,
              borderRadius: BorderRadius.circular(2),
            ),
            margin: const EdgeInsets.symmetric(horizontal: 5),
            height: 4,
          ),
        ),
      ),
    ),
  );
}
