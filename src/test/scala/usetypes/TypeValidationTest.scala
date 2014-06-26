package usetypes

import org.scalatest.{FunSuite, Matchers}
import scalaz.Scalaz._

class TypeValidationTest extends FunSuite with Matchers with TypeConstraints {
	test("single rules") {
		minSizedString(5)("toto") shouldBe ErrorMessage(s"[toto] has not the minimum required size of 5").failNel
		maxSizedString(3)("toto") shouldBe ErrorMessage(s"the size of [toto] is greater than the required maximum size of 3").failNel
	}

	test("success composing rules") {
		compose(minSizedString(3), maxSizedString(5))("toto") shouldBe "toto".successNel
	}

	test("failure composing rules") {
		compose(minSizedString(3), maxSizedString(5))("ab") shouldBe ErrorMessage(s"[ab] has not the minimum required size of 3").failNel
	}
}

/**
 * source :
 *      http://danielwestheide.com/blog/2013/01/23/the-neophytes-guide-to-scala-part-10-staying-dry-with-higher-order-functions.html
 */
trait TypeConstraints {
	import scalaz.ValidationNel
	import scalaz.Scalaz._

	type StringValidations = String => ValidationNel[ErrorMessage, String]

	def compose(validations: StringValidations*): StringValidations = {  value: String =>
		val results = validations.map { constraint => constraint(value) }
		val	result = results.reduce { (a,b) => (a |@| b) {_ + _} }

		if(result.isFailure) result else results.head
	}

	/*val strictSizedString: Int => StringValidations =
		(size, str) => if(str.size == size) str.success else ErrorMessage(s"error size ${str.size} instead of $size").failNel*/

	val minSizedString: Int => StringValidations = minSize => str =>
		if(str.size >= minSize) str.successNel
		else ErrorMessage(s"[$str] has not the minimum required size of $minSize").failNel

	val maxSizedString: Int => StringValidations = maxSize => str =>
		if(str.size <= maxSize) str.successNel
		else ErrorMessage(s"the size of [$str] is greater than the required maximum size of $maxSize").failNel
}

case class ErrorMessage(message: String)
