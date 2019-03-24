package one.xingyi.scriptBackend3

import one.xingyi.core.UtilsSpec
import one.xingyi.core.json.{LensDefnFromProjection, ProjectionToLensDefns, SimpleLensDefnFromProjection}
import one.xingyi.core.optics.{LensLine, SimpleLensDefn}
import one.xingyi.javascript.server.LensLineToJavascript

import scala.collection.immutable

class JavascriptMadeFromProjectsSpec extends UtilsSpec {
  behavior of "Created Javascript from combination of automatic and manual"

  it should "be in order, and express the lens" in {
    val projectionToLensDefns = implicitly[ProjectionToLensDefns]
    val lensDefns: immutable.Seq[LensDefnFromProjection[_, _]] = new Model3PersonDefn().lens

    //    val lensDefns: immutable.Seq[LensDefnFromProjection[_, _]] = projectionToLensDefns(Person.projection).map(_._2)
    val lensLines = lensDefns.map(_.lensLine)
    lensLines.map(implicitly[LensLineToJavascript]) shouldBe List(
      """function lens_address_line1_string(){return lens("line1")};""",
      """function lens_address_line2_string(){return lens("line2")};""",
      """function lens_person_address_address(){return compose(lens("addresses"),lensForItemInList(0))};""",
      """function lens_person_addresses_addresslist(){return lens("addresses")};""",
      """function lens_person_line1_string(){return compose(lens("addresses"),lensForItemInList(0),lens("line1"))};""",
      """function lens_person_line2_string(){return compose(lens("addresses"),lensForLastItemInList,lens("line2"))};""",
      """function lens_person_name_string(){return lens("name")};""",
      """function lens_person_telephonenumber_telephonenumber(){return lens("telephoneNumber")};""",
      """function lens_telephonenumber_number_string(){return lens("number")};""")
  }
}