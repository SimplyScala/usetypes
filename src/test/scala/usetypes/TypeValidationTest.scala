package usetypes

import org.scalatest.{FunSuite, Matchers}
import scalaz.Scalaz._
import scalaz._

class TypeValidationTest extends FunSuite with Matchers with GenericTypeConstraints {
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

/**
 * source :
 *      http://danielwestheide.com/blog/2013/01/23/the-neophytes-guide-to-scala-part-10-staying-dry-with-higher-order-functions.html
 *      http://johnkurkowski.com/posts/accumulating-multiple-failures-in-a-ValidationNEL/
 */
trait TypeConstraints {
	import scalaz.ValidationNel
	import scalaz.Scalaz._

	type ConstraintValidation[T] = T => ValidationNel[ErrorMessage, T]
	type StringValidations = ConstraintValidation[String]

	def compose(validations: StringValidations*): StringValidations = {  value: String =>
		validations
			.map    { constraint => constraint(value) }
			.reduce { (a,b) => (a |@| b).apply {(a,b) => a} }
	}

	type MaxSize = Int
	type MinSize = Int
	type StartWithRegex = String

	val startedWithString: StartWithRegex => StringValidations = startRegex => str =>
		if(str.startsWith(startRegex)) str.successNel
		else ErrorMessage(s"[$str] does not start with regex ($startRegex)").failNel
	
	val minSizedString: MinSize => StringValidations = minSize => str =>
		if(str.size >= minSize) str.successNel
		else ErrorMessage(s"[$str] has not the minimum required size of $minSize").failNel

	val maxSizedString: MaxSize => StringValidations = maxSize => str =>
		if(str.size <= maxSize) str.successNel
		else ErrorMessage(s"the size of [$str] is greater than the required maximum size of $maxSize").failNel
}

trait GenericTypeConstraints extends TypeConstraints {
	def gCompose[V](constraints: ConstraintValidation[V]*): ConstraintValidation[V] = { toValidate: V =>
		constraints
			.map    { constraint => constraint(toValidate) }
			.reduce { (a,b) => (a |@| b) {(e,f) => e} }  /** la partie entre {} <=> apply method */ 
	}                                                    /** et n'est éxécuté que si toutes les validations sont Success */
}

case class ErrorMessage(message: String)