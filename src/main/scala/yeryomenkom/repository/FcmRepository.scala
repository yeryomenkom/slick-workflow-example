package yeryomenkom.repository

import yeryomenkom.db.DbTablesAndModels.profile.api._
import yeryomenkom.db.DbTablesAndModels._

import scala.concurrent.{ExecutionContext, Future}

class FcmRepository(db: Database)(implicit val ec: ExecutionContext) {
  import db._

  def getFcmTokens(userIds: Iterable[Int]): Future[Seq[DbFcmToken]] = run {
    DbFcmTokenQuery.filter(_.userId inSet userIds).result
  }

}

