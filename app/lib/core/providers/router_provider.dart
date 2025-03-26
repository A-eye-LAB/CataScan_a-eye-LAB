import 'package:cata_scan_flutter/models/eye_scan_data.dart';
import 'package:cata_scan_flutter/services/storage/user_info_storage.dart';
import 'package:cata_scan_flutter/views/common/analysis/analysis_loading_screen.dart';
import 'package:cata_scan_flutter/views/user/analysis/analysis_result_screen.dart';
import 'package:cata_scan_flutter/views/common/camera/preview_screen.dart';
import 'package:cata_scan_flutter/views/common/logout_dialog.dart';
import 'package:cata_scan_flutter/views/user/survey/survey_screen.dart';
import 'package:cata_scan_flutter/views/common/user_info/user_info_edit_screen.dart';
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cata_scan_flutter/views/common/survey/survay_landing_screen.dart';
import 'package:cata_scan_flutter/views/welcome_screen.dart';
import 'package:cata_scan_flutter/views/user/main/main_screen.dart';
import 'package:cata_scan_flutter/views/common/camera/camera_select_screen.dart';
import 'package:cata_scan_flutter/views/common/camera/shooting_result_screen.dart';
import 'package:cata_scan_flutter/views/common/camera/shooting_screen.dart';
import 'package:cata_scan_flutter/views/user/medical_info/terms_of_service_screen.dart';
import 'package:go_router/go_router.dart';

import 'package:cata_scan_flutter/views/center/login/center_info_screen.dart';
import 'package:cata_scan_flutter/views/center/login/center_login_screen.dart';
import 'package:cata_scan_flutter/views/center/login/center_internet_error_screen.dart';
import 'package:cata_scan_flutter/views/center/analysis/center_analysis_result_screen.dart';
import 'package:cata_scan_flutter/views/center/main/center_main_screen.dart';
import 'package:cata_scan_flutter/views/center/medical_info/center_terms_of_service_screen.dart';
import 'package:cata_scan_flutter/views/center/survey/center_survey_screen.dart';

final routerProvider = Provider<GoRouter>((ref) {
  return GoRouter(
    initialLocation: WelcomeScreen.routeName,
    redirect: (context, state) async {
      final path = state.uri.path;
      final (success, _) =
          await ref.read(userInfoStorageProvider).getUserInfo();

      if (success && path == WelcomeScreen.routeName) {
        return MainScreen.routeName;
      }
      return null;
    },
    routes: [
      GoRoute(
        path: WelcomeScreen.routeName,
        builder: (context, state) => const WelcomeScreen(),
      ),
      GoRoute(
        path: SurveyLandingScreen.routeName,
        builder: (context, state) => const SurveyLandingScreen(),
      ),
      GoRoute(
        path: MainScreen.routeName,
        builder: (context, state) => const MainScreen(),
      ),
      GoRoute(
        path: UserInfoEditScreen.routeName,
        builder: (context, state) => const UserInfoEditScreen(),
      ),
      GoRoute(
        path: SurveyScreen.routeName,
        builder: (context, state) => const SurveyScreen(),
      ),
      GoRoute(
        path: TermsOfServiceScreen.routeName,
        builder: (context, state) => const TermsOfServiceScreen(),
      ),
      GoRoute(
        path: CameraSelectScreen.routeName,
        builder: (context, state) => const CameraSelectScreen(),
      ),
      GoRoute(
        path: ShootingScreen.routeName,
        builder: (context, state) {
          final extra = state.extra as Map<String, dynamic>;
          return ShootingScreen(
            cameraLensDirection:
                extra['cameraLensDirection'] as CameraLensDirection,
          );
        },
      ),
      GoRoute(
        path: PreviewScreen.routeName,
        builder: (context, state) {
          final extra = state.extra as Map<String, dynamic>;
          return PreviewScreen(
            imagePath: extra['imagePath'] as String,
          );
        },
      ),
      GoRoute(
        path: ShootingResultScreen.routeName,
        builder: (context, state) {
          final extra = state.extra as Map<String, dynamic>;
          return ShootingResultScreen(
            leftEyePath: extra['leftEyePath'] as String,
            rightEyePath: extra['rightEyePath'] as String,
          );
        },
      ),
      GoRoute(
        path: AnalysisLoadingScreen.routeName,
        pageBuilder: (context, state) {
          final extra = state.extra as Map<String, dynamic>;
          return CustomTransitionPage(
            key: state.pageKey,
            child: AnalysisLoadingScreen(
              leftEyePath: extra['leftEyePath'] as String,
              rightEyePath: extra['rightEyePath'] as String,
            ),
            transitionsBuilder:
                (context, animation, secondaryAnimation, child) {
              return FadeTransition(
                opacity: animation,
                child: child,
              );
            },
          );
        },
      ),
      GoRoute(
        path: AnalysisResultScreen.routeName,
        pageBuilder: (context, state) {
          final extra = state.extra as Map<String, dynamic>;
          return CustomTransitionPage(
            key: state.pageKey,
            child: AnalysisResultScreen(
              leftEyePath: extra['leftEyePath'] as String,
              rightEyePath: extra['rightEyePath'] as String,
              leftEyeStatus: extra['leftEyeStatus'] as EyeStatus,
              rightEyeStatus: extra['rightEyeStatus'] as EyeStatus,
            ),
            transitionsBuilder:
                (context, animation, secondaryAnimation, child) {
              return FadeTransition(
                opacity: animation,
                child: child,
              );
            },
          );
        },
      ),
      GoRoute(
        path: LogoutDialog.routeName,
        pageBuilder: (context, state) {
          return CustomTransitionPage(
            key: state.pageKey,
            child: const LogoutDialog(),
            transitionsBuilder:
                (context, animation, secondaryAnimation, child) {
              return FadeTransition(
                opacity: animation,
                child: child,
              );
            },
          );
        },
      ),
      GoRoute(
        path: CenterLoginCenterScreen.routeName,
        builder: (context, state) => const CenterLoginCenterScreen(),
      ),
      GoRoute(
        path: CenterInfoScreen.routeName,
        builder: (context, state) => const CenterInfoScreen(),
      ),
      GoRoute(
        path: CenterInternetErrorScreen.routeName,
        builder: (context, state) => CenterInternetErrorScreen(),
      ),
      GoRoute(
        path: CenterMainScreen.routeName,
        builder: (context, state) => const CenterMainScreen(),
      ),
      GoRoute(
        path: CenterAnalysisResultScreen.routeName,
        pageBuilder: (context, state) {
          final extra = state.extra as Map<String, dynamic>;
          return CustomTransitionPage(
            key: state.pageKey,
            child: CenterAnalysisResultScreen(
              leftEyePath: extra['leftEyePath'] as String,
              rightEyePath: extra['rightEyePath'] as String,
              leftEyeStatus: extra['leftEyeStatus'] as EyeStatus,
              rightEyeStatus: extra['rightEyeStatus'] as EyeStatus,
            ),
            transitionsBuilder:
                (context, animation, secondaryAnimation, child) {
              return FadeTransition(
                opacity: animation,
                child: child,
              );
            },
          );
        },
      ),
      GoRoute(
        path: CenterTermsOfServiceScreen.routeName,
        builder: (context, state) => const CenterTermsOfServiceScreen(),
      ),
      GoRoute(
        path: CenterSurveyScreen.routeName,
        builder: (context, state) => const CenterSurveyScreen(),
      ),
    ],
  );
});
