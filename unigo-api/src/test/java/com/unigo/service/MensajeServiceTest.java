package com.unigo.service;

import com.unigo.persistence.entities.Mensaje;
import com.unigo.persistence.repositories.MensajeRepository;
import com.unigo.service.dtos.MensajeRequest;
import com.unigo.service.dtos.MensajeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @InjectMocks
    private MensajeService mensajeService;

    @Test
    void guardarMensaje_Ok() {
        MensajeRequest request = new MensajeRequest();
        request.setIdRemitente(1);
        request.setIdViaje(2);
        request.setTexto("Hola");

        Mensaje mensajeGuardado = new Mensaje();
        mensajeGuardado.setId(10);
        mensajeGuardado.setIdRemitente(1);
        mensajeGuardado.setIdViaje(2);
        mensajeGuardado.setTexto("Hola");
        mensajeGuardado.setFechaEnvio(LocalDateTime.now());

        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensajeGuardado);

        MensajeResponse response = mensajeService.guardarMensaje(request);

        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals(1, response.getIdRemitente());
        assertEquals(2, response.getIdViaje());
        assertEquals("Hola", response.getTexto());
        assertNotNull(response.getFechaEnvio());

        verify(mensajeRepository, times(1)).save(any(Mensaje.class));
    }
}
