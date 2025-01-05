package com.team2.djavaluxury.utils.mapper;

import java.util.List;

import com.team2.djavaluxury.dto.response.BookingResponse;
import com.team2.djavaluxury.entity.Booking;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Named("ProductMapper")

public interface BookingMapper {
    BookingResponse toResponse(Booking booking);
    Booking toEntity(BookingResponse bookingResponse);
    List<BookingResponse> toResponseList(List<Booking> bookings);
}