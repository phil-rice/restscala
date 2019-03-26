package one.xingyi.lensdsl.server
import one.xingyi.core.codemaker.LensCodeMaker

trait LensDslScriptFixture {
  def defaultLensLanguage: LensDsl = LensDsl
  implicit def lensCodeMaker: LensCodeMaker[LensDsl] = LensDsl.lensCodeMaker
  def expectedCodeString1: String = """"lensdsl":"dFEQ0nBHUBq7_LvC78ZILUEOYwfOcqZX8gQ1qxdP1yU=""""
  def expectedCodeString2: String = """"lensdsl":"zzTJKulvWoAYDTfJS7sKkRALPT7OX7NwuvRipzxibmM=""""
}
