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
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    //все бронирвоания по статусу и по id бронирующего
    List<Booking> findAllByStatusAndBookerIdOrderByStartDesc(StateBooking status, long userId);

    //все оконченные бронирования одного пользователя одной вещи
    List<Booking> findAllByItemIdAndBookerIdAndStatusAndEndBeforeOrderByEndDesc(
            long itemId,
            long bookerId,
            StateBooking status,
            LocalDateTime now);

    //все бронирвоания по id бронирующего и текущее время между стартом и окнчанием бронироания
    @Query(
            "SELECT b FROM Booking b " +
                    "WHERE b.booker.id = :userId AND :now BETWEEN b.start AND b.end " +
                    "ORDER BY b.start DESC")
    List<Booking> findAllByDateBetweenStartAndEnd(long userId, LocalDateTime now);

    //все бронирвоания по статусу, id бронирующего и окончанию бронирования раньше текущего времени
    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
            long userId,
            StateBooking status,
            LocalDateTime now);

    //все бронирвоания по статусу и начало бронирования позже текущего времени
    List<Booking> findAllByBookerIdAndStatusNotAndStartAfterOrderByStartDesc(
            long userId,
            StateBooking status,
            LocalDateTime now);

    /**
     * методы для нахождения всех объектов бронирования пользователя(для влядельца вещей) в зависимости от состояния
     */

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId ORDER BY b.start DESC")
    List<Booking> findAllBookingByOwnerItems(long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllBookingByOwnerItemsAndStatus(long userId, StateBooking status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND :now BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingByOwnerItemsAndStatusAndDateBetweenStartAndEnd(long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND b.status = :status AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findAllBookingByOwnerItemsAndStatusAndEndBefore(
            long userId,
            StateBooking status,
            LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND b.status <> :status AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findAllBookingByOwnerItemsAndStatusAndStartAfter(
            long userId,
            StateBooking status,
            LocalDateTime now);

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
