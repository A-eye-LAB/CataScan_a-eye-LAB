import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cata_scan_flutter/models/user_info.dart';
import 'package:cata_scan_flutter/services/storage/user_info_storage.dart';
import 'package:cata_scan_flutter/models/gender.dart';

class UserInfoNotifier extends AutoDisposeAsyncNotifier<(bool, UserInfo?)> {
  @override
  Future<(bool, UserInfo?)> build() async {
    return _storage.getUserInfo();
  }

  UserInfoStorage get _storage => ref.read(userInfoStorageProvider);

  Future<void> updateAgeRange(AgeGroup ageRange) async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      await _storage.saveAgeRange(ageRange);
      return _storage.getUserInfo();
    });
  }

  Future<void> updateGender(Gender gender) async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      await _storage.saveGender(gender);
      return _storage.getUserInfo();
    });
  }

  Future<void> updateName(String name) async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      await _storage.saveName(name);
      return _storage.getUserInfo();
    });
  }
}

final userInfoProvider = AsyncNotifierProvider.autoDispose<UserInfoNotifier, (bool, UserInfo?)>(() {
  return UserInfoNotifier();
});
