/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core

import java.io.ByteArrayOutputStream

import org.mockito.ArgumentCaptor
import org.scalatest.concurrent.Eventually
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpecLike, Matchers}
import one.xingyi.core.logging.NullLoggingAdapter

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.{higherKinds, postfixOps}
import scala.reflect.ClassTag

trait UtilsSpec extends FlatSpecLike with Matchers with MockitoSugar with Eventually {

  def await[X](f: Future[X]) = Await.result(f, 5 seconds)

  def captorFor[C: ClassTag] = ArgumentCaptor.forClass(implicitly[ClassTag[C]].runtimeClass).asInstanceOf[ArgumentCaptor[C]]

  def compareSequences(a: Seq[_], b: Seq[_]) = {
    for (((a, b), i) <- a.zipAll(b, null, null).zipWithIndex) {
      withClue(s"Item $i") {
        a shouldBe b
      }
    }
  }
  implicit class StringOps(s: String) {
    def noWhiteSpace = s.replaceAll("\\se*", "")
  }

  def capturePrintln[X](block: => X): (X, String) = {
    //    val stream = new
    val stream = new ByteArrayOutputStream()
    val x= Console.withOut(stream)(block)
    (x, stream.toString)
  }


}

trait UtilsWithLoggingSpec extends UtilsSpec {
  implicit val loggingAdapter = NullLoggingAdapter
}

//class UtilsWithExecutionContextSpec extends UtilsSpec {
//  implicit val mdc: MDCPropagatingExecutionContext = ExecutionContext.global
//}
