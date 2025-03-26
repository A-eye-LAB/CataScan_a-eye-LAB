import 'dart:convert';

class EyeScanData {
  final String id;
  final String rightEyeScanPath;
  final String leftEyeScanPath;
  final EyeStatus rightStatus;
  final EyeStatus leftStatus;
  final DateTime timestamp;
  EyeScanData({
    required this.id,
    required this.rightEyeScanPath,
    required this.leftEyeScanPath,
    required this.rightStatus,
    required this.leftStatus,
    required this.timestamp,
  });

  EyeScanData copyWith({
    String? id,
    String? rightEyeScanPath,
    String? leftEyeScanPath,
    EyeStatus? rightStatus,
    EyeStatus? leftStatus,
    DateTime? timestamp,
  }) {
    return EyeScanData(
      id: id ?? this.id,
      rightEyeScanPath: rightEyeScanPath ?? this.rightEyeScanPath,
      leftEyeScanPath: leftEyeScanPath ?? this.leftEyeScanPath,
      rightStatus: rightStatus ?? this.rightStatus,
      leftStatus: leftStatus ?? this.leftStatus,
      timestamp: timestamp ?? this.timestamp,
    );
  }

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'id': id,
      'rightEyeScanPath': rightEyeScanPath,
      'leftEyeScanPath': leftEyeScanPath,
      'rightStatus': rightStatus.value,
      'leftStatus': leftStatus.value,
      'date': timestamp.millisecondsSinceEpoch,
    };
  }

  factory EyeScanData.fromMap(Map<String, dynamic> map) {
    return EyeScanData(
      id: map['id'] as String,
      rightEyeScanPath: map['rightEyeScanPath'] as String,
      leftEyeScanPath: map['leftEyeScanPath'] as String,
      rightStatus: EyeStatus.values.firstWhere(
        (status) => status.value == map['rightStatus'],
        orElse: () => EyeStatus.normal,
      ),
      leftStatus: EyeStatus.values.firstWhere(
        (status) => status.value == map['leftStatus'],
        orElse: () => EyeStatus.normal,
      ),
      timestamp: DateTime.fromMillisecondsSinceEpoch(map['date'] as int),
    );
  }

  String toJson() => json.encode(toMap());

  factory EyeScanData.fromJson(String source) => EyeScanData.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() {
    return 'EyeScanData(id: $id, rightEyeScanPath: $rightEyeScanPath, leftEyeScanPath: $leftEyeScanPath, rightStatus: $rightStatus, leftStatus: $leftStatus, timestamp: $timestamp)';
  }

  @override
  bool operator ==(covariant EyeScanData other) {
    if (identical(this, other)) return true;
    if (id != other.id) return false;
    return other.rightEyeScanPath == rightEyeScanPath && other.leftEyeScanPath == leftEyeScanPath && other.rightStatus == rightStatus && other.leftStatus == leftStatus && other.timestamp == timestamp;
  }

  @override
  int get hashCode {
    return rightEyeScanPath.hashCode ^ leftEyeScanPath.hashCode ^ rightStatus.hashCode ^ leftStatus.hashCode ^ timestamp.hashCode;
  }
}

enum EyeStatus {
  normal('Normal'),
  abnormal('Abnormal');

  const EyeStatus(this.value);
  final String value;
}
