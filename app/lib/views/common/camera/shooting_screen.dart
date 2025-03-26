import 'dart:io';

import 'package:cata_scan_flutter/core/styles/app_asset.dart';
import 'package:cata_scan_flutter/core/styles/app_color.dart';
import 'package:cata_scan_flutter/core/styles/app_font_weight.dart';
import 'package:cata_scan_flutter/services/image/image_cropper_service.dart';
import 'package:cata_scan_flutter/views/common/camera/eye_guide_painter.dart';
import 'package:cata_scan_flutter/views/common/camera/preview_screen.dart';
import 'package:cata_scan_flutter/views/common/camera/shooting_guide_dialog.dart';
import 'package:cata_scan_flutter/views/common/camera/shooting_result_screen.dart';
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:go_router/go_router.dart';
import 'package:lottie/lottie.dart';

class ShootingScreen extends StatefulWidget {
  const ShootingScreen({super.key, required this.cameraLensDirection});
  static const String routeName = '/shooting';
  final CameraLensDirection cameraLensDirection;

  @override
  State<ShootingScreen> createState() => _ShootingScreenState();
}

class _ShootingScreenState extends State<ShootingScreen> {
  late CameraController _controller;
  Future<void>? _initializeControllerFuture;
  List<CameraDescription> cameras = [];
  CameraDescription? currentCamera;
  final ImageCropperService _imageCropper = ImageCropperService();
  String? _leftEyeImagePath;
  String? _rightEyeImagePath;
  double? guideAreaWidth;
  double? guideAreaHeight;
  bool _isLeftButtonSelected = true;
  static const double _topBarHeight = 50;
  static const double _bottomBarHeight = 244;

  @override
  void initState() {
    super.initState();
    _initializeCamera(widget.cameraLensDirection);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  Future<void> _initializeCamera(CameraLensDirection targetCamera) async {
    try {


      cameras = await availableCameras();
      if (cameras.isEmpty) {
        throw Exception('No cameras available');
      }

      for (var camera in cameras) {
        debugPrint('Camera: ${camera.name}, Direction: ${camera.lensDirection}, Sensor: ${camera.sensorOrientation}');
      }

      currentCamera = cameras.firstWhere(
        (camera) => camera.lensDirection == targetCamera,
        orElse: () => cameras.first,
      );

      debugPrint('Selected camera: ${currentCamera?.name}, Direction: ${currentCamera?.lensDirection}');

      _controller = CameraController(
        currentCamera!,
        ResolutionPreset.high,
        enableAudio: false,
      );
      if (mounted) {
        showModalBottomSheet(
          context: context,
          builder: (context) => ShootingGuideDialog(cameraLensDirection: currentCamera!.lensDirection),
          useSafeArea: false,
          isScrollControlled: true,
        );
      }

      _initializeControllerFuture = _controller.initialize();
      setState(() {});
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Camera initialization failed: $e')),
        );
      }
    }
  }

  bool get _isReversed => currentCamera?.lensDirection == CameraLensDirection.back;

  String get _leftButtonText => _isReversed ? 'Right' : 'Left';
  String get _rightButtonText => _isReversed ? 'Left' : 'Right';
  bool get _canProceedToNext => _leftEyeImagePath != null && _rightEyeImagePath != null;

  void _handleImageCapture(String path) {
    setState(() {
      final isLeftEye = _isLeftButtonSelected != _isReversed;
      isLeftEye ? _leftEyeImagePath = path : _rightEyeImagePath = path;
    });
  }

  Widget _buildSelectionButton({
    required bool isLeft,
    required String text,
    required bool isSelected,
  }) {
    return Positioned(
      left: isLeft ? 0 : null,
      right: isLeft ? null : 0,
      child: Stack(
        children: [
          GestureDetector(
            behavior: HitTestBehavior.opaque,
            onTap: () => setState(() => _isLeftButtonSelected = isLeft),
            child: SizedBox(
              width: 100,
              height: 38,
              child: Center(
                child: Text(
                  text,
                  style: const TextStyle(
                    color: AppColor.button5Text,
                    fontSize: 15,
                    fontWeight: AppFontWeight.regular,
                  ),
                ),
              ),
            ),
          ),
          if (isSelected)
            Container(
              width: 100,
              height: 38,
              decoration: BoxDecoration(
                color: AppColor.button5Active,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Center(
                child: Text(
                  text,
                  style: const TextStyle(
                    color: AppColor.button5Text,
                    fontSize: 15,
                    fontWeight: AppFontWeight.semiBold,
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildEyeSelectionButton() {
    return Container(
      width: 200,
      height: 38,
      decoration: BoxDecoration(
        color: AppColor.button5Background,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Stack(
        children: [
          _buildSelectionButton(
            isLeft: true,
            text: _leftButtonText,
            isSelected: _isLeftButtonSelected,
          ),
          _buildSelectionButton(
            isLeft: false,
            text: _rightButtonText,
            isSelected: !_isLeftButtonSelected,
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColor.backgroundWhite,
      body: SafeArea(
        child: Stack(
          alignment: Alignment.center,
          children: [
            Positioned.fill(
              child: FutureBuilder(
                future: _initializeControllerFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.done) {
                    return Align(
                      alignment: Alignment.topCenter,
                      child: CameraPreview(_controller),
                    );
                  } else {
                    return Container(color: Colors.black);
                  }
                },
              ),
            ),
            Positioned(
              top: _topBarHeight,
              left: 0,
              right: 0,
              bottom: _bottomBarHeight,
              child: FutureBuilder(
                future: _initializeControllerFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.done) {
                    return const SizedBox.shrink();
                  } else {
                    return const Center(
                      child: CircularProgressIndicator(
                        color: AppColor.indicatorOn,
                      ),
                    );
                  }
                },
              ),
            ),
            Positioned(
              top: 0,
              left: 0,
              right: 0,
              child: Container(
                width: double.infinity,
                height: _topBarHeight,
                color: AppColor.backgroundWhite,
              ),
            ),
            if (currentCamera?.lensDirection == CameraLensDirection.front)
              Positioned(
                top: _topBarHeight,
                left: 0,
                right: 0,
                bottom: _bottomBarHeight,
                child: LayoutBuilder(
                  builder: (context, constraints) {
                    WidgetsBinding.instance.addPostFrameCallback((_) {
                      setState(() {
                        guideAreaWidth = constraints.maxWidth;
                        guideAreaHeight = constraints.maxHeight;
                      });
                    });
                    return CustomPaint(
                      painter: EyeGuidePainter(),
                    );
                  },
                ),
              ),
            if (currentCamera?.lensDirection == CameraLensDirection.front)
              Positioned(
                top: _topBarHeight,
                child: Column(
                  children: [
                    Lottie.asset(
                      LottieAsset.arrow,
                      width: 24,
                      height: 42,
                      fit: BoxFit.fitHeight,
                    ),
                    const Gap(5),
                    const Text(
                      'Look at the camera lens',
                      style: TextStyle(
                        fontSize: 14,
                        fontWeight: AppFontWeight.bold,
                        color: AppColor.textWhite,
                      ),
                    ),
                  ],
                ),
              ),
            if (currentCamera?.lensDirection == CameraLensDirection.back)
              Positioned(
                top: _topBarHeight,
                left: 0,
                right: 0,
                bottom: _bottomBarHeight,
                child: LayoutBuilder(
                  builder: (context, constraints) {
                    WidgetsBinding.instance.addPostFrameCallback((_) {
                      setState(() {
                        guideAreaWidth = constraints.maxWidth;
                        guideAreaHeight = constraints.maxHeight;
                      });
                    });
                    return Center(
                      child: Image.asset(
                        width: 242,
                        height: 150,
                        RasterAsset.eyeGuide,
                      ),
                    );
                  },
                ),
              ),
            Positioned(
              bottom: 0,
              left: 0,
              right: 0,
              child: _buildBottomBar(),
            ),
            Positioned(
              left: 0,
              top: 0,
              child: IconButton(
                icon: const Icon(
                  Icons.arrow_back_ios_rounded,
                  color: AppColor.textBlack,
                  size: 24,
                ),
                onPressed: () => context.pop(),
              ),
            ),
            Positioned(
              right: 8,
              top: 0,
              child: TextButton(
                onPressed: _handleNextButtonTap,
                style: TextButton.styleFrom(
                  foregroundColor: _canProceedToNext ? AppColor.textBlack : AppColor.textGray,
                ),
                child: const Text(
                  'Next',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: AppFontWeight.semiBold,
                  ),
                ),
              ),
            ),
            Positioned(
              bottom: _bottomBarHeight + 12,
              child: _buildEyeSelectionButton(),
            ),
          ],
        ),
      ),
    );
  }

  void _showRequiredImagesDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Images Required'),
        content: const Text('Please take photos of both your left and right eyes before proceeding.'),
        actions: [
          TextButton(
            onPressed: () => context.pop(),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  void _handleNextButtonTap() {
    // TODO: 디자인팀에 임의로 만들었다고 말하기
    if (!_canProceedToNext) {
      _showRequiredImagesDialog();
      return;
    }

    context.pushReplacement(
      ShootingResultScreen.routeName,
      extra: {
        'leftEyePath': _leftEyeImagePath,
        'rightEyePath': _rightEyeImagePath,
      },
    );
  }

  Widget _buildBottomBar() {
    return Container(
      color: AppColor.backgroundWhite,
      height: 244,
      child: Column(
        children: [
          const Gap(52),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              _previewImage(_isLeftButtonSelected),
              const Gap(34),
              GestureDetector(
                onTap: () async {
                  if (_initializeControllerFuture == null || !mounted || _controller.value.isInitialized == false) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('Please wait while the camera is initializing...'),
                        duration: Duration(seconds: 2),
                      ),
                    );
                    return;
                  }
                  try {
                    await _initializeControllerFuture;
                    final image = await _controller.takePicture();

                    final croppedPath = await _imageCropper.cropImage(
                      imagePath: image.path,
                      pictureHeight: guideAreaHeight ?? 0,
                      pictureWidth: guideAreaWidth ?? 0,
                      topPadding: _topBarHeight,
                      bottomPadding: _bottomBarHeight,
                    );

                    if (!mounted) return;

                    final (result, path) = await context.push(PreviewScreen.routeName, extra: {
                      'imagePath': croppedPath,
                    }) as (bool, String?);

                    if (result != false && mounted && path != null) {
                      _handleImageCapture(path);
                    }
                  } catch (e) {
                    if (mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Failed to capture: $e')),
                      );
                    }
                  }
                },
                child: Container(
                  width: 100,
                  height: 100,
                  decoration: const BoxDecoration(
                    color: AppColor.button1Background,
                    shape: BoxShape.circle,
                  ),
                  child: Center(
                    child: Container(
                      width: 80,
                      height: 80,
                      decoration: const BoxDecoration(
                        color: AppColor.button1Text,
                        shape: BoxShape.circle,
                      ),
                    ),
                  ),
                ),
              ),
              const Gap(34),
              const SizedBox(
                width: 56,
                height: 56,
              ),
            ],
          ),
          const Gap(52),
          GestureDetector(
            onTap: () => showModalBottomSheet(
              context: context,
              builder: (context) => ShootingGuideDialog(cameraLensDirection: currentCamera!.lensDirection),
              useSafeArea: false,
              isScrollControlled: true,
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                SizedBox(
                  width: 20,
                  height: 20,
                  child: Image.asset(
                    width: 20,
                    height: 20,
                    RasterAsset.scanGuide,
                  ),
                ),
                const Gap(4),
                Text(
                  'Scan Guide',
                  style: TextStyle(
                    fontSize: 12,
                    color: AppColor.textCaption,
                    fontWeight: AppFontWeight.semiBold,
                  ),
                )
              ],
            ),
          )
        ],
      ),
    );
  }

  Widget _previewImage(bool isLeftEye) {
    final imagePath = _isReversed ? (isLeftEye ? _rightEyeImagePath : _leftEyeImagePath) : (isLeftEye ? _leftEyeImagePath : _rightEyeImagePath);
    final previewImageText = _isReversed ? (_isLeftButtonSelected ? 'R' : 'L') : (_isLeftButtonSelected ? 'L' : 'R');

    if (imagePath == null) {
      return const SizedBox(width: 64, height: 64);
    }

    return Stack(
      clipBehavior: Clip.none,
      children: [
        SizedBox(
          width: 64,
          height: 64,
          child: ClipRRect(
            borderRadius: BorderRadius.circular(8),
            child: Image.file(
              File(imagePath),
              fit: BoxFit.cover,
            ),
          ),
        ),
        Positioned(
          right: -6,
          top: -6,
          child: Container(
            width: 18,
            height: 18,
            decoration: const BoxDecoration(
              color: AppColor.button1Background,
              shape: BoxShape.circle,
            ),
            child: Center(
              child: Text(
                previewImageText,
                style: const TextStyle(
                  color: AppColor.button1Text,
                  fontSize: 10,
                  fontWeight: AppFontWeight.semiBold,
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
