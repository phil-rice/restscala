package one.xingyi.core.simpleList

import one.xingyi.core.UtilsSpec
import one.xingyi.core.reflection.ClassTags

import scala.language.higherKinds
import scala.reflect.ClassTag

trait ISimpleListStringTest[L <: ISimpleList[String]] extends ISimpleListTest[L, String] {
  def zero = "zero"
  def one = "one"
  def onea = "onea"
  def two = "two"
  def three = "three"
  def four = "four"

}
abstract class ISimpleListTest[L <: ISimpleList[T] : ClassTag, T] extends UtilsSpec {

  behavior of ClassTags.nameOf[L] + " as ISimpleList[String]"

  def makeList(ss: T*): L
  def zero: T
  def one: T
  def onea: T
  def two: T
  def three: T
  def four: T

  it should "have an empty string" in {
    val empty = makeList()
    empty.size shouldBe 0
    empty.toList shouldBe List()
    empty.iterator.toList shouldBe List()
  }

  it should "have a getter" in {
    val list = makeList(zero, one, two)
    list(0) shouldBe zero
    list(1) shouldBe one
    list(2) shouldBe two
    list.size shouldBe 3
    list.toList shouldBe List(zero, one, two)
  }

  it should "be updatable" in {
    val value: L = makeList(zero, one, two)
    val list = value.withItem(1, onea)
    list.toList shouldBe List(zero, onea, two)
  }
  it should "be appendable" in {
    val value: L = makeList(zero, one, two)
    val list = value.append(three).append(four)
    list.toList shouldBe List(zero, one, two, three, four)
  }

}

class SimpleListSpec extends ISimpleListStringTest[SimpleList[String]] {
  override def makeList(ss: String*): SimpleList[String] = ISimpleList(ss: _*)
}
