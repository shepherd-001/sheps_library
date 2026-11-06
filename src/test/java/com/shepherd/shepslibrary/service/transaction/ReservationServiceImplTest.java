package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.model.*;
import com.shepherd.shepslibrary.data.repository.*;
import com.shepherd.shepslibrary.mapper.ReservationMapper;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {
    @Test void memberCannotReadAnotherMembersReservation() {
        ReservationRepository repository = mock(ReservationRepository.class);
        ReservationMapper mapper = mock(ReservationMapper.class);
        ReservationServiceImpl service = new ReservationServiceImpl(mock(BookRepository.class), repository,
                mock(MailNotificationService.class), mapper);
        UUID id = UUID.randomUUID();
        Reservation reservation = Reservation.builder()
                .user(User.builder().id(UUID.randomUUID()).build()).build();
        when(repository.findById(id)).thenReturn(Optional.of(reservation));
        AuthenticatedUser stranger = AuthenticatedUser.builder()
                .user(User.builder().id(UUID.randomUUID()).build()).build();
        assertThrows(AccessDeniedException.class, () -> service.getReservationById(id, stranger));
        verifyNoInteractions(mapper);
    }
}
