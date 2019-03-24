package one.xingyi.javascript.server
import one.xingyi.core.optics._
import one.xingyi.core.strings.Strings

trait LensLineToJavascript extends (LensLine => String)

object LensLineToJavascript extends LensLineToJavascript {
  implicit val lensLineToJavascript: LensLineToJavascript = LensLineToJavascript
  override def apply(lensLine: LensLine): String = {
    val body = lensLine.defns.flatMap {
      case s: StringLensDefn[_] => None //Some("stringLens()")
      case d: DoubleLensDefn[_] => None //Some("doubleLens()")
      case i: IntegerLensDefn[_] => None //Some("integerLens()")
      case b: BooleanLensDefn[_] => None //Some("booleanLens()")
      //      case l: ItemInListDefn[_] => None
      case v: ViewLensDefn[_, _] => None
      case l: ListLensDefn[_, _] => None
      case l: ItemInListDefn[_] => Some("lensForItemInList" + Strings.bracket(l.n.toString))
      case l: LastItemInListDefn[_] => Some("lensForLastItemInList")
      case c: ChildLensDefn[_] => Some("lens" + Strings.bracket(Strings.quote(c.name)))
    }
    val bodyString = body.mkString(",")
    val withCompose = if (body.size > 1) "return compose(" + bodyString + ")" else "return " + bodyString
    s"""function ${lensLine.name}(){$withCompose};"""
  }
}