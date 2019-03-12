package one.xingyi.core.simpleList

object ISimpleList {
  def empty[T]: ISimpleList[T] = SimpleList()
  def apply[T](t: T*): SimpleList[T] = SimpleList(List(t: _*))
}

trait ISimpleList[T] extends Iterable[T] {
  def size: Int
  def apply(n: Int): T
  def withItem(n: Int, t: T): ISimpleList[T]
  def append(t: T): ISimpleList[T]
}

case class SimpleList[T](list: List[T] = List()) extends ISimpleList[T] {
  override def size: Int = list.size
  def apply(n: Int): T = list(n)
  def withItem(n: Int, t: T): ISimpleList[T] = SimpleList(list.updated(n, t))
  def append(t: T): ISimpleList[T] = SimpleList(list :+ t)
  override def iterator(): Iterator[T] = list.iterator
}
