import 'dart:convert';
import 'package:http/http.dart' as http;

import 'package:cata_scan_flutter/core/exceptions/api_exception.dart';
import 'package:cata_scan_flutter/core/exceptions/auth_exception.dart';

class ApiClient {
  ApiClient();
  static const _baseUrl = 'http://ec2-3-35-207-200.ap-northeast-2.compute.amazonaws.com:8080';

  String? _token;

  Future<String?> login(String username, String password) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/auth/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': username,
          'password': password,
        }),
      );

      if (response.statusCode == 200) {
        final token = jsonDecode(response.body)['token'];
        _token = token;
        return token;
      } else {
        throw ApiException.fromStatusCode(response.statusCode);
      }
    } catch (e) {
      throw AuthException('Login failed: ${e is ApiException ? e.message : "Unknown error"}');
    }
  }

  Future<Map<String, dynamic>?> getUserInfo() async {
    try {
      if (_token == null) {
        throw AuthException('Not authenticated');
      }

      final response = await http.get(
        Uri.parse('$_baseUrl/user'),
        headers: {
          'Authorization': 'Bearer $_token',
        },
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw ApiException.fromStatusCode(response.statusCode);
      }
    } catch (e) {
      throw AuthException('get user info failed: ${e is ApiException ? e.message : "Unknown error"}');
    }
  }

  Future<Map<String, dynamic>?> uploadEyeImage({
    required String leftFile,
    required String rightFile,
    required String imageId,
    required String leftResult,
    required String rightResult,
  }) async {
    try {
      //throw AuthException('실패테스트');
      if (_token == null) {
        throw AuthException('token null');
      }

      var request = http.MultipartRequest(
        'POST',
        Uri.parse('$_baseUrl/reports'),
      );

      request.headers['Authorization'] = 'Bearer $_token';

      request.files.add(await http.MultipartFile.fromPath(
        'leftImage',
        leftFile,
      ));

      request.files.add(await http.MultipartFile.fromPath(
        'rightImage',
        rightFile,
      ));

      request.fields['imageId'] = imageId;

      if (leftResult.contains('abnormal')) {
        leftResult = 'requiresAttention';
      } else if (leftResult.contains('normal')) {
        leftResult = 'lowRisk';
      }

      if (rightResult.contains('abnormal')) {
        rightResult = 'requiresAttention';
      } else if (rightResult.contains('normal')) {
        rightResult = 'lowRisk';
      }
      request.fields['leftAiResult'] = leftResult;
      request.fields['rightAiResult'] = rightResult;

      final response = await request.send();
      final responseStr = await response.stream.bytesToString();

      if (response.statusCode == 200) {
        return jsonDecode(responseStr);
      } else {
        throw ApiException.fromStatusCode(response.statusCode);
      }
    } catch (e) {
      throw AuthException('upload data failed: Check Internet');

      //TODO: 에러코드에따른 에러메시지 표출 필요
      // throw AuthException(
      //     'upload data failed: ${e is ApiException ? e.message : "Unknown error"}');
    }
  }
}
