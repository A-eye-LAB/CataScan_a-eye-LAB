class LoginState {
  final String? id;
  final bool isAuthenticated;

  const LoginState({this.id, this.isAuthenticated = false});

  LoginState copyWith({String? id, bool? isAuthenticated}) {
    return LoginState(
      id: id ?? this.id,
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
    );
  }
}
