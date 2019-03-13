package one.xingyi.lensdsl.client

import one.xingyi.core.codemaker.{LensCodeMaker, MediaType}
import one.xingyi.core.serverMediaType.{DomainDefn, LensLanguage}


trait LensDsl extends LensLanguage {
  override def name: String = "lensdsl"
  override def mediaType: MediaType = MediaType("application/javascript")
}

object LensDsl extends LensDsl {

  object lensCodeMaker extends LensCodeMaker[LensDsl] {
    override def apply[SharedE, DomainE](domainDefn: DomainDefn[SharedE, DomainE]): String = "not implemented yet"
  }
}