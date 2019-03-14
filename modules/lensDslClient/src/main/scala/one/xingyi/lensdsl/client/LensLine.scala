package one.xingyi.lensdsl.client

import one.xingyi.core.json.{JsonParser, JsonString, JsonWriter}
import one.xingyi.core.optics.Lens

trait LensLineParser {
  def apply(s: String): LensLine
}

object LensLineParser {
  implicit def simple(implicit lensValueParser: LensValueParser): LensLineParser = new SimpleLensLineParser()
}
class SimpleLensLineParser(implicit lensValueParser: LensValueParser) extends LensLineParser {
  override def apply(s: String): LensLine = s.split("=") match {
    case Array(name, parts) => LensLine(name, lensValueParser(parts))
    case _ => throw new RuntimeException(s"Cannot find lensName=values in [$s]")
  }
}


case class LensLine(name: String, defns: List[LensDefn[_]])

case class LensDefnToLens[J, T](pf: PartialFunction[LensDefn[T], Lens[J, T]])(implicit jsonWriter: JsonWriter[J], jsonParser: JsonParser[J])
