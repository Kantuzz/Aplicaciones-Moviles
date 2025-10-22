package cl.duoc.evalua.data.repo

import cl.duoc.evalua.core.ServiceLocator
import cl.duoc.evalua.data.local.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.UUID

class MesasRepository {
    private val dao = ServiceLocator.db.mesaDao()

    fun observe(): Flow<List<MesaEntity>> = dao.observeAll()

    suspend fun upsert(nombre: String) {
        dao.upsert(MesaEntity(id = UUID.randomUUID(), nombre = nombre.trim()))
    }

    suspend fun upsertEntity(m: cl.duoc.evalua.data.local.MesaEntity) =
        cl.duoc.evalua.core.ServiceLocator.db.mesaDao().upsert(m)
}

class CriteriosRepository {
    private val dao = ServiceLocator.db.criterioDao()
    fun observeTodos() = dao.observeTodos()
    fun observeActivos() = dao.observeActivos()
    suspend fun upsert(c: CriterioEntity) = dao.upsert(c)
}

class JornadasRepository {
    private val dao = ServiceLocator.db.jornadaDao()
    suspend fun abrir(mesaId: UUID, fecha: String): JornadaEntity {
        val j = JornadaEntity(
            id = UUID.randomUUID(),
            mesaId = mesaId,
            fecha = fecha,
            inicio = Instant.now(),
            estado = "ACTIVA"
        )
        dao.upsert(j); return j
    }
    suspend fun cerrar(j: JornadaEntity) = dao.update(j.copy(fin = Instant.now(), estado = "CERRADA"))
}

class EvaluacionesRepository {
    private val dao = ServiceLocator.db.evaluacionDao()

    fun observePorMesaYRango(mesaId: UUID, desde: Instant, hasta: Instant) =
        dao.observePorMesaYRango(mesaId, desde, hasta)

    suspend fun guardar(
        mesaId: UUID,
        jornadaId: UUID?,
        comentario: String?,
        snapshot: List<Pair<String, Int?>>,   // (criterioNombre, pesoSnapshot)
        valores: Map<String, Int>             // criterioNombre -> 1..5
    ) {
        val evaId = UUID.randomUUID()
        dao.insertEval(
            EvaluacionEntity(
                id = evaId, mesaId = mesaId, jornadaId = jornadaId,
                timestamp = Instant.now(), comentario = comentario
            )
        )
        val detalles = snapshot.map { (nombre, peso) ->
            EvaluacionDetalleEntity(
                evaluacionId = evaId,
                criterioNombre = nombre,
                pesoSnapshot = peso,
                valor = valores[nombre] ?: 0
            )
        }
        dao.insertDetalles(detalles)
    }
}
