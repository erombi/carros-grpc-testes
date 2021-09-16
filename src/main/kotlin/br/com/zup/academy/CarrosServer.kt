package br.com.zup.academy

import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarrosServer(
    val repository: CarroRepository
) : CarrosGrpcServiceGrpc.CarrosGrpcServiceImplBase() {

    override fun adicionar(request: CarrosRequest, responseObserver: StreamObserver<CarrosResponse>?) {
        if(repository.existsByPlaca(request.placa)) {
            responseObserver?.onError(Status.ALREADY_EXISTS
                .withDescription("Placa já cadastrada !")
                .asRuntimeException())

            return
        }

        val carro = Carro(request.modelo, request.placa)

        try {
            val response = repository.save(carro).let {
                CarrosResponse.newBuilder()
                    .setId(it.id!!)
                    .build()
            }

            responseObserver?.onNext(response)
            responseObserver?.onCompleted()

        } catch (e: ConstraintViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT
                .withDescription("Dados de entrada inválidos!")
                .asRuntimeException())
            return
        }

    }
}