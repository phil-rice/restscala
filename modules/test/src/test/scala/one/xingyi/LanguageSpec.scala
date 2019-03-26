package one.xingyi
import one.xingyi.core.client.ClientLanguageTest
import one.xingyi.javascript.client.JavascriptClient
import one.xingyi.javascript.server.Javascript
import one.xingyi.lensdsl.client.LensDslClient
import one.xingyi.lensdsl.server.LensDsl

class JavascriptLanguageSpec extends ClientLanguageTest[Javascript, JavascriptClient]{
  override def sl: Javascript = Javascript
  override def cl: JavascriptClient = JavascriptClient
}

class LensDslLanguageSpec extends ClientLanguageTest[LensDsl, LensDslClient] {
  override def sl: LensDsl = LensDsl
  override def cl: LensDslClient = LensDslClient
}
