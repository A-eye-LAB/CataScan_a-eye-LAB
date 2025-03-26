import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/services/patient_info/patient_info_provider.dart';
import 'package:cata_scan_flutter/views/common/button.dart';
import 'package:cata_scan_flutter/views/center/medical_info/center_terms_of_service_screen.dart';
import 'package:cata_scan_flutter/views/common/survey/button/skip_text_button.dart';

import 'package:gap/gap.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

class CenterComment extends ConsumerStatefulWidget {
  const CenterComment({
    super.key,
    required this.onPrevious,
  });
  final Function() onPrevious;

  @override
  ConsumerState<CenterComment> createState() => _CenterCommentState();
}

class _CenterCommentState extends ConsumerState<CenterComment> {
  final TextEditingController _controller = TextEditingController();
  final FocusNode _focusNode = FocusNode();
  bool _hasText = false;

  @override
  void initState() {
    super.initState();
    _controller.addListener(() {
      setState(() {
        _hasText = _controller.text.isNotEmpty;
      });
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.translucent,
      onTap: () {
        _focusNode.unfocus();
      },
      child: Column(
        children: [
          const Text(
            'Leave comment',
            style: TextStyle(
              fontSize: 28,
              fontWeight: AppFontWeight.semiBold,
              color: AppColor.textBlack,
            ),
          ),
          const Gap(16),
          Focus(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: TextField(
                controller: _controller,
                focusNode: _focusNode,
                maxLines: 7,
                style: const TextStyle(
                  decoration: TextDecoration.none,
                ),
                decoration: InputDecoration(
                  hintText: 'Leave a comment about the patient...',
                  hintStyle: const TextStyle(color: AppColor.textHint),
                  filled: true,
                  fillColor: Colors.white,
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(20),
                    borderSide:
                        const BorderSide(color: Color(0xFFC7C7C7), width: 2),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(20),
                    borderSide:
                        const BorderSide(color: Color(0xFF027AFA), width: 2),
                  ),
                  contentPadding: const EdgeInsets.all(16),
                ),
                cursorColor: Colors.blue,
                showCursor: true,
              ),
            ),
          ),
          const Gap(12),
          SkipTextButton(onPressed: () async {
            await ref.read(patientProvider.notifier).updateComment('');
            if (context.mounted) {
              context.go(CenterTermsOfServiceScreen.routeName);
            }
          }),
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
                      if (_hasText == false) {
                        return;
                      }

                      //TODO: 환자세부 내용도 저장필요 comment

                      if (context.mounted) {
                        context.pushReplacement(
                            CenterTermsOfServiceScreen.routeName);
                      }
                    },
                    text: 'Confirm',
                    isDisabled: _hasText == false,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
