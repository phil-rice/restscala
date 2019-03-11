package one.xingyi.script
import one.xingyi.core.UtilsSpec
import one.xingyi.core.json.{JsonParser, JsonWriter}
import one.xingyi.core.script.{ClientPreferedLanguage, ScriptFixture}
import one.xingyi.core.serverMediaType.LensLanguage
import one.xingyi.javascript.server.Javascript
import one.xingyi.lensdsl.server.LensDsl
import one.xongyi.javascript.server.{JavascriptScriptFixture, LensDslScriptFixture}
import org.json4s.JValue

abstract class LensLanguageClientAndServerTest[J: JsonParser : JsonWriter, SL <: LensLanguage](preferedLanguage: ClientPreferedLanguage) extends ScriptFixture[SL] with UtilsSpec {
  behavior of "LensLanguage between client and server"

  it should "be setup properly" in {
    preferedLanguage.s shouldBe defaultLensLanguage.name
  }

  it should "allow lens to be processed on the client" in {

  }
}

import one.xingyi.json4s.Json4sParser._
import one.xingyi.json4s.Json4sWriter._


class JavascriptLensLanguageClientAndServerTest extends LensLanguageClientAndServerTest[JValue, Javascript](ClientPreferedLanguage("javascript")) with JavascriptScriptFixture {


}
class LensDSlLanguageClientAndServerTest extends LensLanguageClientAndServerTest[JValue, LensDsl](ClientPreferedLanguage("lensdsl")) with LensDslScriptFixture {


}