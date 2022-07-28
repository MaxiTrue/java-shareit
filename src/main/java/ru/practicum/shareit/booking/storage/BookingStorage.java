package ru.practicum.shareit.booking.storage;

import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByItemIdAndStatusAndEndAfterOrderByStartAsc(
            Long item_id,
            StateBooking status,
            LocalDateTime end);

    /**методы для нахождения всех объектов бронирования пользователя( для бронирующего) в зависимости от состояния*/

    //все бронирования по id бронирующего
    Collection<Booking> findAllByBooker_IdOrderByStartDesc(long userId);

    //все бронирвоания по статусу и по id бронирующего
    Collection<Booking> findAllByStatusAndBookerIdOrderByStartDesc(StateBooking status, long userId);

    //все бронирвоания по статусу, id бронирующего и текущее время между стартом и окнчанием бронироания
    @Query(
            "select b from Booking b " +
                    "Where b.booker.id = ?1 AND b.status = ?2 and ?3 between b.start and b.end ORDER BY b.start desc")
    Collection<Booking> findAllByStatusAndDateBetweenStartAndEnd(long userId, StateBooking status, LocalDateTime now);

    //все бронирвоания по статусу, id бронирующего и окончанию бронирования раньше текущего времени
    Collection<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
            long userId,
            StateBooking status,
            LocalDateTime now);

    //все бронирвоания по статусу и начало бронирования позже текущего времени
    Collection<Booking> findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
            long userId,
            StateBooking status,
            LocalDateTime now);

    /**методы для нахождения всех объектов бронирования пользователя(для влядельца вещей) в зависимости от состояния*/

    @Query("select b From Booking b Where b.item.owner.id = ?1 order by b.start desc")
    Collection<Booking> findAllBookingByOwnerItems(long userId);

    @Query("select b From Booking b Where b.item.owner.id = ?1 And b.status = ?2 order by b.start desc")
    Collection<Booking> findAllBookingByOwnerItemsAndStatus(long userId, StateBooking status);

    @Query("select b From Booking b " +
            "Where b.item.owner.id = ?1 And b.status = ?2 And ?3 between b.start and b.end order by b.start desc")
    Collection<Booking> findAllBookingByOwnerItemsAndStatusAndDateBetweenStartAndEnd(
            long userId,
            StateBooking status,
            LocalDateTime now);

    @Query("select b From Booking b " +
            "Where b.item.owner.id = ?1 And b.status = ?2 And b.end < ?3 order by b.start desc")
    Collection<Booking> findAllBookingByOwnerItemsAndStatusAndEndBefore(
            long userId,
            StateBooking status,
            LocalDateTime now);

    @Query("select b From Booking b " +
            "Where b.item.owner.id = ?1 And b.status = ?2 And b.start > ?3 order by b.start desc")
    Collection<Booking> findAllBookingByOwnerItemsAndStatusAndStartAfter(
            long userId,
            StateBooking status,
            LocalDateTime now);

}
