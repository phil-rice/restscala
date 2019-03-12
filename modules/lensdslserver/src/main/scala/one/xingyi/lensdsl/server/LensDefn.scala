package one.xingyi.lensdsl.server


trait LensDefn
case class StringLensDefn(name: String) extends LensDefn
case class IntegerLensDefn(name: String) extends LensDefn
case class DoubleLensDefn(name: String) extends LensDefn
case class ViewLensDefn(name: String, clazz: String) extends LensDefn
case class ListLensDefn(name: String, clazz: String) extends LensDefn
case class SimpleListLensDefn(name: String, clazz: String) extends LensDefn
case class FirstItemInListDefn() extends LensDefn
case class IdentityDefn() extends LensDefn
case class ItemAsListDefn() extends LensDefn

trait LensValueParser {
  def apply(s: String): List[LensDefn]
}

object LensValueParser {
  def simple: LensValueParser = new SimpleLensParser()
}
class SimpleLensParser extends LensValueParser {
  val basic: Map[String, (String => LensDefn)] = Map(
    "integer" -> IntegerLensDefn.apply,
    "string" -> StringLensDefn.apply,
    "double" -> DoubleLensDefn.apply,
    "{firstItem}" -> (_ => FirstItemInListDefn()),
    "{identity}" -> (_ => IdentityDefn()),
    "{itemAsList}" -> (_ => ItemAsListDefn()))


  override def apply(s: String): List[LensDefn] = s.split(",").toList.map { item: String =>
    item.split("/") match {
      case Array(name, clazz) if clazz.startsWith("**") => SimpleListLensDefn(name, clazz.substring(2))
      case Array(name, clazz) if clazz.startsWith("*") => ListLensDefn(name, clazz.substring(1))
      case Array(name) if basic.contains(name) => basic(name)(name)
      case Array(name, clazz) if basic.contains(clazz) => basic(clazz)(name)
      case Array(name, clazz) => ViewLensDefn(name, clazz)
      case _ => throw new RuntimeException("could not find two parts in item " + item + " which is in " + s)
    }
  }
}
