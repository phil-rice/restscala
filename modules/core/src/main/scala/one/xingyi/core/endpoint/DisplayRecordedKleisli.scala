/** Copyright (c) 2019, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.core.endpoint
import one.xingyi.core.http.{Body, ContentType, ServiceRequest, ServiceResponse}
import one.xingyi.core.json.JsonWriter
import one.xingyi.core.monad.Monad
import one.xingyi.core.objectify.{RecordedCall, ResultWithRecordedCalls}
import one.xingyi.core.strings.ToHtml
import one.xingyi.core.language.MonadFunctionLanguage._

import scala.language.higherKinds

trait DisplayRecordedKleisli[M[_]] {
  protected implicit def monad: Monad[M]

  def recordedCallToString(recordedCall: RecordedCall) =
    s"""Request: ${recordedCall.req.method}${recordedCall.req.path} ${recordedCall.req.headers.mkString(",")}
       |Result: ${recordedCall.res.status.code}
       |${recordedCall.res.body.s}
       |${recordedCall.res.headers.mkString(",")}
     """.stripMargin

  def andDisplayRecorded[J](raw: ServiceRequest => M[Option[ServiceResponse]])(implicit jsonWriter: JsonWriter[J],
                                                                               recordedCalls: InheritableThreadLocal[Seq[RecordedCall]],
                                                                               toHtml: ToHtml[ResultWithRecordedCalls[ServiceResponse]]): ServiceRequest => M[Option[ServiceResponse]] = {
    req =>
      raw(req).map {
        case Some(sr) => Some(ServiceResponse(sr.status, Body(toHtml(ResultWithRecordedCalls(sr, recordedCalls.get))), ContentType("text/html")))
        case None => None
      }
  }

}
