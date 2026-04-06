package com.shepherd.shepslibrary.mapper;

import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;
import com.shepherd.shepslibrary.data.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralConfig.class)
public interface ReservationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "reservationDate", source = "reservationDateTime")
    ReservationResponse mapToReservationResponse(Reservation reservation);
}
