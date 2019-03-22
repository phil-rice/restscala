/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.optics

import one.xingyi.core.crypto.Codec

object Lens {
  implicit def identity[X] = Lens[X, X](a => a, (a, b) => b, name = Some("{identity}"))
  def cast[X, Y] = Lens[X, Y](a => a.asInstanceOf[Y], (a, b) => b.asInstanceOf[X])

  def itemInListL[T](n: Int): Lens[List[T], T] = Lens(_ (n), (ts, t) => ts.updated(n, t), name = Some("#" + n))
  def apply[A, B](get: A => B, set: (A, B) => A, name: Option[String] = None): Lens[A, B] = SimpleLens(get, set, name)
  def asListLens[A, B, T](lens: Lens[A, List[B]])(implicit codec: Codec[T,B]): Lens[A, List[T]] = ListLens(lens)
}
trait Lens[A, B] {
  def get: A => B
  def set: (A, B) => A
  def setFn: B => A => A = { b => a => set(a, b) }
  def apply(whole: A): B = get(whole)
  def map(a: A, f: B => B): A = set(a, f(get(a)))
  def map(f: B => B): A => A = a => set(a, f(get(a)))
  //  def compose[C](that: Lens[C, A]) = SimpleLens[C, B](c => get(that.get(c)), (c, b) => that.map(c, set(_, b)), name = Some(s"$that andThen ${Lens.this}"))
  def andThen[C](that: Lens[B, C]) = new ComposeLens(this, that)
  def andGet[C](fn: B => C) = get andThen fn
}

case class ListLens[A, B, T](lens: Lens[A, List[B]])(implicit codec: Codec[T,B]) extends Lens[A, List[T]] {
  override def get = a => lens.get(a).map(codec.backwards)
  override def set = (a, listt) => lens.set(a, listt.map(codec.forwards))
}

case class ComposeLens[A, B, C](first: Lens[A, B], second: Lens[B, C]) extends Lens[A, C] {
  val get = a => second.get(first.get(a))
  val set = (a, c) => first.set(a, second.set(first.get(a), c))
}
trait DelegateLens[A, B] extends Lens[A, B] {
  def lens: Lens[A, B]
  override def get: A => B = lens.get
  override def set: (A, B) => A = lens.set
}
case class SimpleLens[A, B](get: A => B, set: (A, B) => A, name: Option[String] = None) extends Lens[A, B] {
  override def toString() = name.fold(super.toString())(n => s"Lens $n end")
}

