package br.com.zup.academy

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
internal class CarrosServerTest(
    val grpcClient : CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub,
    val repository: CarroRepository
) {

    @Test
    fun `deve cadastrar novo carro`() {

        repository.deleteAll()

        val response = grpcClient.adicionar(
            CarrosRequest.newBuilder()
                .setModelo("Gol")
                .setPlaca("ABC-1234")
                .build()
        )

        with(response) {
            Assertions.assertNotNull(id)
            Assertions.assertTrue(repository.existsById(id))
        }

    }

    @Test
    fun `nao deve adicionar novo carro quando carro com placa ja existe`() {

        repository.deleteAll()
        repository.save(Carro("Gol", "ABC-1234"))


        val error = assertThrows<StatusRuntimeException> {
            val response = grpcClient.adicionar(
                CarrosRequest.newBuilder()
                    .setModelo("Gol")
                    .setPlaca("ABC-1234")
                    .build()
            )
        }

        with(error) {
            Assertions.assertEquals(Status.ALREADY_EXISTS.code, status.code)
            Assertions.assertEquals("Placa já cadastrada !", status.description)
        }
    }

    @Test
    fun `nao deve adicionar novo carro quando dados de entrada forem invalidos`() {
        repository.deleteAll()

        val error = assertThrows<StatusRuntimeException> {
            val response = grpcClient.adicionar(
                CarrosRequest.newBuilder()
                    .build()
            )
        }

        with(error) {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            Assertions.assertEquals("Dados de entrada inválidos!", status.description)
            println(this)
        }
    }

}

@Factory
class CarrosGrpcClientFactory {

    @Singleton
    fun carrosClientGrpc(@GrpcChannel(GrpcServerChannel.NAME) channel : ManagedChannel) : CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub {
        return CarrosGrpcServiceGrpc.newBlockingStub(channel);
    }

}