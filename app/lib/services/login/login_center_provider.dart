import 'dart:async';

import 'package:cata_scan_flutter/services/storage/login_center_storage.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cata_scan_flutter/services/api/api_client.dart';
import 'package:cata_scan_flutter/core/providers/api_client_provider.dart';
import 'package:cata_scan_flutter/models/login_state.dart';
import 'package:cata_scan_flutter/core/utils/pst_time_zone_converter.dart'
    as pst;

final loginCenterProvider =
    AsyncNotifierProvider<LoginCenterNotifier, LoginState>(() {
  return LoginCenterNotifier();
});

class LoginCenterNotifier extends AsyncNotifier<LoginState> {
  late final ApiClient _apiClient;
  late final LoginCenterStorage _storage;

  @override
  Future<LoginState> build() async {
    _apiClient = ref.watch(apiClientProvider);
    _storage = ref.watch(loginCneterStorageProvider);

    final id = await _storage.getId();
    return LoginState(id: id, isAuthenticated: false);
  }

  Future<bool> login(String id, String password) async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      pst.initTimeZone();
      await _apiClient.login(id, password);
      final bool result = await _storage.saveIdPassword(id, password);
      if (result == false) {
        throw Exception('login data save error');
      }
      return LoginState(id: id, isAuthenticated: true);
    });
    return state.hasError;
  }

  Future<bool> autoLogin() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      pst.initTimeZone();
      final (success, id, password) = await _storage.getIdPassword();
      if (!success || id == null || password == null) {
        throw Exception('Invalid credentials');
      }
      await _apiClient.login(id, password);
      return LoginState(id: id, isAuthenticated: true);
    });
    return state.hasError;
  }

  Future<bool> logout() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      final result = await _storage.deleteIdPassword();
      if (result == false) {
        throw Exception('logout fail');
      }

      return const LoginState(id: null, isAuthenticated: false);
    });
    return state.hasError;
  }
}
