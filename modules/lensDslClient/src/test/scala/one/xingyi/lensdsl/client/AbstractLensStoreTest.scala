package one.xingyi.lensdsl.client

import one.xingyi.core.UtilsSpec
import one.xingyi.core.crypto.Codec
import one.xingyi.core.json.{JsonInt, JsonParserWriter}
import one.xingyi.core.optics.Lens
import one.xingyi.core.script.ViewNamesToViewLens

class AbstractLensStoreTest[J](implicit json: JsonParserWriter[J]) extends UtilsSpec {

  behavior of getClass.getSimpleName

  val jsonString =
    """{
      |"a":1,
      |"b":true,
      |"c":2.0,
      |"d": "value"
      |"e":[0,1,2,3]
      |}""".stripMargin
  val j = json(jsonString)

  val lens =
    """aLens=a,{integer}
      |bLens=b,{boolean}
      |cLens=c,{double}
      |dLens=d,{string}
      |eList=e,*
      |e0Lens=e,*,#0,{integer}
      |e1Lens=e,*,#1,{integer}
      |elastLens=e,*,#last,{integer}
      |fLens=f,!F""".stripMargin


  implicit val viewNamesToViewLens = new ViewNamesToViewLens(Map("F" -> Lens[J, MirroredObjectForTest[J]](MirroredObjectForTest.apply, (m, f) => f.mirror)))
  val lensStore: ILensStore[J] = ILensStore[J](lens)


  def checkLens[T](lens: Lens[J, T], expected: T, newT: T) = {
    lens(j) shouldBe expected
    val j2 = lens.set(j, newT)
    lens(j) shouldBe expected
    lens(j2) shouldBe newT
    j2
  }

  it should "have primitive lens" in {
    checkLens(lensStore.integerLens("aLens"), 1, 2)
    checkLens(lensStore.booleanLens("bLens"), true, false)
    checkLens(lensStore.doubleLens("cLens"), 2.0, 3.0)
    checkLens(lensStore.stringLens("dLens"), "value", "newValue")
  }


  implicit val intCodec: Codec[Int, J] = new Codec[Int, J] {
    override def forwards: Int => J = { i: Int => json.toJ(JsonInt(i)) }
    override def backwards: J => Int = json.extractInt
  }
  def checkListLens(lens: Lens[J, Int], expected: Int, newValue: Int, changedList: List[Int]): Unit = {
    val j2 = checkLens(lens, expected, newValue)
    lensStore.listLens("eList").get(j) shouldBe List(0, 1, 2, 3)
    lensStore.listLens("eList").get(j2) shouldBe changedList
  }

  it should "have list lens" in {
    checkListLens(lensStore.integerLens("e0Lens"), 0, 99, List(99, 1, 2, 3))
    checkListLens(lensStore.integerLens("e1Lens"), 1, 99, List(0, 99, 2, 3))
    checkListLens(lensStore.integerLens("elastLens"), 3, 99, List(0, 1, 2, 99))
  }


}
