# 빌드 방법

이 문서에서는 CataScan 모바일 앱의 빌드 및 실행 방법에 대해 설명합니다.

[처음으로](../overview.md) | 
[소개로](00_introduction.md) | 
[이전: 소개](00_introduction.md) | 
[다음: 앱 구조](02_app_architecture.md) 

## 사전 요구사항

- Git
- Flutter SDK
- FVM(Flutter Version Management)
- Android Studio (Android 빌드용)
- Xcode (iOS 빌드용, macOS 필요)
- Cocoapods (iOS 빌드용)
- JDK 17 이상
- Gradle

## Git 저장소 클론
```bash
git clone https://github.com/A-eye-LAB/cata_scan_flutter.git
```

## FVM 설치

```bash
# macOS 또는 Linux
brew tap leoafarias/fvm
brew install fvm

# Windows
dart pub global activate fvm
```

### 프로젝트 설정

1. 프로젝트 디렉토리로 이동

```bash
cd cata_scan_flutter
```

2. 프로젝트에 정의된 flutter 버전 설치

```bash
fvm install
```

3. 의존성 패키지 설치
```bash
fvm flutter pub get
```

## Android 빌드

### 개발용 APK 빌드

1. 프로젝트 최상위 디렉토리에서 다음 명령 실행:

```bash
fvm flutter build apk --debug
```

2. 빌드된 APK 파일 위치:
   - `build/app/outputs/flutter-apk/app-debug.apk`

### 릴리스용 AAB 빌드

1. 앱 버전 업데이트: 
   - `pubspec.yaml` 파일에서 `version` 항목 수정

2. AAB 빌드:

```bash
fvm flutter build appbundle --release
```

3. 빌드된 AAB 파일 위치:
   - `build/app/outputs/bundle/release/app-release.aab`

### 서명 파일 설정

릴리스 빌드는 서명 파일이 필요합니다. 다음 단계를 따라 설정하세요:

1. 키스토어 생성 (처음 한 번만):

```bash
keytool -genkey -v -keystore ~/upload-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

2. `[project]/android/key.properties` 파일 생성:

```
storePassword=<비밀번호>
keyPassword=<비밀번호>
keyAlias=upload
storeFile=<키스토어 경로, 예: /Users/사용자명/upload-keystore.jks>
```

3. `android/app/build.gradle` 파일 수정 (이미 설정되어 있다면 건너뛰기):

```gradle
def keystoreProperties = new Properties()
def keystorePropertiesFile = rootProject.file('key.properties')
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}

android {
    // ...

    signingConfigs {
        release {
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile keystoreProperties['storeFile'] ? file(keystoreProperties['storeFile']) : null
            storePassword keystoreProperties['storePassword']
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

4. 서명된 앱 빌드:

```bash
fvm flutter build apk --release
# 또는
fvm flutter build appbundle --release
```

## iOS 빌드

> **주의**: iOS 빌드는 macOS 환경에서만 가능합니다.

### 개발용 빌드

1. 의존성 설치:

```bash
cd ios
pod install
cd ..
```

2. 개발용 빌드:

```bash
fvm flutter build ios --debug
```

3. Xcode로 실행 (시뮬레이터 또는 실제 기기):

```bash
open ios/Runner.xcworkspace
```

4. Xcode에서 실행할 기기 선택 후 Run 버튼 클릭

### 릴리스용 빌드

1. 앱 버전 업데이트: 
   - `pubspec.yaml` 파일에서 `version` 항목 수정

2. 릴리스 빌드 준비:

```bash
fvm flutter build ios --release
```

3. Xcode에서 아카이브 생성:
   - `open ios/Runner.xcworkspace`로 Xcode 실행
   - `Product > Archive` 메뉴 선택
   - Archives 창에서 Distribute App 선택

### Provisioning Profile 설정

1. Apple Developer 계정에 로그인

2. 인증서 및 프로필 설정:
   - Certificates, Identifiers & Profiles 섹션에서 적절한 App ID 생성
   - 필요한 인증서 생성 (개발 및 배포용)
   - Provisioning Profile 생성 (개발 및 배포용)

3. Xcode에서 설정:
   - Xcode > Preferences > Accounts에서 Apple ID 로그인
   - 프로젝트 설정 > Signing & Capabilities에서 팀 선택
   - Automatically manage signing 옵션 체크 또는 해제 (상황에 맞게)
