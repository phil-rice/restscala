package one.xingyi.javascript.server
import one.xingyi.core.optics._
import one.xingyi.core.strings.Strings

import scala.collection.GenTraversableOnce

trait LensLineToJavascript extends (LensLine => String)

object LensLineToJavascript extends LensLineToJavascript {
  implicit val lensLineToJavascript: LensLineToJavascript = LensLineToJavascript
  def one: Function[LensDefn[_, _], Traversable[String]] = {
    case s: StringLensDefn[_] => None //Some("stringLens()")
    case d: DoubleLensDefn[_] => None //Some("doubleLens()")
    case i: IntegerLensDefn[_] => None //Some("integerLens()")
    case b: BooleanLensDefn[_] => None //Some("booleanLens()")
    //      case l: ItemInListDefn[_] => None
    case v: ViewLensDefn[_, _] => None
    case l: ListLensDefn[_, _] => None
    case l: ItemInListDefn[_] => Some("lensForItemInList" + Strings.bracket(l.n.toString))
    case l: LastItemInListDefn[_] => Some("lensForLastItemInList()")
    case c: ChildLensDefn[_] => Some("lens" + Strings.bracket(Strings.quote(c.name)))
  }
  def recurse(defns: List[String]): String = defns match {
    case defn :: Nil => defn
    case head :: tail => "compose(" + head + "," + recurse(tail) +")"
  }
  override def apply(lensLine: LensLine): String = {
    val bodyStrings = lensLine.defns.flatMap(one)
    val bodyString = recurse(bodyStrings)
    s"""function ${lensLine.name}(){return $bodyString};"""
  }
}