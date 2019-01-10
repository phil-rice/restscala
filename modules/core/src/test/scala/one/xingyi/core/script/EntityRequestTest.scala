/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.script
import one.xingyi.core.UtilsSpec
import one.xingyi.core.http._
import one.xingyi.core.json.{JsonParser, JsonWriter, ObjectProjection}
import one.xingyi.core.monad.IdentityMonad

import scala.util.Failure

abstract class EntityRequestTest[J: JsonParser: JsonWriter, SharedE, DomainE](implicit projection: ObjectProjection[SharedE, DomainE]) extends UtilsSpec {

  import EditEntityRequestFailer.EditEntityRequestFailerForThrowable

  behavior of "Entity Request"

  val srWithHost = ServiceRequest(Get, Uri("/some/uri/someid"), List(Header("host", "someHost")), None)
  val srNoHost = ServiceRequest(Get, Uri("/some/uri/someid"), List(), None)
  val fromSr = implicitly[FromServiceRequest[IdentityMonad, EntityRequest]]

  it should "be gettable from a Service request" in {
    fromSr(srWithHost).value.get shouldBe EntityRequest("someid", "someHost")
  }

  it should "have a fail if no host" in {
    val Failure(e) = fromSr(srNoHost).value
    e.getMessage shouldBe s"No host in the request\n$srNoHost"
  }

}
