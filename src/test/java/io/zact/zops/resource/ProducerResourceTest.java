package io.zact.zops.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.zact.zops.dto.NotificationEmailDTO;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ProducerResourceTest {
    @Mock
    private Emitter<NotificationEmailDTO> emailEmitter;

    @InjectMocks
    private ProducerResource producerResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmailToQueueReturnsOkWhenEmailIsSent() {
        // Configura o DTO de e-mail
        NotificationEmailDTO notificationEmailDTO = new NotificationEmailDTO();
        notificationEmailDTO.setRecipient("test@example.com");
        notificationEmailDTO.setSender("expositoresdocestete@gmail.com");
        notificationEmailDTO.setSubject("Test Subject");
        notificationEmailDTO.setBody("Test Body");

        // Executa o método sob teste
        Response response = producerResource.sendEmailToQueue(notificationEmailDTO);

        // Verifica o resultado
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("E-mail colocado na fila para envio.", response.getEntity());

        // Verifica se o Emitter foi chamado
        verify(emailEmitter, times(1)).send(notificationEmailDTO);
    }

    @Test
    void sendEmailToQueueReturnsInternalServerErrorWhenExceptionOccurs() {
        // Configura o DTO de e-mail
        NotificationEmailDTO notificationEmailDTO = new NotificationEmailDTO();
        notificationEmailDTO.setRecipient("test@example.com");
        notificationEmailDTO.setSender("expositoresdocestete@gmail.com");
        notificationEmailDTO.setSubject("Test Subject");
        notificationEmailDTO.setBody("Test Body");

        // Configura o mock para lançar uma exceção
        doThrow(new RuntimeException("Failed to send email")).when(emailEmitter).send(notificationEmailDTO);

        // Executa o método sob teste
        Response response = producerResource.sendEmailToQueue(notificationEmailDTO);

        // Verifica o resultado
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Failed to send email", response.getEntity());

        // Verifica se o Emitter foi chamado
        verify(emailEmitter, times(1)).send(notificationEmailDTO);
    }
}
