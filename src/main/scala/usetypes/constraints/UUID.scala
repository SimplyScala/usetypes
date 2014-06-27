package usetypes.constraints

import scalaz.ValidationNel


/*sealed trait UUIDModule {
	type UUID = ImplemUUID
	private[UUID] case class ImplemUUID(value: String)
}*/

object UUID extends StringConstraints/* extends UUIDModule*/ {
	import scalaz.Scalaz
	//import Scalaz._
	//import scalaz.Validation

	type UUID = ImplemUUID
	private[UUID] case class ImplemUUID(value: String)

	val requiredSize = 4

	def apply(value: String): ValidationNel[ErrorMessage, UUID] = {
		requiredSizeString(requiredSize)(value).map { ImplemUUID(_) }
		/*if(value.size == requiredSize) ImplemUUID(value).success
		else ErrorMessage(s"the UUID have a size of ${value.size} instead of required size of $requiredSize").fail*/
	}
}