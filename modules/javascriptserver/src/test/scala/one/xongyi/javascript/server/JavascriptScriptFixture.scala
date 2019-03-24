package one.xongyi.javascript.server
import one.xingyi.core.codemaker.LensCodeMaker
import one.xingyi.javascript.server.Javascript

trait JavascriptScriptFixture {
   def defaultLensLanguage: Javascript = Javascript
   implicit def lensCodeMaker: LensCodeMaker[Javascript] = Javascript.lensCodeMaker
   def expectedCodeString1: String = """"javascript":"lrUORQBagKGHAhquiHvkwTDBo4rQLIqJpvx_jxUrJEQ=""""
   def expectedCodeString2: String = """"javascript":"eBuceOmcE2WU0iHVveIbF8neO2iXFlmc0vmz9YVRf_0=""""

}
