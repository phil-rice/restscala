package one.xingyi.lensdsl.client

import one.xingyi.core.UtilsSpec
import one.xingyi.core.json.JsonParser
import one.xingyi.core.optics.Lens

class AbstractLensStoreTest[J](implicit parser: JsonParser[J]) extends UtilsSpec {

  behavior of getClass.getSimpleName

  val jsonString =
    """{
      |"a":1,
      |"b":true,
      |"c":2.0,
      |"d": "value"
      |"e":[1,2,3,4]
      |}""".stripMargin
  val j = parser(jsonString)

  val lens =
    """aLens=a/integer
      |bLens=b/boolean
      |cLens=c/double
      |dLens=d/string
      |eLens=e/*integer""".stripMargin

  val lensStore: ILensStore[J] = ILensStore[J](lens)

  def checkLens[T](lens: Lens[J, T], expected: T, newT: T): Unit = {
    val j2 = lens.set(j, newT)
    lens(j) shouldBe expected
    lens(j2) shouldBe newT

  }

  it should "have a primitive lens" in {
    checkLens(lensStore.integerLens("aLens"), 1, 2)
    checkLens(lensStore.booleanLens("bLens"), true, false)
    checkLens(lensStore.doubleLens("cLens"), 2.0, 3.0)
    checkLens(lensStore.stringLens("dLens"), "value", "newValue")
  }


}
