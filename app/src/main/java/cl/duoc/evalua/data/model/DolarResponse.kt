package cl.duoc.evalua.data.model

data class DolarItem(
    val fecha: String,
    val valor: Double
)

data class DolarResponse(
    val version: String,
    val autor: String,
    val codigo: String,
    val nombre: String,
    val unidad_medida: String,
    val serie: List<DolarItem>
)