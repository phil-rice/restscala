/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.endpoint

import one.xingyi.core.http._
import one.xingyi.core.json.{JsonWriter, ToJsonLib}
import one.xingyi.core.language.Language._
import one.xingyi.core.monad.{LocalVariable, Monad, MonadCanFail, MonadWithState}
import one.xingyi.core.objectify.{RecordedCall, ResultWithRecordedCalls}
import one.xingyi.core.strings.{Strings, ToHtml}

import scala.language.higherKinds
import scala.reflect.ClassTag

trait EndpointKleisli[M[_]] {
  protected implicit def monad: Monad[M]

  def endpoint[Req: ClassTag, Res: ClassTag](normalisedPath: String, matchesServiceRequest: MatchesServiceRequest, debug: Boolean = false)(raw: Req => M[Res])
                                            (implicit fromServiceRequest: FromServiceRequest[M, Req], toServiceResponse: ToServiceResponse[Req, Res]): ServiceRequest => M[Option[ServiceResponse]] =
    EndPoint(normalisedPath, matchesServiceRequest, debug)(raw)
}






case class EndPoint[M[_] : Monad, Req, Res](normalisedPath: String, matchesServiceRequest: MatchesServiceRequest, debug: Boolean)(kleisli: Req => M[Res])
                                           (implicit fromServiceRequest: FromServiceRequest[M, Req],
                                            toServiceResponse: ToServiceResponse[Req, Res],
                                           ) extends PartialFunction[ServiceRequest, M[Option[ServiceResponse]]] {
  def debugInfo(req: ServiceRequest) = s"Endpoint($normalisedPath, $matchesServiceRequest) called with $req results in ${isDefinedAt(req)}"

  override def apply(serviceRequest: ServiceRequest): M[Option[ServiceResponse]] = {
    if (debug) {
      println(s"In endpoint $this ServiceRequest is $serviceRequest")
    }
    if (isDefinedAt(serviceRequest))
      (fromServiceRequest |==> (kleisli |=+> toServiceResponse) |=> toSome) (serviceRequest)
    else
      Option.empty[ServiceResponse].liftM
  }

  def isDefinedAt(serviceRequest: ServiceRequest): Boolean = {
    val result = matchesServiceRequest(normalisedPath)(serviceRequest)
    if (debug) {
      println(s"In endpoint evaluation 'isDefinedAt''  $result $this Normalised Path is $normalisedPath ServiceRequest is $serviceRequest")
    }
    result
  }

  override def toString() = s"Endpoint($normalisedPath, $matchesServiceRequest)"
}



