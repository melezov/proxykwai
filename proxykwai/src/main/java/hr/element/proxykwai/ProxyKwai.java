package hr.element.proxykwai;

import java.io.*;
import java.util.*;

import java.lang.reflect.Method;

import jasmin.ClassFile;

public class ProxyKwai {
  private final String targetClass;
  private final String targetMethod;
  private final Class<?>[] arguments;

  public ProxyKwai(
      final String targetClass,
      final String targetMethod,
      final Class<?>[] arguments) {
    this(
      targetClass,
      targetMethod,
      arguments.clone(),
      new ProxyCall[0]
    );
  }

  final ProxyCall[] calls;

  private ProxyKwai(
      final String targetClass,
      final String targetMethod,
      final Class<?>[] arguments,
      final ProxyCall[] calls) {
    this.targetClass  = targetClass;
    this.targetMethod = targetMethod;
    this.arguments = arguments;
    this.calls = calls;
  }

  public ProxyKwai addObjectProxy(
      final String sourceClass,
      final String sourceMethod,
      final Class<?> returnType) {
    final ProxyCall call =
      new ProxyCall(
        sourceClass,
        sourceMethod,
        returnType
      );

    final ProxyCall[] newCalls =
      Arrays.copyOf(calls, calls.length + 1);
    newCalls[calls.length] = call;

    return new ProxyKwai(
      targetClass,
      targetMethod,
      arguments,
      newCalls
    );
  }

  public ProxyKwai addObjectProxy(
      final Class<?> sourceClass,
      final String sourceMethod,
      final Class<?> returnType) {
    return addObjectProxy(
      sourceClass.getName(),
      sourceMethod,
      returnType
    );
  }

  public ProxyKwai addObjectProxy(
      final Method sourceMethod) {
    return addObjectProxy(
      sourceMethod.getDeclaringClass(),
      sourceMethod.getName(),
      sourceMethod.getReturnType()
    );
  }

  static String slashify(final String name) {
    return name.replace('.', '/');
  }

  private String toJasmin() {
    final String argStr =
      getClassSymbolList(arguments);

    final Formatter formatter =
      new Formatter()
      .format(".class public %s\n", targetClass)
      .format(".super java/lang/Object\n");

    for (final ProxyCall call: calls) {
      formatter
        .format(
          "\n.method public static %s(%s)%s\n",
          targetMethod,
          argStr,
          getClassSymbol(call.returnType)
        ).format(
          "    .limit locals %d\n",
          arguments.length
        ).format(
          "    .limit stack %d\n\n",
          arguments.length + 1
        ).format(
          "    getstatic %1$s/MODULE$ L%1$s;\n",
          slashify(call.sourceClass)
        ).format(
          "%s",
          getArgumentLoader(arguments)
        ).format(
          "    invokevirtual %s/%s(%s)%s\n",
          slashify(call.sourceClass),
          call.sourceMethod,
          argStr,
          getClassSymbol(call.returnType)
        ).format(
          "    %sreturn\n",
          getReturnType(call.returnType)
        ).format(
            ".end method\n"
          );
    }

    System.out.println(formatter);
    return formatter.toString();
  }

  private static String getArgumentLoader(final Class<?>[] arguments) {
    final StringBuilder sB = new StringBuilder();

    for (int index = 0; index < arguments.length; index ++) {
      final String quickDirtyHack =
        getReturnType(arguments[index]);

      // quick and dirty hack will not work for indexes > 3
      // and god knows how many other cases...

      sB.append("    ")
        .append(quickDirtyHack)
        .append("load_")
        .append(index)
        .append('\n');
    }

    return sB.toString();
  }

  public void export(final String classPath) {
    final String body = toJasmin();

    try {
      final byte[] bA = assemble(body);

      final File outFile =
        new File(
          String.format(
            "%s/%s.class",
            classPath,
            slashify(targetClass)
          )
        );

      outFile.getParentFile().mkdirs();

      final FileOutputStream fOS = new FileOutputStream(outFile);
      fOS.write(bA);
      fOS.close();
    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private static byte[] assemble(final String body) throws Exception {
    final ClassFile classFile = new ClassFile();
    final BufferedReader bR =
      new BufferedReader(new StringReader(body));

    classFile.readJasmin(bR, "ProxyKwai.j", false);
    bR.close();

    final ByteArrayOutputStream bAOS =
      new ByteArrayOutputStream();

    classFile.write(bAOS);
    bAOS.close();

    return bAOS.toByteArray();
  }

  private static String getClassSymbolList(final Class<?>[] clazzes) {
    final StringBuilder sB = new StringBuilder();

    for (final Class<?> clazz : clazzes) {
      sB.append(getClassSymbol(clazz));
    }

    return sB.toString();
  }

  private static String getClassSymbol(final Class<?> clazz) {
    final String name = clazz.getName().toString();
    final char uc0 = Character.toUpperCase(name.charAt(0));

    if (clazz.isPrimitive()) {
      return String.valueOf(
        ('B' == uc0 && 'o' == name.charAt(1)) ? 'Z' :
        ('L' == uc0) ? 'J' :
        uc0
      );
    }

    final String slashName =
      slashify(name);

    return '[' == uc0
      ? slashName
      : String.format("L%s;", slashName);
  }

  private static String getReturnType(final Class<?> clazz) {
    final String classSymbol = getClassSymbol(clazz);
    if (classSymbol.length() > 1) {
      return "a";
    }

    final char lc0 = Character.toLowerCase(classSymbol.charAt(0));
    switch (lc0) {
      case 'b':
      case 'c':
      case 's':
      case 'z':
        return "i";

      case 'j':
        return "l";

      case 'v':
        return "";

      default:
        return String.valueOf(lc0);
    }
  }
}
