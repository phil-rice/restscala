package one.xingyi.core.client
import one.xingyi.core.UtilsSpec
import one.xingyi.core.reflection.ClassTags
import one.xingyi.core.serverMediaType.LensLanguage
import org.scalatest.FunSuite

import scala.reflect.ClassTag

abstract class ClientLanguageTest[SL <: LensLanguage : ClassTag, CL <: ClientLanguage : ClassTag] extends UtilsSpec {
  behavior of "Client Language " + ClassTags.nameOf[SL] + "/" + ClassTags.nameOf[CL]

  def sl: SL
  def cl: CL

  it should "have a string that matchs" in {
    sl.name shouldBe cl.language
  }

}
