package one.xingyi.javascript.client
import javax.script.{Invocable, ScriptEngine}
import one.xingyi.core.optics.Lens
import one.xingyi.core.script._

import language.postfixOps

class JavascriptXingYiLoader() extends IXingYiLoader {
  override def apply(javaScript: String): IXingYi = {
    import jdk.nashorn.api.scripting.NashornScriptEngineFactory
    val engine: ScriptEngine = new NashornScriptEngineFactory().getScriptEngine("--language=es6 ")
    engine.eval(javaScript)
    new JavascriptXingYi(engine)
  }
}

class JavascriptXingYi(engine: ScriptEngine) extends IXingYi {
  val inv = engine.asInstanceOf[Invocable]

  def wrap[X](name: => String, fn: => X): X = try {
    fn
  } catch {
    case e: Exception => throw new XingYiExecutionException(s"Error executing $name", e)
  }

  override def rawRender(name: String, t: Object): String = wrap("render", inv.invokeFunction(s"render_$name", t).asInstanceOf[String])

  override def objectLens[T1 <: Domain, T2 <: Domain](name: String)(implicit maker1: DomainMaker[T1], maker2: DomainMaker[T2]): Lens[T1, T2] = Lens[T1, T2](
    { t => wrap(s"objectLens.get($name)", maker2.create(inv.invokeFunction("getL", name, t.mirror))) },
    { (t, v) => wrap(s"objectLens.set($name)", maker1.create(inv.invokeFunction("setL", name, t.mirror, v.mirror))) })

  override def stringLens[T <: Domain](name: String)(implicit maker: DomainMaker[T]): Lens[T, String] = Lens[T, String](
    { t => wrap(s"stringLens.get($name)", inv.invokeFunction("getL", name, t.mirror).asInstanceOf[String]) },
    { (t, v) => wrap(s"objectstringLensLens.set($name)", maker.create(inv.invokeFunction("setL", name, t.mirror, v))) })

  def parse[T <: Domain](s: String)(implicit domainMaker: DomainMaker[T]) = wrap("parse", domainMaker.create(inv.invokeFunction("parse", s)))


  import java.util

  import jdk.nashorn.api.scripting.ScriptObjectMirror

  def toList(original: Any): List[Object] = {
    if (!original.isInstanceOf[ScriptObjectMirror]) throw new IllegalStateException("This is supposed to be an object mirror and it isn't" + original.getClass + " / " + original)
    val jsOriginal = original.asInstanceOf[ScriptObjectMirror]
    if (!jsOriginal.isArray) throw new IllegalStateException("This is supposed to be an array mirror and it isn't" + original.getClass + " / " + original)
    val listResult = new util.ArrayList[Object]
    val length = jsOriginal.get("length").asInstanceOf[Integer]
    (0 to length - 1) map (i => jsOriginal.get("" + i)) toList
  }

  //  def fromMirror(d: Domain) = d.mirror.asInstanceOf[ScriptObjectMirror].
  def fromList(list: List[Domain]): Object =
    inv.invokeFunction("makeArray", list.map(_.mirror): _*)

  override def listLens[T1 <: Domain, T2 <: Domain](name: String)(implicit maker1: DomainMaker[T1], maker2: DomainMaker[T2]): Lens[T1, List[T2]] = Lens[T1, List[T2]](
    { t => toList(inv.invokeFunction("getL", name, t.mirror)).map(maker2.create) }, {
      (t, v) => maker1.create(inv.invokeFunction("setL", name, t.mirror, fromList(v)))
    })
}
