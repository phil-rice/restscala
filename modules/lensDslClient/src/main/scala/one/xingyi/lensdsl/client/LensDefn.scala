package one.xingyi.lensdsl.client

import one.xingyi.core.json.{JsonParser, JsonParserWriter, JsonWriter}
import one.xingyi.core.optics.Lens

trait LensDefn[From, To] {
}

trait SimpleLensDefn[From, To] extends LensDefn[From, To] {
  def lens: Lens[From, To]
}
trait JsonLensDefn[From, To] extends LensDefn[From, To] {
  def lens(implicit jsonParserWriter: JsonParserWriter[From]): Lens[From, To]
  override def equals(obj: Any): Boolean = if (obj == null) false else obj.getClass == this.getClass
  override def hashCode(): Int = getClass.hashCode()
}
class StringLensDefn[From]() extends JsonLensDefn[From, String] {
  override def lens(implicit jsonParserWriter: JsonParserWriter[From]): Lens[From, String] = jsonParserWriter.lensToString
  override def toString: String = "{string}"
}
case class ChildLensDefn[From](name: String) extends JsonLensDefn[From, From] {
  override def lens(implicit jsonParserWriter: JsonParserWriter[From]): Lens[From, From] = jsonParserWriter.lensToChild(name)
}
class IntegerLensDefn[From]() extends JsonLensDefn[From, Int] {
  override def lens(implicit jsonParserWriter: JsonParserWriter[From]): Lens[From, Int] = jsonParserWriter.lensToInteger
  override def toString: String = "{integer}"
}
class DoubleLensDefn[From]() extends JsonLensDefn[From, Double] {
  override def lens(implicit jsonParserWriter: JsonParserWriter[From]): Lens[From, Double] = jsonParserWriter.lensToDouble
  override def toString: String = "{double}"
}
class BooleanLensDefn[From]() extends JsonLensDefn[From, Boolean] {
  override def lens(implicit jsonParserWriter: JsonParserWriter[From]): Lens[From, Boolean] = jsonParserWriter.lensToBoolean
  override def toString: String = "{boolean}"
}
case class ViewLensDefn[From, To](lens: Lens[From, To]) extends LensDefn[From, To] {
  override def toString: String = "{double}"
}

class ListLensDefn[From, To]() extends JsonLensDefn[From, List[From]] {
  override def lens(implicit jsonParserWriter: JsonParserWriter[From]): Lens[From, List[From]] = jsonParserWriter.lensToList
  override def toString: String = "*"
}
case class ItemInListDefn[From](n: Int) extends SimpleLensDefn[List[From], From] {
  def lens: Lens[List[From], From] = Lens.itemInListL(n)
  override def toString: String = "#" + n
}
class LastItemInListDefn[From]() extends SimpleLensDefn[List[From], From] {
  override def lens: Lens[List[From], From] = Lens(_.last, (old, f) => old.dropRight(1) :+ f)
  override def toString: String = "#last"
}
class IdentityDefn[From]() extends SimpleLensDefn[From, From] {
  override def lens: Lens[From, From] = Lens.identity
  override def toString: String = "{identity}"
}
class ItemAsListDefn[From, To]() extends SimpleLensDefn[From, List[From]] {
  override def lens: Lens[From, List[From]] = Lens(x => List(x), (old, list) => list.head)
  override def toString: String = "{itemAsList}"
}

trait LensValueParser {
  def apply(s: String): List[LensDefn[_, _]]
}

object LensValueParser {
  implicit def simple(implicit viewNamesToViewLens: ViewNamesToViewLens): LensValueParser = new SimpleLensParser(viewNamesToViewLens)
}


class ViewNamesToViewLens(map: Map[String, Lens[_, _]]) {
  def legalValues = map.keySet.toList.sorted
  def contains(name: String) = map.contains(name)
  def lens(name: String) = map.get(name)

}

//TODO check that the views and the lists have legal names. Can do that by passing the names in, as we know those names.
class SimpleLensParser(viewNamesToViewLens: ViewNamesToViewLens) extends LensValueParser {

  override def apply(s: String): List[LensDefn[_, _]] = s.split(",").toList.map { item: String =>
    item match {
      case "{integer}" => new IntegerLensDefn()
      case "{string}" => new StringLensDefn()
      case "{double}" => new DoubleLensDefn()
      case "{boolean}" => new BooleanLensDefn()
      case "{identity}" => new IdentityDefn()
      case "{itemAsList}" => new ItemAsListDefn()
      case "*" => new ListLensDefn()
      case "#last" => new LastItemInListDefn()
      case n if n.startsWith("#") => ItemInListDefn(n.substring(1).toInt)
      case name if name.startsWith("!") && viewNamesToViewLens.contains(name.substring(1)) => ViewLensDefn(viewNamesToViewLens.lens(name.substring(1)).getOrElse(throw new RuntimeException(s"lens '$name references '$name but that is not a legal value. Legal values are ${viewNamesToViewLens.legalValues}")))
      case name => ChildLensDefn(name)
    }
  }
}
