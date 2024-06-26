package com.springboot.Mountain_View_Hotel.repository;

import com.springboot.Mountain_View_Hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
