package usetypes.constraints

import scalaz.Scalaz._
import scalaz.{Failure, Success, ValidationNel}

/**
* source :
*      http://danielwestheide.com/blog/2013/01/23/the-neophytes-guide-to-scala-part-10-staying-dry-with-higher-order-functions.html
*      http://johnkurkowski.com/posts/accumulating-multiple-failures-in-a-ValidationNEL/
*/
trait GenericTypeConstraints {
	type ConstraintValidation[T] = T => ValidationNel[ErrorMessage, T]

	def gCompose[V](constraints: ConstraintValidation[V]*): ConstraintValidation[V] = { toValidate: V =>
		constraints
			.map    { constraint => constraint(toValidate) }
			.reduce { (a,b) => (a |@| b) {(e,f) => e} }  /** la partie entre {} <=> apply method */
	}                                                    /** et n'est éxécuté que si toutes les validations sont Success */
}

trait StringConstraints extends GenericTypeConstraints {
	type StringValidations = ConstraintValidation[String]

	def compose(validations: StringValidations*): StringValidations = {  value: String =>
		validations
			.map    { constraint => constraint(value) }
			.reduce { (a,b) => (a |@| b).apply {(a,b) => a} }
	}

	type MaxSize = Int
	type MinSize = Int
	type RequiredSize = Int
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

	val requiredSizeString: RequiredSize => StringValidations = requiredSize => str =>
		gCompose(minSizedString(requiredSize), maxSizedString(requiredSize))(str) match {
			case s@Success(_) => s
			case Failure(_) => ErrorMessage(s"string [$str] have a size of ${str.size} instead of required size of $requiredSize").failNel
		}
}