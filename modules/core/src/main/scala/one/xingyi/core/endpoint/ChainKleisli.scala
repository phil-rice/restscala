package one.xingyi.core.endpoint
import one.xingyi.core.functions.Monoid
import one.xingyi.core.http.{Failer, ServiceRequest, ServiceResponse}
import one.xingyi.core.monad.MonadCanFail
import one.xingyi.core.language.MonadFunctionLanguage._

import scala.language.higherKinds

trait ChainKleisli[M[_], Fail] {
  protected implicit def monad: MonadCanFail[M, Fail]

  protected def failer: Failer[Fail]

  def chain(chains: (ServiceRequest => M[Option[ServiceResponse]])*): ServiceRequest => M[Option[ServiceResponse]] = { serviceRequest: ServiceRequest =>
    chains.foldLeft[M[Option[ServiceResponse]]](monad.liftM(Option.empty[ServiceResponse])) {
      case (acc, v) => acc.flatMap[Option[ServiceResponse]] {
        _ match {
          case s if s.isDefined => monad.liftM(s)
          case none => v match {
            case pf: PartialFunction[ServiceRequest, _] => if (pf.isDefinedAt(serviceRequest)) v(serviceRequest) else monad.liftM(None)
            case _ => v(serviceRequest)
          }
        }
      }
    }
  }
}
