/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.script
import java.io.File

import one.xingyi.core.UtilsSpec
import one.xingyi.core.codemaker._
import one.xingyi.core.crypto.Digestor
import one.xingyi.core.id.IdLens
import one.xingyi.core.json._
import one.xingyi.core.optics.Lens
import one.xingyi.core.serverMediaType._
import one.xingyi.core.strings.Files

import scala.language.higherKinds

trait IChild
case class ChildForTest(name: String, age: Int)
object ChildForTest {
  implicit val proof: ProofOfBinding[IChild, ChildForTest] = new ProofOfBinding
  object parentChildrenOps extends IChildOps[IXingYiLens, IChild] {
    //    override def houseLens = XingYiDomainObjectLens(Lens[ParentForTest, HouseForTest](_.house, (p, h) => p.copy(house = h)))
    //    override def childrenLens = XingYiDomainObjectLens(Lens[ParentForTest, List[ChildForTest]](_.children, (p, c) => p.copy(children = c)))
    override def nameLens = XingYiDomainStringLens(Lens[ChildForTest, String](_.name, (c, n) => c.copy(name = n)))
  }


  implicit val projection = ObjectProjection[IChild, ChildForTest](ChildForTest("someName", 0),
    "name" -> StringFieldProjection(parentChildrenOps.nameLens)
  )

}

trait IHouse
case class HouseForTest(houseNo: Int, postCode: String)


object HouseForTest {
  implicit val proofOfBinding: ProofOfBinding[IHouse, HouseForTest] = new ProofOfBinding

  implicit object houseOps extends IHouseOps[IXingYiLens, IHouse] {
    override def houseNoLens = XingYiDomainStringLens(Lens[HouseForTest, String](_.houseNo.toString, (h, p) => h.copy(houseNo = p.toInt)))
    override def postCodeLens = XingYiDomainStringLens(Lens[HouseForTest, String](_.postCode, (h, p) => h.copy(postCode = p)))
  }

  implicit val projection = ObjectProjection(HouseForTest(123, "somePostcode"),
    "houseNo" -> StringFieldProjection(houseOps.houseNoLens),
    "postcode" -> StringFieldProjection(houseOps.postCodeLens)
  )

}


trait IParent

@XingYiInterface(clazzes = Array(classOf[IParent]))
trait IParentNameOps[L[_, _], P <: IParent] extends IXingYiSharedOps[L, P] {
  def nameLens: L[P, String]
}
@XingYiInterface(clazzes = Array(classOf[IParent], classOf[IHouse]))
trait IParentHouseOps[L[_, _], P <: IParent, H <: IHouse] extends IXingYiSharedOps[L, P] {
  def houseLens: L[P, H]
}
@XingYiInterface(clazzes = Array(classOf[IParent], classOf[IChild]))
trait IParentChildrenOps[L[_, _], P <: IParent, C <: IChild] extends IXingYiSharedOps[L, P] {
  def childrenLens: L[P, List[C]]
}
@XingYiInterface(clazzes = Array(classOf[IParent]))
trait IParentHousePostCodeOps[L[_, _], P <: IParent] extends IXingYiSharedOps[L, P] {
  def housePostcodeLens: L[P, String]
}
@XingYiInterface(clazzes = Array(classOf[IHouse]))
trait IHouseOps[L[_, _], P <: IHouse] extends IXingYiSharedOps[L, P] {
  def houseNoLens: L[P, String]
  def postCodeLens: L[P, String]
}

@XingYiInterface(clazzes = Array(classOf[IChild]))
trait IChildOps[L[_, _], P <: IChild] extends IXingYiSharedOps[L, P] {
  def nameLens: L[P, String]
}
case class ParentForTest(name: String, age: Int, house: HouseForTest, children: List[ChildForTest])
object ParentForTest {
  implicit val proof: ProofOfBinding[IParent, ParentForTest] = new ProofOfBinding
  val prototype = ParentForTest("someParent", 1, HouseForTest(12, "somePostcode"), List())
  implicit val idLens = IdLens[ParentForTest, String](_.name, (p, n) => p.copy(name = n))


  object parentNameOps extends IParentNameOps[IXingYiLens, IParent] {
    override def nameLens = XingYiDomainStringLens(Lens[ParentForTest, String](_.name, (p, n) => p.copy(name = n)))
  }
  object parentHouseOps extends IParentHouseOps[IXingYiLens, IParent, IHouse] {
    override def houseLens = XingYiDomainObjectLens(Lens[ParentForTest, HouseForTest](_.house, (p, h) => p.copy(house = h)))
  }
  object parentChildrenOps extends IParentChildrenOps[IXingYiLens, IParent, IChild] {
    //    override def houseLens = XingYiDomainObjectLens(Lens[ParentForTest, HouseForTest](_.house, (p, h) => p.copy(house = h)))
    override def childrenLens = XingYiDomainObjectLens(Lens[ParentForTest, List[ChildForTest]](_.children, (p, c) => p.copy(children = c)))
  }

  implicit val parentProjection = ObjectProjection[IParent, ParentForTest](prototype,
    "name" -> StringFieldProjection(parentNameOps.nameLens),
    "house" -> ObjectFieldProjection(parentHouseOps.houseLens),
    "children" -> ListFieldProjection(parentChildrenOps.childrenLens)

  )
}
class ParentDomainForTest1 extends DomainDefn[IParent, ParentForTest](
  classOf[ParentForTest].getPackageName(),
  List("renderer1", "renderer2"),
  List(ParentForTest.parentNameOps -> ParentForTest.parentProjection,
    ParentForTest.parentHouseOps -> ParentForTest.parentProjection,
    ParentForTest.parentChildrenOps -> ParentForTest.parentProjection,
    HouseForTest.houseOps -> HouseForTest.projection,
    ChildForTest.parentChildrenOps -> ChildForTest.projection
    //    ParentForTest.parentChildrenOps -> ParentForTest.parentProjection
  ), List()) {
  override def packageName: String = "one.xingyi.test.client"

}

class ParentDomainForTest2 extends DomainDefn[IParent, ParentForTest](
  classOf[ParentForTest].getPackageName,
  List("renderer1", "renderer2"),
  List(ParentForTest.parentNameOps -> ParentForTest.parentProjection,
    //    ParentForTest.parentHouseOps -> ParentForTest.parentProjection,
    ParentForTest.parentChildrenOps -> ParentForTest.parentProjection
  ), List(
    new IParentHousePostCodeOps[XingYiManualPath, IParent] {
      override def housePostcodeLens: XingYiManualPath[IParent, String] =
        XingYiManualPath("lens_person_postcode_string", "stringLens",
          """function lens_person_postcode_string() { return compose(xxx(), xx())}""")
    }
  ))


case class LensLanguageForTest() extends LensLanguage {
  override def name: String = "testlenslang"
  override def mediaType: MediaType = MediaType("test")
}

object LensLanguageForTest {
  val lensLanguageForTestCode = "lensLanguageForTestCode"
  implicit object lensMakerForLensLanguageForTest extends LensCodeMaker[LensLanguageForTest] {
    override def apply[SharedE, DomainE](domainDefn: DomainDefn[SharedE, DomainE]): String = {
      lensLanguageForTestCode
    }
  }
}

trait ScriptFixtureWithTestLanguage extends ScriptFixture[LensLanguageForTest] {
  override def defaultLensLanguage: LensLanguageForTest = LensLanguageForTest()
  override implicit def lensCodeMaker: LensCodeMaker[LensLanguageForTest] = LensLanguageForTest.lensMakerForLensLanguageForTest
  def lensLanguageDigest = Digestor.default.apply(LensLanguageForTest.lensLanguageForTestCode)
}
trait ScriptFixture[L <: LensLanguage] {
  def defaultLensLanguage: L
  implicit def lensCodeMaker: LensCodeMaker[L]
  implicit val lensLanguages = new LensLanguages(List(defaultLensLanguage))
  val dom1 = new ParentDomainForTest1
  val dom2 = new ParentDomainForTest2

  val getMethod = GetMethodData[ParentForTest]("/parent/<id>", _ => ???)
  val postMethod = PostMethodData[ParentForTest]("/parent/<id>", _ => ???)
  val putMethod = PutMethodData[ParentForTest]("/parent/<id>", (a, b) => throw new RuntimeException)

  val domAndMethods1 = DomainAndMethods(List(getMethod, postMethod), dom1)
  val domAndMethods2 = DomainAndMethods(List(getMethod, postMethod), dom2)
  val listOfDomainAndMethods1 = ListofDomainAndMethods(domAndMethods1, List(domAndMethods1, domAndMethods2))
  val listofDomainAndMethods2 = ListofDomainAndMethods(domAndMethods2, List(domAndMethods1, domAndMethods2))


  val details1 = DomainDefnToDetails(dom1)
  val details2 = DomainDefnToDetails(dom2)
  val domainList = DomainList[IParent, ParentForTest](details1, details2)
  val code0 = domainList.domains(0).code
  val code1 = domainList.domains(1).code

  val js0Hash = code0(defaultLensLanguage).hash
  val js1Hash = code1(defaultLensLanguage).hash


  val sharedPackageName = new ParentDomainForTest1().sharedPackageName
  val domainCd1 = DomainCD("one.xingyi.test.client", sharedPackageName, "ParentDomainForTest1", DomainDefnToCodeDom.imports(sharedPackageName),
    List(EntityCD("House", "one.xingyi.core.script.IHouse"),
      EntityCD("Child", "one.xingyi.core.script.IChild"),
      EntityCD("Parent", "one.xingyi.core.script.IParent")),
    List(InterfaceCD("IParentNameOps", "ParentNameOps", List("Parent"), List(LensMethodCD("nameLens", "lens_parent_name_string", "stringLens[Parent]"))),
      InterfaceCD("IParentHouseOps", "ParentHouseOps", List("Parent", "House"), List(LensMethodCD("houseLens", "lens_parent_house_house", "objectLens[Parent,House]"))),
      InterfaceCD("IParentChildrenOps", "ParentChildrenOps", List("Parent", "Child"), List(LensMethodCD("childrenLens", "lens_parent_children_childlist", "listLens[Parent,Child]"))),
      InterfaceCD("IHouseOps", "HouseOps", List("House"), List(LensMethodCD("houseNoLens", "lens_house_houseno_string", "stringLens[House]"), LensMethodCD("postCodeLens", "lens_house_postcode_string", "stringLens[House]"))),
      InterfaceCD("IChildOps", "ChildOps", List("Child"), List(LensMethodCD("nameLens", "lens_child_name_string", "stringLens[Child]")))))


  val domainCd2 = DomainCD("one.xingyi.core.script", sharedPackageName, "ParentDomainForTest2", DomainDefnToCodeDom.imports(sharedPackageName),
    List(EntityCD("House", "one.xingyi.core.script.IHouse"),
      EntityCD("Child", "one.xingyi.core.script.IChild"),
      EntityCD("Parent", "one.xingyi.core.script.IParent")),
    List(InterfaceCD("IParentNameOps", "ParentNameOps", List("Parent"), List(LensMethodCD("nameLens", "lens_parent_name_string", "stringLens[Parent]"))),
      InterfaceCD("IParentChildrenOps", "ParentChildrenOps", List("Parent", "Child"), List(LensMethodCD("childrenLens", "lens_parent_children_childlist", "listLens[Parent,Child]")))))

  val domainDd1 = DomainDD("ParentDomainForTest1",
    List(MethodDD("Get", "/parent/<id>"), MethodDD("Post", "/parent/<id>")),
    List(EntityDD("House", "one.xingyi.core.script.IHouse"),
      EntityDD("Child", "one.xingyi.core.script.IChild"),
      EntityDD("Parent", "one.xingyi.core.script.IParent")),
    Map("one.xingyi.core.script.IParent" -> List(LensMethodDD("nameLens", "lens_parent_name_string"), LensMethodDD("houseLens", "lens_parent_house_house"), LensMethodDD("childrenLens", "lens_parent_children_childlist")),
      "one.xingyi.core.script.IHouse" -> List(LensMethodDD("houseNoLens", "lens_house_houseno_string"), LensMethodDD("postCodeLens", "lens_house_postcode_string")),
      "one.xingyi.core.script.IChild" -> List(LensMethodDD("nameLens", "lens_child_name_string"))),
    List("renderer1", "renderer2"))


  val domainDd2 =
    DomainDD("ParentDomainForTest2",
      List(MethodDD("Get", "/parent/<id>"), MethodDD("Post", "/parent/<id>")),
      List(EntityDD("House", "one.xingyi.core.script.IHouse"),
        EntityDD("Child", "one.xingyi.core.script.IChild"),
        EntityDD("Parent", "one.xingyi.core.script.IParent")),
      Map("one.xingyi.core.script.IParent" ->
          List(LensMethodDD("nameLens", "lens_parent_name_string"),
            LensMethodDD("childrenLens", "lens_parent_children_childlist"))),
      List("renderer1", "renderer2"))

}

class CreateFilesForScriptFixture extends UtilsSpec {

  val scalaFile = new File("modules/test/src/test/scala/one/xingyi/test/createdCode/CreatedPerson.scala").getAbsoluteFile
  val javascriptFile = new File("modules/test/src/test/resources/createdPerson.js").getAbsoluteFile
  behavior of "Create The Example Domain Classes"

  val isCorrectDirectory = scalaFile.getPath.replace('/', '\\').endsWith("restscala\\modules\\test\\src\\test\\scala\\one\\xingyi\\test\\createdCode\\CreatedPerson.scala")
  it should "be talking to the correct directory" in {
    checkCorrectDirectory
  }
  behavior of "Create Files for ScriptFixture"


  def checkCorrectDirectory = {
    withClue(s"directory is $scalaFile")(isCorrectDirectory shouldBe true)
  }

  it should "make the scala" in {
    checkCorrectDirectory
    val domainCd = implicitly[DomainDefnToCodeDom].apply(new ParentDomainForTest1)
    val scala = implicitly[ToScalaCode[DomainCD]] apply domainCd
    Files.printToFile(scalaFile)(pw => pw.print(scala))
  }


}
