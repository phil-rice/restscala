package one.xingyi.lensdsl.server
import one.xingyi.core.codemaker.LensCodeMaker

trait LensDslScriptFixture {
  def defaultLensLanguage: LensDsl = LensDsl
  implicit def lensCodeMaker: LensCodeMaker[LensDsl] = LensDsl.lensCodeMaker
  def expectedCodeString1: String = """"lensdsl":"_azJ67H0hTQxygV4hKxxVLJH9xGxj0CzvMBKaKONesc=""""
  def expectedCodeString2: String = """"lensdsl":"8ofNMjM5br9vyCXlpOfPyEximH6bsBlJLWLSb7rmaKw=""""
}
