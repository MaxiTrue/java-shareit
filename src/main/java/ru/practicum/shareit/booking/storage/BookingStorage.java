package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            Long itemId,
            StateBooking status,
            LocalDateTime end);


    /**
     * методы для нахождения всех объектов бронирования пользователя(для бронирующего) в зависимости от состояния
     */

    //все бронирования по id бронирующего
    Page<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    //все бронирвоания по статусу и по id бронирующего
    Page<Booking> findAllByStatusAndBookerIdOrderByStartDesc(StateBooking status, long userId, Pageable pageable);

    //все оконченные бронирования одного пользователя одной вещи
    List<Booking> findAllByItemIdAndBookerIdAndStatusAndEndBeforeOrderByEndDesc(
            long itemId,
            long bookerId,
            StateBooking status,
            LocalDateTime now);

    //все бронирвоания по id бронирующего и текущее время между стартом и окончанием бронироания
    @Query("SELECT b FROM Booking b " +
                    "WHERE b.booker.id = :userId AND :now BETWEEN b.start AND b.end " +
                    "ORDER BY b.start DESC")
    Page<Booking> findAllByBookerIdAndDateBetweenStartAndEnd(long userId, LocalDateTime now, Pageable pageable);

    //все бронирвоания по статусу, id бронирующего и окончанию бронирования раньше текущего времени
    Page<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
            long userId,
            StateBooking status,
            LocalDateTime now,
            Pageable pageable);

    //все бронирвоания по статусу и начало бронирования позже текущего времени
    Page<Booking> findAllByBookerIdAndStatusNotAndStartAfterOrderByStartDesc(
            long userId,
            StateBooking status,
            LocalDateTime now,
            Pageable pageable);

    /**
     * методы для нахождения всех объектов бронирования пользователя(для владельца вещей) в зависимости от состояния
     */

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long userId, StateBooking status, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND :now BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllBookingByOwnerItemsAndDateBetweenStartAndEnd(
            long userId,
            LocalDateTime now,
            Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND b.status = :status AND b.end < :now ORDER BY b.start DESC")
    Page<Booking> findAllBookingByOwnerItemsAndStatusAndEndBefore(
            long userId,
            StateBooking status,
            LocalDateTime now,
            Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND b.status <> :status AND b.start > :now ORDER BY b.start DESC")
    Page<Booking> findAllBookingByOwnerItemsAndStatusAndStartAfter(
            long userId,
            StateBooking status,
            LocalDateTime now,
            Pageable pageable);

    /**
     * методы для нахождения последнего и следующего бронирования
     */
    @Query(value = "(SELECT * FROM bookings AS b " +
            "WHERE b.item_id = :itemId AND b.end_date_time < :now ORDER BY b.end_date_time DESC limit 1)",
            nativeQuery = true)
    Optional<Booking> findLastBooking(long itemId, LocalDateTime now);

    @Query(value = "(SELECT * FROM bookings AS b " +
            "WHERE b.item_id = :itemId AND b.start_date_time > :now ORDER BY b.start_date_time DESC limit 1)",
            nativeQuery = true)
    Optional<Booking> findNextBooking(long itemId, LocalDateTime now);

}
