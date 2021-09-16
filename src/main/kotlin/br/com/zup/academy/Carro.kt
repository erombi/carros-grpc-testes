package br.com.zup.academy

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Carro(
    modelo: String,
    placa: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @NotBlank
    val modelo = modelo

    @NotBlank
    val placa = placa

}
