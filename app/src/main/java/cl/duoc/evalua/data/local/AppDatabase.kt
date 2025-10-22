package cl.duoc.evalua.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        MesaEntity::class,
        CriterioEntity::class,
        JornadaEntity::class,
        JornadaParticipanteEntity::class,
        EvaluacionEntity::class,
        EvaluacionDetalleEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mesaDao(): MesaDao
    abstract fun criterioDao(): CriterioDao
    abstract fun jornadaDao(): JornadaDao
    abstract fun evaluacionDao(): EvaluacionDao
}
