import 'package:cata_scan_flutter/core/constants/model_constants.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:onnxruntime/onnxruntime.dart';

final cataractDiagnosisModelSessionProvider = FutureProvider.autoDispose<OrtSession>((ref) async {
  OrtEnv.instance.init();
  final sessionOptions = OrtSessionOptions();
  final rawAssetFile = await rootBundle.load(CataractDiagnosisModelConfig.modelUrl);
  final modelBytes = rawAssetFile.buffer.asUint8List();
  return OrtSession.fromBuffer(modelBytes, sessionOptions);
});
