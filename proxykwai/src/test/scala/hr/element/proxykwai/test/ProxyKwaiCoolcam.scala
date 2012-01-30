package hr.element.proxykwai
package test

import org.scalatest._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ProxyKwaiCoolcam extends FeatureSpec {

  object UglyPowers {
    def powerAsInt   (x: Int, y: Int) = math.pow(x, y).toInt
    def powerAsString(x: Int, y: Int) = x +" ^ "+ y
  }

  val prettyPowerCompiler =
    new ProxyKwai(
      "hr.element.proxykwai.test.PrettyPowers",
      "$times$times",
      Array[Class[_]](classOf[Int], classOf[Int]))

    .addObjectProxy(UglyPowers.getClass, "powerAsInt",    classOf[Int])
    .addObjectProxy(UglyPowers.getClass, "powerAsString", classOf[String])

  feature("Proxykwai is pretty") {

    scenario("Multiple arguments") {
      prettyPowerCompiler.export("target/test-classes")

/*
      info(**(2,8): Int)     // 390625
      info(**(5,8): String)  // 5 ^ 8
*/
    }
  }
}
