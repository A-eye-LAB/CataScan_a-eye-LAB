import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:cata_scan_flutter/models/gender.dart';

class UserInfo {
  final String name;
  final Gender gender;
  final AgeGroup ageRange;
  final List<MedicalCondition> medicalConditions;
  UserInfo({
    required this.name,
    required this.gender,
    required this.ageRange,
    required this.medicalConditions,
  });

  UserInfo copyWith({
    String? name,
    Gender? gender,
    AgeGroup? ageRange,
    List<MedicalCondition>? medicalConditions,
  }) {
    return UserInfo(
      name: name ?? this.name,
      gender: gender ?? this.gender,
      ageRange: ageRange ?? this.ageRange,
      medicalConditions: medicalConditions ?? this.medicalConditions,
    );
  }

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'name': name,
      'gender': gender.text,
      'ageRange': ageRange.text,
      'medicalConditions': medicalConditions.map((e) => e.text).toList(),
    };
  }

  factory UserInfo.fromMap(Map<String, dynamic> map) {
    return UserInfo(
      name: map['name'] as String,
      gender: Gender.values.firstWhere((e) => e.text == map['gender']),
      ageRange: AgeGroup.values.firstWhere((e) => e.text == map['ageRange']),
      medicalConditions:
          map['medicalConditions'] != null ? (map['medicalConditions'] as List<dynamic>).map((e) => MedicalCondition.values.firstWhere((condition) => condition.text == e)).toList() : [],
    );
  }

  String toJson() => json.encode(toMap());

  factory UserInfo.fromJson(String source) => UserInfo.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() {
    return 'UserInfoModel(name: $name, gender: $gender, ageRange: $ageRange, medicalConditions: $medicalConditions)';
  }

  @override
  bool operator ==(covariant UserInfo other) {
    if (identical(this, other)) return true;

    return other.name == name && other.gender == gender && other.ageRange == ageRange && listEquals(other.medicalConditions, medicalConditions);
  }

  @override
  int get hashCode {
    return name.hashCode ^ gender.hashCode ^ ageRange.hashCode ^ medicalConditions.hashCode;
  }
}

enum AgeGroup {
  under10('0-9'),
  under20('10-19'),
  under30('20-29'),
  under40('30-39'),
  under50('40-49'),
  over50('Over 50');

  const AgeGroup(this.text);
  final String text;
}

enum MedicalCondition {
  cataract('Cataract'),
  diabetes('Diabetes'),
  hypertension('Hypertension'),
  none('I don\'t know');

  const MedicalCondition(this.text);
  final String text;
}
