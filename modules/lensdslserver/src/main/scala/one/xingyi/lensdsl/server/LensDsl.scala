package one.xingyi.lensdsl.server

import one.xingyi.core.codemaker.{LensCodeMaker, MediaType}
import one.xingyi.core.optics.LensLine
import one.xingyi.core.serverMediaType.{DomainDefn, LensLanguage}


trait LensDsl extends LensLanguage {
  override def name: String = "lensdsl"
  override def mediaType: MediaType = MediaType("application/javascript")
}

object LensDsl extends LensDsl {

  object lensCodeMaker extends LensCodeMaker[LensDsl] {
    def one(line: LensLine) = line.name + "="+line.defns.map(_.toString).mkString(",")

    override def apply[SharedE, DomainE](domainDefn: DomainDefn[SharedE, DomainE]): String =
      domainDefn.lens.map(_.lensLine).map(one).mkString("\n")

  }
}