package one.xingyi.script
import one.xingyi.core.UtilsSpec
import one.xingyi.core.json.{JsonParser, JsonWriter, ToJson}
import one.xingyi.core.optics.Lens
import one.xingyi.core.script._
import one.xingyi.core.serverMediaType.{CodeDetails, DomainDetails, LensLanguage}
import one.xingyi.javascript.client.JavascriptXingYiLoader
import one.xingyi.javascript.server.Javascript
import one.xingyi.test.client._
import one.xongyi.javascript.server.JavascriptScriptFixture
import org.json4s.JValue

import scala.io.Source

abstract class LensLanguageClientAndServerTest[J: JsonParser, SL <: LensLanguage](preferedLanguage: ClientPreferedLanguage)(implicit jsonWriter: JsonWriter[J]) extends ScriptFixture[SL] with UtilsSpec {
  behavior of "LensLanguage between client and server"

  val parent = ParentForTest("someName", 12, HouseForTest(5, "somePostCode"), List(ChildForTest("child1", 6), ChildForTest("child2", 7)))

  implicit def loader: IXingYiLoader

  it should "be setup properly" in {
    preferedLanguage.s shouldBe defaultLensLanguage.name
  }

  def setup(fn: (IXingYi) => Unit): Unit = {
    val (lensLanguage, domainDetails) = domainList.accept(None, defaultLensLanguage)
    lensLanguage shouldBe defaultLensLanguage
    val CodeDetails(code) = domainDetails.code(lensLanguage)
    implicit val xingyi = implicitly[IXingYiLoader].apply(code)
    fn(xingyi)
  }
  it should "allow the person's name to be extracted and changed - testing string lens" in {
    setup { implicit xingyi =>
      val json = jsonWriter(ParentForTest.parentProjection.toJson(parent))
      val namesOps = new ParentNameOps
      val person = xingyi.parse[Parent](json)


      val nameLens = namesOps.nameLens
      val person2 = nameLens.set(person, "New Name")
      val person3 = nameLens.set(person2, "Newer Name")

      nameLens.get(person) shouldBe "someName"
      nameLens.get(person2) shouldBe "New Name"
      nameLens.get(person3) shouldBe "Newer Name"
    }
  }

  it should "allow the person's house to be extracted and changed - testing object lens" in {
    setup { implicit xingyi =>
      val json = jsonWriter(ParentForTest.parentProjection.toJson(parent))
      val person = xingyi.parse[Parent](json)
      val housePostcodeLens = (new ParentHouseOps).houseLens andThen (new HouseOps).postCodeLens

      val person2 = housePostcodeLens.set(person, "NewPostCode")
      val person3 = housePostcodeLens.set(person2, "NewerPostCode")

      housePostcodeLens.get(person) shouldBe "somePostCode"
      housePostcodeLens.get(person2) shouldBe "NewPostCode"
      housePostcodeLens.get(person3) shouldBe "NewerPostCode"
    }
  }

  it should "allow the person's address to be extracted and changed - testing list lens" in {
    setup { implicit xingyi =>
      val json = jsonWriter(ParentForTest.parentProjection.toJson(parent))
      val person = xingyi.parse[Parent](json)
      val childrenLens: Lens[Parent, List[Child]] = (new ParentChildrenOps).childrenLens
      val firstItemLens = Lens.firstItemL[Child]
      val childNameLens = (new ChildOps).nameLens
      val nameLens = childrenLens andThen firstItemLens andThen childNameLens


      val person2 = nameLens.set(person, "newChildName")
      val person3 = nameLens.set(person2, "newerChildName")

      nameLens.get(person) shouldBe "child1"
      nameLens.get(person2) shouldBe "newChildName"
      nameLens.get(person3) shouldBe "newerChildName"
    }
  }


  //  it should "allow lens to be processed on the client" in {
  //    val (lensLanguage, domainDetails) = domainList.accept(None, defaultLensLanguage)
  //    lensLanguage shouldBe defaultLensLanguage
  //    val CodeDetails(code) = domainDetails.code(lensLanguage)
  //
  //
  //  }
}

import one.xingyi.json4s.Json4sParser._
import one.xingyi.json4s.Json4sWriter._


class JavascriptLensLanguageClientAndServerTest extends LensLanguageClientAndServerTest[JValue, Javascript](ClientPreferedLanguage("javascript")) with JavascriptScriptFixture {
  override def loader: IXingYiLoader = new JavascriptXingYiLoader
}
//class LensDSlLanguageClientAndServerTest extends LensLanguageClientAndServerTest[JValue, LensDsl](ClientPreferedLanguage("lensdsl")) with LensDslScriptFixture {
//  override def loader: IXingYiLoader = ???
//}