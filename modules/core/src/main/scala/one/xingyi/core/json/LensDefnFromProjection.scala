/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.json

import one.xingyi.core.codemaker.InterfaceToImplName
import one.xingyi.core.optics._
import one.xingyi.core.reflection.{ClassTags, Reflect}

import scala.reflect.ClassTag


class ProjectionToLensDefns {
  def apply[Shared, Domain](projection: Projection[Shared, Domain])(implicit interfaceToImplName: InterfaceToImplName): List[(IXingYiLens[_, _], LensDefnFromProjection[_, _])] = {
    implicit val sharedClassTag: ClassTag[Shared] = projection.sharedClassTag
    implicit val domainClassTag: ClassTag[Domain] = projection.domainClassTag
    implicit val proof = projection.proof

    projection match {
      case ObjectProjection(prototype, children@_*) =>
        val x = children
        children.toList.flatMap {
          case (name, s: StringField[_]) => List()
          case (name, s: StringFieldProjection[_, _]) => List((s.lens, LensDefnFromProjection.string(name)(s.sharedClassTag, s.lensName)))
          case (name, o: ObjectFieldProjection[_, _, _, _]) => (o.lens, LensDefnFromProjection.obj(name)(o.sharedClassTag, o.sharedTargetClassTag, o.lensName, interfaceToImplName)) :: apply(o.projection)
          case (name, l: ListFieldProjection[_, _, _, _]) => (l.lens, LensDefnFromProjection.list(name)(l.sharedClassTag, l.sharedTargetClassTag, l.lensName)) :: apply(l.projection)
        }
    }
  }
}
object ProjectionToLensDefns {
  implicit val projectionToLensDefns: ProjectionToLensDefns = new ProjectionToLensDefns
}


sealed abstract class LensDefnFromProjection[A, B](implicit val classA: ClassTag[A], val classB: ClassTag[B]) {
  def name: String
  def lensDefns: List[LensDefn[_, _]]
  def isList: Boolean = lensLine.defns.find(_.isInstanceOf[ListLensDefn[_, _]]).isDefined
  def lensLine: LensLine = LensLine(name, lensDefns)

  val a = classA.runtimeClass.getSimpleName
  val b = classB.runtimeClass.getSimpleName
}
case class SimpleLensDefnFromProjection[A: ClassTag, B: ClassTag](name: String, lensDefns: List[LensDefn[_, _]]) extends LensDefnFromProjection[A, B] {
  def names: List[String] = lensDefns.collect { case c: ChildLensDefn[_] => c.name }
}
case class ManualLensDefnFromProjection[A: ClassTag, B: ClassTag](name: String, lensDefns: List[LensDefn[_, _]]) extends LensDefnFromProjection[A, B]


trait LensNameForJavascript[A, B] {
  def apply(name: String, isList: Boolean)(implicit classTagA: ClassTag[A], classTagB: ClassTag[B]): String
}
object LensNameForJavascript {
  def removedFirstLetterIfI(s: String) = if (s.startsWith("I")) s.substring(1) else s
  def objectName[T](implicit classTag: ClassTag[T]) = removedFirstLetterIfI(classTag.runtimeClass.getSimpleName)

  implicit def default[A, B] = new LensNameForJavascript[A, B] {
    override def apply(name: String, isList: Boolean)(implicit classTagA: ClassTag[A], classTagB: ClassTag[B]) = {
      (List("lens", objectName[A](classTagA), name, objectName[B](classTagB)).mkString("_") + (if (isList) "List" else "")).toLowerCase
    }
  }
}

object LensDefnFromProjection {
  def string[A: ClassTag](name: String)(implicit lensNameForJavascript: LensNameForJavascript[A, String]): LensDefnFromProjection[A, String] =
    SimpleLensDefnFromProjection(lensNameForJavascript(name, false), List(new ChildLensDefn[A](name), new StringLensDefn))
  def obj[A: ClassTag, B: ClassTag](name: String)(implicit lensNameForJavascript: LensNameForJavascript[A, B], interfaceToImplName: InterfaceToImplName): LensDefnFromProjection[A, B] =
    SimpleLensDefnFromProjection(lensNameForJavascript(name, false), List(new ChildLensDefn[A](name), new ViewLensDefn(interfaceToImplName.impl(ClassTags.clazz[B]).toLowerCase)))
  def list[A: ClassTag, B: ClassTag](name: String)(implicit lensNameForJavascript: LensNameForJavascript[A, B]): LensDefnFromProjection[A, B] = {
    SimpleLensDefnFromProjection(lensNameForJavascript(name, true), List(new ChildLensDefn[A](name), new ListLensDefn()));
  }
}
