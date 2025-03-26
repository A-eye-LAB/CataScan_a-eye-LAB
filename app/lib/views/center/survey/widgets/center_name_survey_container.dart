import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/center/main/center_main_screen.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'package:cata_scan_flutter/services/patient_info/patient_info_provider.dart';

class CenterNameSurveyContainer extends ConsumerStatefulWidget {
  const CenterNameSurveyContainer({
    super.key,
    required this.nameController,
    required this.onConfirm,
  });
  final TextEditingController nameController;
  final Function() onConfirm;

  @override
  ConsumerState<CenterNameSurveyContainer> createState() =>
      _CenterNameSurveyContainerState();
}

class _CenterNameSurveyContainerState
    extends ConsumerState<CenterNameSurveyContainer> {
  final FocusNode _focusNode = FocusNode();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _focusNode.requestFocus();
    });
  }

  @override
  void dispose() {
    _focusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final isNameValid = widget.nameController.text.isNotEmpty;
    return Column(
      children: [
        const Text(
          'Enter Patient\'s Name',
          style: TextStyle(
            fontSize: 28,
            fontWeight: AppFontWeight.semiBold,
            color: AppColor.textBlack,
          ),
        ),
        const Gap(26),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          width: double.infinity,
          height: 56,
          child: TextField(
            controller: widget.nameController,
            focusNode: _focusNode,
            onChanged: (value) {
              setState(() {});
            },
            textAlign: TextAlign.center,
            enableSuggestions: false,
            cursorHeight: 42,
            cursorColor: AppColor.textBlack,
            cursorWidth: 2,
            style: const TextStyle(
              fontSize: 40,
              fontWeight: AppFontWeight.semiBold,
              color: AppColor.textPrimary,
            ),
            decoration: const InputDecoration(
              hintText: 'Name',
              hintStyle: TextStyle(
                fontSize: 40,
                fontWeight: AppFontWeight.semiBold,
                color: AppColor.textHint,
              ),
              border: InputBorder.none,
              contentPadding: EdgeInsets.zero,
            ),
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
                    context.go(CenterMainScreen.routeName);
                  },
                  text: 'Previous',
                ),
              ),
              const Gap(10),
              Flexible(
                child: PrimaryButton(
                  onTap: () async {
                    if (isNameValid) {
                      await ref
                          .read(patientProvider.notifier)
                          .updateName(widget.nameController.text);
                      widget.onConfirm();
                    }
                  },
                  text: 'Confirm',
                  isDisabled: !isNameValid,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
