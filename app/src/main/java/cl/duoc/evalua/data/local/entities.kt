package cl.duoc.evalua.data.local

import androidx.room.*
import java.time.Instant
import java.util.UUID

@Entity(tableName = "mesas")
data class MesaEntity(
    @PrimaryKey val id: UUID,
    val nombre: String,
    val activa: Boolean = true,
    val createdAt: Instant = Instant.now()
)

@Entity(tableName = "criterios")
data class CriterioEntity(
    @PrimaryKey val id: UUID,
    val nombre: String,
    val peso: Int? = null, // 0..100 (opcional)
    val orden: Int = 0,
    val activo: Boolean = true
)

@Entity(
    tableName = "jornadas",
    indices = [Index("mesaId")]
)
data class JornadaEntity(
    @PrimaryKey val id: UUID,
    val mesaId: UUID,
    val fecha: String,        // "AAAA-MM-DD"
    val inicio: Instant,
    val fin: Instant? = null,
    val estado: String = "ACTIVA" // ACTIVA o CERRADA
)

@Entity(
    tableName = "jornada_participantes",
    indices = [Index("jornadaId")]
)
data class JornadaParticipanteEntity(
    @PrimaryKey val id: UUID,
    val jornadaId: UUID,
    val nombre: String,
    val rut: String, // normalizado
    val presente: Boolean = true
)

@Entity(
    tableName = "evaluaciones",
    indices = [Index("mesaId"), Index("jornadaId")]
)
data class EvaluacionEntity(
    @PrimaryKey val id: UUID,
    val mesaId: UUID,
    val jornadaId: UUID?, // puede ser null si no usas jornadas
    val timestamp: Instant,
    val comentario: String? = null
)

@Entity(
    tableName = "evaluacion_detalle",
    primaryKeys = ["evaluacionId", "criterioNombre"]
)
data class EvaluacionDetalleEntity(
    val evaluacionId: UUID,
    val criterioNombre: String, // snapshot del nombre
    val pesoSnapshot: Int? = null,
    val valor: Int // 1..5
)
