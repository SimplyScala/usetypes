package usetypes

import org.scalatest.{Matchers, FunSuite}
import usetypes.AsyncResultWrapperModule.AsyncResult
import usetypes.SyncResultWrapperModule.SyncResult

import scala.concurrent.Future

class RepositoryTest extends FunSuite with Matchers {
	trait User                       // a stub aggregate root to simulate repository

	test("a sync repository") {
		object UserSyncRepository extends Repository[SyncResult, User] {
			override def create(root: User): String = "ok"
			override def update(root: User): ServerResponse = ???
			override def retrieveAll(): Set[User] = ???
			override def delete(root: User): ServerResponse = ???
			override def retrieve(id: String): Option[User] = ???
		}

		UserSyncRepository.create(null) shouldBe "ok"
	}

	test("an async repository") {
		import concurrent.ExecutionContext.Implicits.global
		import concurrent.Await.result
		import concurrent.duration._

		object UserAsyncRepository extends Repository[AsyncResult, User] {
			override def create(root: User): Future[String] = Future("ok")
			override def update(root: User): Future[ServerResponse] = ???
			override def retrieveAll(): Future[Set[User]] = ???
			override def delete(root: User): Future[ServerResponse] = ???
			override def retrieve(id: String): Future[Option[User]] = ???
		}

		result(UserAsyncRepository.create(null), 1 second) shouldBe "ok"
	}
}