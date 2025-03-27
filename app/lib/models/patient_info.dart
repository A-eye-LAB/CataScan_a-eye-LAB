import 'package:cata_scan_flutter/models/eye_scan_data.dart';
import 'package:cata_scan_flutter/models/gender.dart';

class PatientInfo {
  final String? name;
  final Gender? gender;
  final String? comment;
  final EyeScanData eyeScanData;

  const PatientInfo({
    this.name,
    this.gender,
    this.comment,
    required this.eyeScanData,
  });

  PatientInfo copyWith({
    String? name,
    Gender? gender,
    String? comment,
    EyeScanData? eyeScanData,
  }) {
    return PatientInfo(
      name: name ?? this.name,
      gender: gender ?? this.gender,
      comment: comment ?? this.comment,
      eyeScanData: eyeScanData ?? this.eyeScanData,
    );
  }

  factory PatientInfo.initial() {
    final emptyEyeScanDta = EyeScanData(
      id: '',
      rightEyeScanPath: '',
      leftEyeScanPath: '',
      rightStatus: EyeStatus.normal,
      leftStatus: EyeStatus.normal,
      timestamp: DateTime(0),
    );
    return PatientInfo(
        name: null, gender: null, comment: null, eyeScanData: emptyEyeScanDta);
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'gender': gender?.text,
      'comment': comment,
      'eyeScanData': eyeScanData.toMap(),
    };
  }

  factory PatientInfo.fromJson(Map<String, dynamic> json) {
    return PatientInfo(
        name: json['name'] as String?,
        gender: json['gender'] != null
            ? Gender.values.firstWhere(
                (e) => e.text == json['gender'] as String,
                orElse: () => Gender.others // 기본값 설정
                )
            : null,
        comment: json['comment'] as String?,
        eyeScanData: json['eyeScanData'] =
            EyeScanData.fromMap(json['eyeScanData'] as Map<String, dynamic>));
  }
}
