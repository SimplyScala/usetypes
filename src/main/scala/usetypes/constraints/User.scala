package usetypes.constraints


import UUID.UUID

case class User(uuid: UUID,
                name: String,
                surName: String,
                age: Int)