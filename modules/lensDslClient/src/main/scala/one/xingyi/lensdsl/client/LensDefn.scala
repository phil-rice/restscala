package one.xingyi.lensdsl.client
import one.xingyi.core.json.{JsonParser, JsonWriter}
import one.xingyi.core.optics.Lens

trait LensDefn[T] {
  //  def Lens[Mirror,T]
}
case class StringLensDefn(name: String) extends LensDefn[String]
case class IntegerLensDefn(name: String) extends LensDefn[Int]
case class DoubleLensDefn(name: String) extends LensDefn[Double]
case class ViewLensDefn[T](name: String, clazz: String) extends LensDefn[T]
case class ListLensDefn[T](name: String, clazz: String) extends LensDefn[List[T]]
case class FirstItemInListDefn[T]() extends LensDefn[T]
case class IdentityDefn[T]() extends LensDefn[T]
case class ItemAsListDefn[T]() extends LensDefn[List[T]]

trait LensValueParser {
  def apply(s: String): List[LensDefn[_]]
}

object LensValueParser {
  implicit val simple: LensValueParser = new SimpleLensParser()
}
class SimpleLensParser extends LensValueParser {
  val basic: Map[String, (String => LensDefn[_])] = Map(
    "integer" -> IntegerLensDefn.apply,
    "string" -> StringLensDefn.apply,
    "double" -> DoubleLensDefn.apply,
    "{firstItem}" -> (_ => FirstItemInListDefn()),
    "{identity}" -> (_ => IdentityDefn()),
    "{itemAsList}" -> (_ => ItemAsListDefn()))


  override def apply(s: String): List[LensDefn[_]] = s.split(",").toList.map { item: String =>
    item.split("/") match {
      case Array(name, clazz) if clazz.startsWith("*") => ListLensDefn(name, clazz.substring(1))
      case Array(name) if basic.contains(name) => basic(name)(name)
      case Array(name, clazz) if basic.contains(clazz) => basic(clazz)(name)
      case Array(name, clazz) => ViewLensDefn(name, clazz)
      case _ => throw new RuntimeException("could not find two parts in item " + item + " which is in " + s)
    }
  }
}
