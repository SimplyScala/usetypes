package usetypes

import org.scalatest.{FunSuite, Matchers}
import usetypes.constraints.{StringConstraints, ErrorMessage}
import scalaz.Scalaz._
import scalaz._

class TypeValidationTest extends FunSuite with Matchers with StringConstraints {
	test("validation api example") {
		val error = ErrorMessage("error").failureNel[Option[String]]
		val ok = (str: String) => Option(str).successNel[ErrorMessage]

		(ok("ok") |@| error) {(a,b) => a} shouldBe error
		(ok("ok") |@| ok("coucou")) { (s1,s2) => s1.get.size + s2.get.size } shouldBe (2+6).success
	}
	
	test("single constraint") {
		minSizedString(5)("toto") shouldBe ErrorMessage(s"[toto] has not the minimum required size of 5").failNel
		maxSizedString(3)("toto") shouldBe ErrorMessage(s"the size of [toto] is greater than the required maximum size of 3").failNel
	}

	test("composing succeed constraints") {
		compose(minSizedString(3), maxSizedString(5))("toto") shouldBe "toto".successNel
	}

	test("composing failed constraints") {
		compose(minSizedString(3), maxSizedString(5))("ab") shouldBe ErrorMessage(s"[ab] has not the minimum required size of 3").failNel
	}

	test("gCompose failed constraints") {
		val expectedFailure = Failure(NonEmptyList(ErrorMessage("[ab] has not the minimum required size of 3"),
									  ErrorMessage("[ab] does not start with regex (c)")))

		gCompose(minSizedString(3), startedWithString("c"))("ab") shouldBe expectedFailure
	}

	test("gCompose succeed constraints") {
		gCompose(minSizedString(3), maxSizedString(5))("toto") shouldBe "toto".successNel
	}
}