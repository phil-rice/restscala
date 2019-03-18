package one.xingyi.core.json


import one.xingyi.core.functions.Functions
import one.xingyi.core.optics.Lens

import scala.reflect.ClassTag

class JsonParserWriterSpec[J: ClassTag](implicit parserWriter: JsonParserWriter[J]) extends JsonParserAndWriterSpec[J] {

  val jsonString = """{"name":"someName","age":23,""" + """"addresses":[{"line1":"someLine1a","line2":"someLine2a","postcode":"somePostcode1"},{"line1":"someLine1b","line2":"someLine2b","postcode":"somePostcode2"}],""" + """"telephone":{"number":"someNumber"}}"""
  val jsonStringWithLists = """{"listOfStrings":["one","two","three"],""" + """"listOfInts":[1,2,3],""" + """"listOfDoubles":[1.0,2.0,3.0],""" + """"listOfBooleans":[false, true, false]""" + """}"""

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

  it should "be able to chain child lens'" in {
    check(lensToChild("telephone") andThen lensToChild("number") andThen lensToString, "someNumber", "newNumber")
  }

  it should "have a list lens" in {
    val line1: Lens[J, String] = lensToChild("line1") andThen lensToString
    val json: J = parserWriter(jsonString)
    val lens: Lens[J, List[J]] = lensToChild("addresses") andThen lensToList[J]("address", Functions.identity, Functions.identity)
    val addresses: List[J] = lens.get(json)
    val firstChildJson: J = addresses(0)
    val secondChildJson: J = addresses(1)

    line1.get(firstChildJson) shouldBe "someLine1a"
    line1.get(secondChildJson) shouldBe "someLine1b"
  }

  it should "have an optional lens" in {

  }


}
