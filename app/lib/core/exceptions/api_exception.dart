class ApiException implements Exception {
  final String message;
  final int statusCode;

  ApiException(this.message, this.statusCode);

  factory ApiException.fromStatusCode(int code) {
    switch (code) {
      case 400:
        return ApiException('Bad request', code);
      case 401:
        return ApiException('Unauthorized', code);
      case 403:
        return ApiException('Forbidden', code);
      case 404:
        return ApiException('Not found', code);
      default:
        return ApiException('Server error', code);
    }
  }
}
