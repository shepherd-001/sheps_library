package com.shepherd.shepslibrary.mapper;

import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;
import com.shepherd.shepslibrary.data.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralConfig.class)
public interface ReservationMapper {

    @Mapping(target = "userId", source = "reservation.user.id")
    @Mapping(target = "reservationId", source = "reservation.id")
    @Mapping(target = "bookId", source = "reservation.book.id")
    @Mapping(target = "reservationDate", source = "reservation.reservationDate")
    ReservationResponse mapToReservationResponse(Reservation reservation);
}
