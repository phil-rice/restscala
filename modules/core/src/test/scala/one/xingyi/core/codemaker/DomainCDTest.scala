/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.codemaker

import one.xingyi.core.UtilsSpec
import one.xingyi.core.script.{ScriptFixture, ScriptFixtureWithTestLanguage}

class CodeDomTest extends UtilsSpec with ScriptFixtureWithTestLanguage {

  behavior of "DomainCd"

  val domainDefnToCodeDom = implicitly[DomainDefnToCodeDom]
  val domainCdToScala = implicitly[ToScalaCode[DomainCD]]

  it should "have a smoke test " in {
    domainCdToScala(domainCd1).noWhiteSpace shouldBe
      """package one.xingyi.test.client
        |
        |import one.xingyi.core.script._
        |import one.xingyi.core.json.IXingYiHeaderFor
        |import one.xingyi.core.optics.Lens
        |import one.xingyi.core.script.{Domain,DomainMaker, IXingYi,ServerDomain}
        |
        |object ParentDomainForTest1 extends ServerDomain{
        |  def lens=List("lens_parent_name_string","lens_parent_house_house","lens_parent_children_childlist","lens_house_houseno_string","lens_house_postcode_string","lens_child_name_string")
        |}
        |
        |case class House (mirror: Object) extends Domain with one.xingyi.core.script.IHouse
        |object House {
        |  implicit object default extends DomainMaker[House] {
        |    override def create(mirror: Object): House = House(mirror)
        |  }
        |}
        |
        |
        |case class Child (mirror: Object) extends Domain with one.xingyi.core.script.IChild
        |object Child {
        |  implicit object default extends DomainMaker[Child] {
        |    override def create(mirror: Object): Child = Child(mirror)
        |  }
        |}
        |
        |
        |case class Parent (mirror: Object) extends Domain with one.xingyi.core.script.IParent
        |object Parent {
        |  implicit object default extends DomainMaker[Parent] {
        |    override def create(mirror: Object): Parent = Parent(mirror)
        |  }
        |}
        |
        |
        |object ParentNameOps {
        |   implicit def hasHeader: IXingYiHeaderFor[ParentNameOps] =  () => List("lens_parent_name_string")
        |}
        |class ParentNameOps(implicit val xingYi: IXingYi) extends IParentNameOps[Lens, Parent] {
        |   def nameLens = xingYi.stringLens[Parent]("lens_parent_name_string")
        |}
        |
        |object ParentHouseOps {
        |   implicit def hasHeader: IXingYiHeaderFor[ParentHouseOps] =  () => List("lens_parent_house_house")
        |}
        |class ParentHouseOps(implicit val xingYi: IXingYi) extends IParentHouseOps[Lens, Parent,House] {
        |   def houseLens = xingYi.objectLens[Parent,House]("lens_parent_house_house")
        |}
        |
        |object ParentChildrenOps {
        |   implicit def hasHeader: IXingYiHeaderFor[ParentChildrenOps] =  () => List("lens_parent_children_childlist")
        |}
        |class ParentChildrenOps(implicit val xingYi: IXingYi) extends IParentChildrenOps[Lens, Parent,Child] {
        |   def childrenLens = xingYi.listLens[Parent,Child]("lens_parent_children_childlist")
        |}
        |
        |object HouseOps {
        |   implicit def hasHeader: IXingYiHeaderFor[HouseOps] =  () => List("lens_house_houseno_string","lens_house_postcode_string")
        |}
        |class HouseOps(implicit val xingYi: IXingYi) extends IHouseOps[Lens, House] {
        |   def houseNoLens = xingYi.stringLens[House]("lens_house_houseno_string")
        |   def postCodeLens = xingYi.stringLens[House]("lens_house_postcode_string")
        |}
        |
        |object ChildOps {
        |   implicit def hasHeader: IXingYiHeaderFor[ChildOps] =  () => List("lens_child_name_string")
        |}
        |class ChildOps(implicit val xingYi: IXingYi) extends IChildOps[Lens, Child] {
        |   def nameLens = xingYi.stringLens[Child]("lens_child_name_string")
        |}""".stripMargin.noWhiteSpace
  }
}
