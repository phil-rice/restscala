package one.xongyi.javascript.server
import one.xingyi.core.codemaker.LensCodeMaker
import one.xingyi.javascript.server.Javascript

trait JavascriptScriptFixture {
   def defaultLensLanguage: Javascript = Javascript
   implicit def lensCodeMaker: LensCodeMaker[Javascript] = Javascript.lensCodeMaker
   def expectedCodeString1: String = """"javascript":"2gX9qynNFOcahh7bmqApJP787bIMJebrvuWn1Ddz-Jg=""""
   def expectedCodeString2: String = """"javascript":"to3fmQzyuC-5ryhueR2T0RB3NI3VImLvDSMoS5SkAyA=""""

}
