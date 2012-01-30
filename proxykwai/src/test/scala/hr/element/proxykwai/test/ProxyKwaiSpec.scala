package hr.element.proxykwai
package test

import org.scalatest._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ProxyKwaiFeatureSpec extends FeatureSpec {

  object TestParserPrimitives {
    def parseBoolean(s: String) = s.toBoolean
    def parseByte(s: String)    = s.toByte
    def parseShort(s: String)   = s.toShort
    def parseChar(s: String)    = s.head
    def parseInt(s: String)     = s.toInt
    def parseFloat(s: String)   = s.toFloat
    def parseDouble(s: String)  = s.toDouble
    def parseLong(s: String)    = s.toLong
    def parseUnit(s: String)    = ()
  }

  object TestParserJavaBoxes {
    def parseBoolean(s: String)   = java.lang.Boolean.valueOf(s)
    def parseByte(s: String)      = java.lang.Byte.valueOf(s)
    def parseShort(s: String)     = java.lang.Short.valueOf(s)
    def parseCharacter(s: String) = java.lang.Character.valueOf(s.head)
    def parseInteger(s: String)   = java.lang.Integer.valueOf(s)
    def parseFloat(s: String)     = java.lang.Float.valueOf(s)
    def parseDouble(s: String)    = java.lang.Double.valueOf(s)
    def parseLong(s: String)      = java.lang.Long.valueOf(s)
    def parseVoid(s: String)      = null: java.lang.Void
  }

  object TestParserClasses {
    def parseString(s: String)     = s
    def parseCharArray(s: String)  = s.toCharArray
    def parseIntArray(s: String)   = s.toCharArray.map(_.getNumericValue)
    def parseBigInt(s: String)     = BigInt(s)
    def parseRegex(s: String)      = s.r
    def parseOption(s: String)     = Option(s)
    def parseStringList(s: String) = s.split(",").toList
  }

  val testParserProxyCompiler =
    new ProxyKwai(
      "hr.element.proxykwai.test.TestParserProxy",
      "parse",
      Array[Class[_]](classOf[String]))

    .addObjectProxy(TestParserPrimitives.getClass, "parseBoolean", classOf[Boolean])
    .addObjectProxy(TestParserPrimitives.getClass, "parseByte",    classOf[Byte])
    .addObjectProxy(TestParserPrimitives.getClass, "parseShort",   classOf[Short])
    .addObjectProxy(TestParserPrimitives.getClass, "parseChar",    classOf[Char])
    .addObjectProxy(TestParserPrimitives.getClass, "parseInt",     classOf[Int])
    .addObjectProxy(TestParserPrimitives.getClass, "parseLong",    classOf[Long])
    .addObjectProxy(TestParserPrimitives.getClass, "parseFloat",   classOf[Float])
    .addObjectProxy(TestParserPrimitives.getClass, "parseDouble",  classOf[Double])
    .addObjectProxy(TestParserPrimitives.getClass, "parseUnit",    classOf[Unit])

    .addObjectProxy(TestParserJavaBoxes.getClass, "parseBoolean",   classOf[java.lang.Boolean])
    .addObjectProxy(TestParserJavaBoxes.getClass, "parseByte",      classOf[java.lang.Byte])
    .addObjectProxy(TestParserJavaBoxes.getClass, "parseShort",     classOf[java.lang.Short])
    .addObjectProxy(TestParserJavaBoxes.getClass, "parseCharacter", classOf[java.lang.Character])
    .addObjectProxy(TestParserJavaBoxes.getClass, "parseInteger",   classOf[java.lang.Integer])
    .addObjectProxy(TestParserJavaBoxes.getClass, "parseLong",      classOf[java.lang.Long])
    .addObjectProxy(TestParserJavaBoxes.getClass, "parseFloat",     classOf[java.lang.Float])
    .addObjectProxy(TestParserJavaBoxes.getClass, "parseDouble",    classOf[java.lang.Double])
    .addObjectProxy(TestParserJavaBoxes.getClass, "parseVoid",      classOf[java.lang.Void])

    .addObjectProxy(TestParserClasses.getClass, "parseString",    classOf[String])
    .addObjectProxy(TestParserClasses.getClass, "parseCharArray", classOf[Array[Char]])
    .addObjectProxy(TestParserClasses.getClass, "parseIntArray",  classOf[Array[Int]])
    .addObjectProxy(TestParserClasses.getClass, "parseBigInt",    classOf[BigInt])
    .addObjectProxy(TestParserClasses.getClass, "parseRegex",     classOf[scala.util.matching.Regex])
    .addObjectProxy(TestParserClasses.getClass, "parseOption",    classOf[Option[String]])

  feature("Overloading via return types on scala singletons") {

    scenario("Multiple singleton objects") {
      testParserProxyCompiler.export("target/test-classes")

/*
      info(TestParserProxy.parse("true"): Boolean)
      info(TestParserProxy.parse("123456"): Array[Char])
      info(TestParserProxy.parse("true"): Array[Int])
*/
    }
  }
}
