import 'dart:async';
import 'dart:convert';

import 'package:cata_scan_flutter/models/eye_scan_data.dart';
import 'package:cata_scan_flutter/core/providers/shared_preferences_provider.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

final analysisDataStorageProvider = AsyncNotifierProvider.autoDispose<AnalysisDataStorageProvider, List<EyeScanData>>(AnalysisDataStorageProvider.new);

class AnalysisDataStorageProvider extends AutoDisposeAsyncNotifier<List<EyeScanData>> {
  late final SharedPreferences _prefs;
  static const String _analysisDataKey = 'analysis_data';

  AnalysisDataStorageProvider();

  @override
  FutureOr<List<EyeScanData>> build() async {
    _prefs = ref.read(sharedPreferencesProvider);
    final eyeScanDataListJson = _prefs.getString(_analysisDataKey);
    if (eyeScanDataListJson == null) {
      return [];
    }
    return (jsonDecode(eyeScanDataListJson) as List<dynamic>).map<EyeScanData>((dynamic e) => EyeScanData.fromJson(e)).toList();
  }

  Future<void> addEyeScanData(EyeScanData eyeScanData) async {
    final eyeScanDataList = state.valueOrNull ?? [];
    final newEyeScanDataList = [...eyeScanDataList, eyeScanData];
    final encodedData = jsonEncode(newEyeScanDataList.map((e) => e.toJson()).toList());
    await _prefs.setString(_analysisDataKey, encodedData);
    state = AsyncData(newEyeScanDataList);
  }

  Future<void> removeEyeScanData(String id) async {
    final eyeScanDataList = state.valueOrNull ?? [];
    final newEyeScanDataList = eyeScanDataList.where((eyeScanData) => eyeScanData.id != id).toList();
    final encodedData = jsonEncode(newEyeScanDataList.map((e) => e.toJson()).toList());
    await _prefs.setString(_analysisDataKey, encodedData);
    state = AsyncData(newEyeScanDataList);
  }

  Future<void> clearEyeScanData() async {
    await _prefs.remove(_analysisDataKey);
    state = const AsyncData([]);
  }
}
