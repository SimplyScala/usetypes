package usetypes.constraints

import org.scalatest.{FunSuite, Matchers}

import scalaz.Scalaz._
import scalaz.{Failure, Success, ValidationNel}

class UserTest extends FunSuite with Matchers {
	test("create an User") {
		val user: ValidationNel[ErrorMessage, User] = userBuilder("toto", "bourdon", "ugo", 32)

		user.map { _.uuid.value } shouldBe "toto".success
	}

	test("fail to create an User") {
		val user: ValidationNel[ErrorMessage, User] = userBuilder("totoX", "bourdon", "ugo", 32)

		user shouldBe ErrorMessage(s"string [totoX] have a size of 5 instead of required size of 4").failNel
	}

	def userBuilder(uuid: String, name: String, surName: String, age: Int): ValidationNel[ErrorMessage, User] = {
		UUID(uuid) match {
			case Success(x) => User(x, "bourdon", "ugo", 32).successNel
			case Failure(e) => e.fail
		}
	}
}