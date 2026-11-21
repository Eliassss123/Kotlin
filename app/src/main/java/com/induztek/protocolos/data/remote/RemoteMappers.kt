package com.induztek.protocolos.data.remote

import com.induztek.protocolos.model.Acta
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Instrumento
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.MomentoMedicion
import com.induztek.protocolos.model.ModalidadVisita
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import com.induztek.protocolos.model.SyncStatus
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.model.Visita

fun UserResponseDto.toProfile(): UserProfile = UserProfile(
    id = id,
    correo = email,
    nombre = name,
    perfilCodigo = perfilCodigo,
    empresaCliente = company,
    activo = active,
    fotoPerfil = photoUrl,
    biografia = biography
)

fun EquipoDto.toDomain(): Equipo = Equipo(
    id = id,
    codigo = codigo,
    tagCliente = tagCliente,
    tipo = tipo,
    cliente = cliente,
    ubicacion = ubicacion,
    marca = marca,
    modelo = modelo,
    numeroSerie = numeroSerie,
    potenciaTension = potenciaTension,
    anio = anio,
    tensiones = tensiones,
    tap = tap,
    conexion = conexion,
    ultimoEstado = EstadoProtocolo.fromLabel(ultimoEstado)
)

fun Equipo.toRequest(): EquipmentRequest = EquipmentRequest(
    codigo = codigo,
    tagCliente = tagCliente,
    tipo = tipo,
    cliente = cliente,
    ubicacion = ubicacion,
    marca = marca,
    modelo = modelo,
    numeroSerie = numeroSerie,
    potenciaTension = potenciaTension,
    anio = anio,
    tensiones = tensiones,
    tap = tap,
    conexion = conexion,
    ultimoEstado = ultimoEstado.label
)

fun MedicionDto.toDomain(): Medicion = Medicion(
    id = id ?: java.util.UUID.randomUUID().toString(),
    campo = campo,
    valorNumerico = valorNumerico?.toDoubleOrNull(),
    unidad = unidad,
    momento = momento?.let(MomentoMedicion::fromLabel) ?: MomentoMedicion.PREVIA,
    instrumentoInfo = instrumentoInfo.orEmpty()
)

fun Medicion.toDto(): MedicionDto = MedicionDto(
    id = id,
    campo = campo,
    valorNumerico = valorNumerico?.toString(),
    unidad = unidad,
    momento = momento.label,
    instrumentoInfo = instrumentoInfo
)

fun TestDto.toDomain(): Prueba = Prueba(
    id = id,
    protocoloId = protocoloId,
    ordenTrabajo = ordenTrabajo,
    equipoCodigo = equipoCodigo,
    tipoPrueba = tipoPrueba,
    ubicacion = ubicacion,
    fechaHora = fechaHora,
    estadoPrueba = EstadoPrueba.fromLabel(estadoPrueba),
    motivoNoRealizada = motivoNoRealizada,
    resultado = ResultadoPrueba.fromLabel(resultado),
    instrumentoId = instrumentoId,
    instrumento = instrumento,
    instrumentoSerie = instrumentoSerie,
    instrumentoCalibracion = instrumentoCalibracion,
    calibracionVigente = calibracionVigente,
    temperaturaAmbiente = temperaturaAmbiente,
    humedad = humedad,
    mediciones = mediciones.map(MedicionDto::toDomain),
    observaciones = observaciones,
    variacionHistoricaPorcentaje = variacionHistoricaPorcentaje,
    superaVariacion25 = superaVariacion25
)

fun Prueba.toDto(): TestDto = TestDto(
    id = id,
    protocoloId = protocoloId,
    ordenTrabajo = ordenTrabajo,
    equipoCodigo = equipoCodigo,
    tipoPrueba = tipoPrueba,
    ubicacion = ubicacion,
    fechaHora = fechaHora,
    estadoPrueba = estadoPrueba.label,
    motivoNoRealizada = motivoNoRealizada,
    resultado = resultado.label,
    instrumentoId = instrumentoId,
    instrumento = instrumento,
    instrumentoSerie = instrumentoSerie,
    instrumentoCalibracion = instrumentoCalibracion,
    calibracionVigente = calibracionVigente,
    temperaturaAmbiente = temperaturaAmbiente,
    humedad = humedad,
    mediciones = mediciones.map(Medicion::toDto),
    observaciones = observaciones,
    variacionHistoricaPorcentaje = variacionHistoricaPorcentaje,
    superaVariacion25 = superaVariacion25
)

fun ProtocolDto.toDomain(): Protocolo = Protocolo(
    id = id,
    codigoProtocolo = codigoProtocolo,
    visitaId = visitaId,
    ordenTrabajo = ordenTrabajo,
    equipoCodigo = equipoCodigo,
    tipoEquipo = tipoEquipo,
    fechaHora = fechaHora,
    estado = EstadoProtocolo.fromLabel(estado),
    resultadoGlobal = ResultadoPrueba.fromLabel(resultadoGlobal),
    tecnico = tecnico,
    tecnicoCreador = tecnicoCreador,
    descripcionMantenimiento = descripcionMantenimiento,
    instrumento = instrumento,
    calibracionVigente = calibracionVigente,
    temperaturaAmbiente = temperaturaAmbiente,
    humedad = humedad,
    actaEntregadaCliente = actaEntregadaCliente,
    formalizadoPor = formalizadoPor,
    fechaFormalizacion = fechaFormalizacion,
    syncStatus = SyncStatus.fromLabel(syncStatus),
    observaciones = observaciones,
    fotos = fotos,
    pruebas = pruebas.map(TestDto::toDomain),
    mediciones = mediciones.map(MedicionDto::toDomain)
)

fun Protocolo.toDto(): ProtocolDto = ProtocolDto(
    id = id,
    codigoProtocolo = codigoProtocolo,
    visitaId = visitaId,
    ordenTrabajo = ordenTrabajo,
    equipoCodigo = equipoCodigo,
    tipoEquipo = tipoEquipo,
    fechaHora = fechaHora,
    estado = estado.label,
    resultadoGlobal = resultadoGlobal.label,
    tecnico = tecnico,
    tecnicoCreador = tecnicoCreador,
    descripcionMantenimiento = descripcionMantenimiento,
    instrumento = instrumento,
    calibracionVigente = calibracionVigente,
    temperaturaAmbiente = temperaturaAmbiente,
    humedad = humedad,
    actaEntregadaCliente = actaEntregadaCliente,
    formalizadoPor = formalizadoPor,
    fechaFormalizacion = fechaFormalizacion,
    syncStatus = syncStatus.label,
    observaciones = observaciones,
    pruebas = pruebas.map(Prueba::toDto),
    mediciones = mediciones.map(Medicion::toDto),
    fotos = fotos
)

fun VisitDto.toDomain(): Visita = Visita(
    id = id,
    ordenTrabajo = ordenTrabajo,
    fecha = fecha,
    horaInicio = horaInicio,
    cliente = cliente,
    instalacion = instalacion,
    modalidad = ModalidadVisita.fromLabel(modalidad),
    ingenieroACargo = ingenieroACargo,
    tecnicos = tecnicos,
    temperaturaAmbiente = temperaturaAmbiente,
    humedad = humedad,
    cerrada = cerrada,
    syncStatus = SyncStatus.fromLabel(syncStatus)
)

fun Visita.toDto(): VisitDto = VisitDto(
    id = id,
    ordenTrabajo = ordenTrabajo,
    fecha = fecha,
    horaInicio = horaInicio,
    cliente = cliente,
    instalacion = instalacion,
    modalidad = modalidad.label,
    ingenieroACargo = ingenieroACargo,
    tecnicos = tecnicos,
    temperaturaAmbiente = temperaturaAmbiente,
    humedad = humedad,
    cerrada = cerrada,
    syncStatus = syncStatus.label
)

fun ActaDto.toDomain(): Acta = Acta(
    id = id,
    visitaId = visitaId,
    ordenTrabajo = ordenTrabajo,
    cliente = cliente,
    instalacion = instalacion,
    fechaEmision = fechaEmision,
    tecnicoEntrega = tecnicoEntrega,
    recibidoPor = recibidoPor,
    marcaPendienteFormalizacion = marcaPendienteFormalizacion,
    resumenProtocolos = resumenProtocolos,
    syncStatus = SyncStatus.fromLabel(syncStatus)
)

fun Acta.toDto(): ActaDto = ActaDto(
    id = id,
    visitaId = visitaId,
    ordenTrabajo = ordenTrabajo,
    cliente = cliente,
    instalacion = instalacion,
    fechaEmision = fechaEmision,
    tecnicoEntrega = tecnicoEntrega,
    recibidoPor = recibidoPor,
    marcaPendienteFormalizacion = marcaPendienteFormalizacion,
    resumenProtocolos = resumenProtocolos,
    syncStatus = syncStatus.label
)

fun InstrumentDto.toDomain(): Instrumento = Instrumento(
    id = id,
    tipo = tipo,
    marca = marca,
    modelo = modelo,
    numeroSerie = numeroSerie,
    vencimientoCalibracion = vencimientoCalibracion,
    activo = activo
)

fun Instrumento.toDto(): InstrumentDto = InstrumentDto(
    id = id,
    tipo = tipo,
    marca = marca,
    modelo = modelo,
    numeroSerie = numeroSerie,
    vencimientoCalibracion = vencimientoCalibracion,
    activo = activo
)
