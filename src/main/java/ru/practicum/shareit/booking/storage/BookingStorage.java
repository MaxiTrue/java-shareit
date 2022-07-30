package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    //все актиыне и будущие бронирования
    List<Booking> findAllByItemIdAndStatusAndEndAfterOrderByStartAsc(
            Long itemId, StateBooking status, LocalDateTime end);


    /**
     * методы для нахождения всех объектов бронирования пользователя(для бронирующего) в зависимости от состояния
     */

    //все бронирования по id бронирующего
    List<Booking> findAllByBooker_IdOrderByStartDesc(long userId);

    //все бронирвоания по статусу и по id бронирующего
    List<Booking> findAllByStatusAndBookerIdOrderByStartDesc(StateBooking status, long userId);

    //все оконченные бронирования одного пользователя одной вещи
    List<Booking> findAllByItemIdAndBookerIdAndStatusAndEndBeforeOrderByEndDesc(
            long itemId,
            long bookerId,
            StateBooking status,
            LocalDateTime now);

    //все бронирвоания по статусу, id бронирующего и текущее время между стартом и окнчанием бронироания
    @Query(
            "select b from Booking b " +
                    "Where b.booker.id = ?1 AND b.status = ?2 and ?3 between b.start and b.end ORDER BY b.start desc")
    List<Booking> findAllByStatusAndDateBetweenStartAndEnd(long userId, StateBooking status, LocalDateTime now);

    //все бронирвоания по статусу, id бронирующего и окончанию бронирования раньше текущего времени
    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
            long userId,
            StateBooking status,
            LocalDateTime now);

    //все бронирвоания по статусу и начало бронирования позже текущего времени
    List<Booking> findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
            long userId,
            StateBooking status,
            LocalDateTime now);

    /**
     * методы для нахождения всех объектов бронирования пользователя(для влядельца вещей) в зависимости от состояния
     */

    @Query("select b From Booking b Where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findAllBookingByOwnerItems(long userId);

    @Query("select b From Booking b Where b.item.owner.id = ?1 And b.status = ?2 order by b.start desc")
    List<Booking> findAllBookingByOwnerItemsAndStatus(long userId, StateBooking status);

    @Query("select b From Booking b " +
            "Where b.item.owner.id = ?1 And b.status = ?2 And ?3 between b.start and b.end order by b.start desc")
    List<Booking> findAllBookingByOwnerItemsAndStatusAndDateBetweenStartAndEnd(
            long userId,
            StateBooking status,
            LocalDateTime now);

    @Query("select b From Booking b " +
            "Where b.item.owner.id = ?1 And b.status = ?2 And b.end < ?3 order by b.start desc")
    List<Booking> findAllBookingByOwnerItemsAndStatusAndEndBefore(
            long userId,
            StateBooking status,
            LocalDateTime now);

    @Query("select b From Booking b " +
            "Where b.item.owner.id = ?1 And b.status = ?2 And b.start > ?3 order by b.start desc")
    List<Booking> findAllBookingByOwnerItemsAndStatusAndStartAfter(
            long userId,
            StateBooking status,
            LocalDateTime now);

    /**
     * методы для нахождения последнего и следующего бронирования
     */
    @Query(value = "(Select * from bookings as b " +
            "where b.status = 'APPROVED' AND b.end_date_time < ?1 order by b.end_date_time desc limit 1)",
            nativeQuery = true)
    Optional<Booking> findLastBooking(LocalDateTime now);

    @Query(value = "(Select * from bookings as bo " +
            "where bo.status = 'APPROVED' AND bo.start_date_time > ?1 order by bo.start_date_time desc limit 1)",
            nativeQuery = true)
    Optional<Booking> findNextBooking(LocalDateTime now);


}
