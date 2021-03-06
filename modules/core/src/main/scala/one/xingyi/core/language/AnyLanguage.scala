/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.language

import java.text.MessageFormat
import java.util.concurrent.atomic.AtomicInteger

import one.xingyi.core.functions._
import one.xingyi.core.monad._

import scala.util.Try
import scala.language.higherKinds
import scala.reflect.ClassTag

object AnyLanguage extends AnyLanguage
trait AnyLanguage {
  case class use[P, X](thingMaker: P => X) {
    def apply[R](fn: X => R): P => R = { p => fn(thingMaker(p)) }
  }

  def using[T, T1](t: T)(fn: T => T1): T1 = fn(t)

  def toSome[X](x: X): Option[X] = Some(x)

  def withValue[X, Y](x: X)(fn: X => Y): Y = fn(x)

  def sideeffect[X, Y](x: X)(fn: X => Y): X = {fn(x); x}
  def sideeffectAll[From, To](seq: ((From, To) => Unit)*): From => To => To = { from => to => seq.foreach(_.apply(from, to)); to }


  implicit class AnyOps[T](t: => T) {
    def |>[T2](fn: T => T2) = fn(t)
    def |+>[T1](fn: T => T => T1): T1 = fn(t)(t)
    def |?[M[_], Failure](validation: T => Seq[Failure])(implicit withFailure: MonadCanFail[M, Failure], monoid: Monoid[Failure]): M[T] =
      validation(t) match {
        case Nil => t.liftM
        case s => withFailure.fail(monoid.addAll(s))
      }
    def liftM[M[_]](implicit container: Liftable[M]): M[T] = container.liftM(t)
    def liftResultAndPut[M[_], V](localVariable: LocalVariable[V], v: V)(implicit monad: MonadWithState[M]): M[T] = monad.liftMAndPut(t, localVariable, v)
    def liftException[M[_], T1](implicit async: MonadWithException[M], ev: T <:< Throwable): M[T1] = async.exception(t)
    def ifError(fn: Exception => T): T = try {
      t
    } catch {
      case e: Exception => fn(e)
    }
    def sideeffectTry(fn: Try[T] => Unit): Try[T] = {
      val triedT = Try(t)
      fn(triedT)
      triedT
    }
    def sideeffectIfIs[Sub <: T](fn: Sub => _)(implicit classTag: ClassTag[Sub]) = if (classTag.runtimeClass.isAssignableFrom(t.getClass)) fn(t.asInstanceOf[Sub])

    def sideeffect[X](block: T => Unit) = {
      val result = t
      block(result)
      result
    }
  }

  implicit class BooleanOps(boolean: Boolean) {
    def toOption[T](value: => T): Option[T] = if (boolean) Some(value) else None
  }


  implicit class TryOps[T](tryT: Try[T]) {
    def liftTry[M[_]](implicit monadWithException: MonadWithException[M]): M[T] = tryT.fold(monadWithException.exception, monadWithException.liftM)
  }

  implicit class AtomicIntegerOps(a: AtomicInteger) {
    def tick(size: Int)(fn: => Unit): Unit = {
      if (a.updateAndGet { old => if (old >= size - 1) 0 else old + 1 } == 0) fn
    }
    def ifNotZero(fn: => Unit): Unit = {
      if (a.get != 0) fn
    }
  }

  implicit class ListOps[X](s: List[X]) {
    def asString(fn: X => String, separator: String = ","): String = s.map(fn).mkString(separator)
    def foldLeftWithOptions[Acc](initial: Acc)(foldFn: (Acc, X) => Option[Acc]): Option[Acc] =
      s.foldLeft[Option[Acc]](Some(initial)) { case (Some(acc), v) => foldFn(acc, v); case _ => None }
    def foldLeftWithOptionsEatingExceptions[Acc](initial: Acc)(foldFn: (Acc, X) => Option[Acc]): Option[Acc] =
      foldLeftWithOptions(initial) { (acc, v) => try foldFn(acc, v) catch {case e: Exception => None} }
    def +(opt: Option[X]): List[X] = opt.fold(s)(_ :: s)
  }
}
