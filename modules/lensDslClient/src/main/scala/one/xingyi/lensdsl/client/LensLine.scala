package one.xingyi.lensdsl.client

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


case class LensLine(name: String, defns: List[LensDefn])
