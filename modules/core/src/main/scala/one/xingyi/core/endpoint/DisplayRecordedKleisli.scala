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
