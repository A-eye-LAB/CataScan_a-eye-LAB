import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cata_scan_flutter/core/providers/flutter_secure_storage_provider.dart';

final loginCneterStorageProvider =
    Provider.autoDispose<LoginCenterStorage>((ref) {
  final secureStorage = ref.read(flutterSecureStorageProvider);
  return LoginCenterStorage(secureStorage);
});

class LoginCenterStorage {
  final FlutterSecureStorage _secureStorage;
  LoginCenterStorage(this._secureStorage);

  static const String _idKey = 'center_id';
  static const String _passwordKey = 'center_password';

  Future<bool> saveIdPassword(String id, String password) async {
    final results = await Future.wait([
      saveId(id),
      savePassword(password),
    ]);

    return results.isNotEmpty && !results.contains(false);
  }

  Future<(bool, String?, String?)> getIdPassword() async {
    final id = await getId();
    final password = await getPassword();

    if (id == null || password == null) {
      return (false, null, null);
    } else {
      return (true, id, password);
    }
  }

  Future<bool> saveId(String id) async {
    try {
      await _secureStorage.write(key: _idKey, value: id);
      return true;
    } catch (e) {
      return false;
    }
  }

  Future<bool> savePassword(String password) async {
    try {
      await _secureStorage.write(key: _passwordKey, value: password);
      return true;
    } catch (e) {
      return false;
    }
  }

  Future<bool> deleteIdPassword() async {
    try {
      final results = await Future.wait([
        deleteId(),
        deletePassword(),
      ]);

      return results.isNotEmpty && !results.contains(false);
    } catch (e) {
      return false;
    }
  }

  Future<String?> getId() async => await _secureStorage.read(key: _idKey);
  Future<String?> getPassword() async =>
      await _secureStorage.read(key: _passwordKey);

  Future<bool> deleteId() async {
    try {
      await _secureStorage.delete(key: _idKey);
      return true;
    } catch (e) {
      return false;
    }
  }

  Future<bool> deletePassword() async {
    try {
      await _secureStorage.delete(key: _passwordKey);
      return true;
    } catch (e) {
      return false;
    }
  }
}
