package one.xongyi.javascript.server
import one.xingyi.core.codemaker.LensCodeMaker
import one.xingyi.javascript.server.Javascript

trait JavascriptScriptFixture {
   def defaultLensLanguage: Javascript = Javascript
   implicit def lensCodeMaker: LensCodeMaker[Javascript] = Javascript.lensCodeMaker
   def expectedCodeString1: String = """"javascript":"sxehR7C6UyHnE_KEuWobupvcoa_jkz7t5TdudQkGPJU=""""
   def expectedCodeString2: String = """"javascript":"Y1TrpwgUDDsLftdaCeDIs5xDQxvN__X5uNajygKtvaQ=""""

}
