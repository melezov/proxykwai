package hr.element.proxykwai;

class ProxyCall {
  final String sourceClass;
  final String sourceMethod;
  final Class<?> returnType;

  ProxyCall(
      final String sourceClass,
      final String sourceMethod,
      final Class<?> returnType) {
    this.sourceClass = sourceClass;
    this.sourceMethod = sourceMethod;
    this.returnType = returnType;
  }
}
