package cl.duoc.evalua.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.UUID

@Dao
interface MesaDao {
    @Query("SELECT * FROM mesas ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MesaEntity>>

    @Query("SELECT * FROM mesas WHERE id = :id")
    suspend fun getById(id: UUID): MesaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mesa: MesaEntity)

    @Delete suspend fun delete(mesa: MesaEntity)
}

@Dao
interface CriterioDao {
    @Query("SELECT * FROM criterios WHERE activo = 1 ORDER BY orden ASC")
    fun observeActivos(): Flow<List<CriterioEntity>>

    @Query("SELECT * FROM criterios ORDER BY orden ASC")
    fun observeTodos(): Flow<List<CriterioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg c: CriterioEntity)

    @Delete suspend fun delete(c: CriterioEntity)
}

@Dao
interface JornadaDao {
    @Query("SELECT * FROM jornadas WHERE mesaId = :mesaId AND estado = 'ACTIVA' LIMIT 1")
    suspend fun getActiva(mesaId: UUID): JornadaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(j: JornadaEntity)

    @Update suspend fun update(j: JornadaEntity)
}

data class EvaluacionConDetalle(
    @Embedded val eva: EvaluacionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "evaluacionId"
    )
    val detalles: List<EvaluacionDetalleEntity>
)

@Dao
interface EvaluacionDao {
    @Transaction
    @Query("""
        SELECT * FROM evaluaciones 
        WHERE mesaId = :mesaId AND timestamp BETWEEN :desde AND :hasta 
        ORDER BY timestamp DESC
    """)
    fun observePorMesaYRango(
        mesaId: UUID,
        desde: Instant,
        hasta: Instant
    ): Flow<List<EvaluacionConDetalle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEval(e: EvaluacionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalles(detalles: List<EvaluacionDetalleEntity>)
}
