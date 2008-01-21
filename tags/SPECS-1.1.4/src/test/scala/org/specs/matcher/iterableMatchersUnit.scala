package org.specs.matcher
import org.specs._
import org.specs.runner._

class iterableMatchersTest extends JUnit3(iterableMatchersUnit)
object iterableMatchersUnit extends MatchersSpecification {
  "A 'contain' matcher" should {
    "be ok if an iterable contains an element" in {
      List(1, 2, 3) must contain(1)
    }
    "be ko if an iterable doesn't contain an element" in {
      assertion(List(1, 2, 3) must contain(4)) must failWith("'List(1, 2, 3)' doesn't contain '4'")
    }
  }
  "An 'exist' matcher" should {
    "be ok if there is one element in the iterable verifying a passed function" in {
      List(1, 2, 3) must exist[Int](_ > 2)
    }
    "be ko if there is no element in the iterable verifying a passed function" in {
      assertion(List(1, 2, 3) must exist[Int](_ < 0)) must failWith("no element verifies the property in 'List(1, 2, 3)'")
    }
  }
  "An 'containMatch' matcher" should {
    "be ok if there is one string in an iterable[String] matching a given pattern" in {
      List("aaa", "bbb", "ccc") must containMatch("b+")
    }
    "be ko if there is no string in the iterable matching a given pattern" in {
      assertion(List("aaa", "bbb", "ccc") must containMatch("z+")) must failWith("no element matches 'z+' in 'List(aaa, bbb, ccc)'")
    }
  }
}