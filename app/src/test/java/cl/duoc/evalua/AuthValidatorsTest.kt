package cl.duoc.evalua

import cl.duoc.evalua.viewmodel.validarEmailDocente
import cl.duoc.evalua.viewmodel.validarPassword
import org.junit.Assert.*
import org.junit.Test

class AuthValidatorsTest {

    @Test
    fun email_en_blanco_da_error() {
        val result = validarEmailDocente("")
        assertEquals("Ingresa tu correo institucional", result)
    }

    @Test
    fun email_con_dominio_incorrecto_da_error() {
        val result = validarEmailDocente("docente@gmail.com")
        assertEquals("Debe ser @profesor.duoc.cl", result)
    }

    @Test
    fun email_valido_no_da_error() {
        val result = validarEmailDocente("juan.perez@profesor.duoc.cl")
        assertNull(result)
    }

    @Test
    fun password_corta_da_error() {
        val result = validarPassword("123")
        assertEquals("Mínimo 6 caracteres", result)
    }

    @Test
    fun password_valida_no_da_error() {
        val result = validarPassword("123456")
        assertNull(result)
    }
}