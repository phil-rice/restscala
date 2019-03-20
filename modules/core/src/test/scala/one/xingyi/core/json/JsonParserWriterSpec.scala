package one.xingyi.core.json


import one.xingyi.core.functions.Functions
import one.xingyi.core.optics.{Lens, SimpleLens}

import scala.reflect.ClassTag

class JsonParserWriterSpec[J: ClassTag](implicit parserWriter: JsonParserWriter[J]) extends JsonParserAndWriterSpec[J] {

  val jsonString = """{"name":"someName","age":23,""" + """"addresses":[{"line1":"someLine1a","line2":"someLine2a","postcode":"somePostcode1"},{"line1":"someLine1b","line2":"someLine2b","postcode":"somePostcode2"}],""" + """"telephone":{"number":"someNumber"}}"""
  val jsonStringWithLists = """{"bool": false,"listOfStrings":["one","two","three"],""" + """"listOfInts":[1,2,3],""" + """"listOfDoubles":[1.0,2.0,3.0],""" + """"listOfBooleans":[false, true, false]""" + """}"""

  def check[T](supplier: => Lens[J, T], expected: T, newValue: T): Unit = {
    val json: J = parserWriter(jsonString)
    val lens: Lens[J, T] = supplier
    lens.get(json) shouldBe expected
    val newJ: J = lens.set(json, newValue)
    lens.get(json) shouldBe expected
    lens.get(newJ) shouldBe newValue
  }

  behavior of "json lens for " + getClass.getSimpleName
  import parserWriter._

  it should "have a string lens" in {
    check(lensToChild("name") andThen lensToString, "someName", "newName")
  }
  it should "have an integer lens " in {
    check(lensToChild("age") andThen lensToInteger, 23, 999)
  }
  it should "have an boolean lens " in {
    check(lensToChild("bool") andThen lensToBoolean, false, true)
  }

  it should "be able to chain child lens'" in {
    check(lensToChild("telephone") andThen lensToChild("number") andThen lensToString, "someNumber", "newNumber")
  }

  it should "have a list lens" in {
    val json: J = parserWriter(jsonString)
    def line1(n: Int): Lens[J, String] = lensToChild("addresses") andThen lensToList andThen Lens.itemInListL(n) andThen lensToChild("line1") andThen lensToString

    line1(0).get(json) shouldBe "someLine1a"
    line1(1).get(json) shouldBe "someLine1b"
  }


}
