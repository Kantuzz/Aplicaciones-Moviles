package cl.duoc.evalua.data.remote

import cl.duoc.evalua.data.model.DolarResponse
import retrofit2.http.GET

interface ApiService {

    // https://mindicador.cl/api/dolar
    @GET("dolar")
    suspend fun getDolar(): DolarResponse
}