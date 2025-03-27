import 'package:cata_scan_flutter/core/providers/shared_preferences_provider.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:cata_scan_flutter/models/user_info.dart';
import 'package:cata_scan_flutter/models/gender.dart';

final userInfoStorageProvider = Provider.autoDispose<UserInfoStorage>((ref) {
  final prefs = ref.read(sharedPreferencesProvider);
  return UserInfoStorage(prefs);
});

class UserInfoStorage {
  final SharedPreferences _prefs;
  static const String _nameKey = 'user_name';
  static const String _genderKey = 'user_gender';
  static const String _ageRangeKey = 'user_age_range';
  static const String _medicalConditionsKey = 'user_medical_conditions';

  UserInfoStorage(this._prefs);

  Future<(bool, UserInfo?)> getUserInfo() async {
    final name = _prefs.getString(_nameKey);
    final gender = _prefs.getString(_genderKey);
    final ageRange = _prefs.getString(_ageRangeKey);
    // final medicalConditions = _prefs.getStringList(_medicalConditionsKey);

    if (name == null || gender == null || ageRange == null) {
      return (false, null);
    }

    return (
      true,
      UserInfo(
        name: name,
        gender: Gender.values.firstWhere((e) => e.text == gender),
        ageRange: AgeGroup.values.firstWhere((e) => e.text == ageRange),
        medicalConditions: [],
      )
    );
  }

  Future<bool> saveUserInfo(UserInfo userInfo) async {
    final results = await Future.wait([
      _prefs.setString(_nameKey, userInfo.name),
      _prefs.setString(_genderKey, userInfo.gender.text),
      _prefs.setString(_ageRangeKey, userInfo.ageRange.text),
      _prefs.setStringList(_medicalConditionsKey,           userInfo.medicalConditions.map((e) => e.text).toList())
    ]);

    return results.isNotEmpty && !results.contains(false);
  }

  Future<bool> clearUserInfo() async {
    final results = await Future.wait([
      _prefs.remove(_nameKey),
      _prefs.remove(_genderKey),
      _prefs.remove(_ageRangeKey),
      _prefs.remove(_medicalConditionsKey),
    ]);

    return !results.contains(false);
  }

  Future<bool> saveName(String name) async {
    return await _prefs.setString(_nameKey, name);
  }

  Future<bool> saveGender(Gender gender) async {
    return await _prefs.setString(_genderKey, gender.text);
  }

  Future<bool> saveAgeRange(AgeGroup ageRange) async {
    return await _prefs.setString(_ageRangeKey, ageRange.text);
  }

  Future<bool> saveMedicalConditions(List<MedicalCondition> conditions) async {
    return await _prefs.setStringList(_medicalConditionsKey, conditions.map((e) => e.text).toList());
  }

  String? getName() => _prefs.getString(_nameKey);
  Gender? getGender() => Gender.values.firstWhere((e) => e.text == _prefs.getString(_genderKey));
  AgeGroup? getAgeRange() => AgeGroup.values.firstWhere((e) => e.text == _prefs.getString(_ageRangeKey));
  List<String>? getMedicalConditions() => _prefs.getStringList(_medicalConditionsKey);
}
