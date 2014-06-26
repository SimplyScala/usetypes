package usetypes

import scala.concurrent.Future

trait Repository[ResultWrapper[_], AggregateRoot] {
	def create(root: AggregateRoot): ResultWrapper[String]
	def retrieve(id: String): ResultWrapper[Option[AggregateRoot]]
	def retrieveAll(): ResultWrapper[Set[AggregateRoot]]
	def update(root: AggregateRoot): ResultWrapper[ServerResponse]
	def delete(root: AggregateRoot): ResultWrapper[ServerResponse]
}

trait ServerResponse

object SyncResultWrapperModule {
	type SyncResult[X] = X
}

object AsyncResultWrapperModule {
	type AsyncResult[X] = Future[X]
}

/**
 * https://groups.google.com/forum/#!topic/paris-scala-user-group/VxhUOheC0vQ
 * http://www.chuusai.com/2011/06/09/scala-union-types-curry-howard/
 * http://rapture.io/exceptions
 */