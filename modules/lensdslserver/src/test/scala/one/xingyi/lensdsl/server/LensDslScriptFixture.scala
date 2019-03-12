package one.xingyi.lensdsl.server

import one.xingyi.core.codemaker.LensCodeMaker

trait LensDslScriptFixture {
   def defaultLensLanguage: LensDsl = LensDsl
   implicit def lensCodeMaker: LensCodeMaker[LensDsl] = LensDsl.lensCodeMaker
   def expectedCodeString1: String = """"lensdsl":"0PmKqjmdhr_cPIBA5DF3sFP5RbmM7MOdeWWjAzDsv_Y=""""
   def expectedCodeString2: String = """"lensdsl":"0PmKqjmdhr_cPIBA5DF3sFP5RbmM7MOdeWWjAzDsv_Y=""""
}
