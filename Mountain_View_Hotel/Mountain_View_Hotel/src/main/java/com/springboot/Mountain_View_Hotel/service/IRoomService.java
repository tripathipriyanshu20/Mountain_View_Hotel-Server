package com.springboot.Mountain_View_Hotel.service;

import com.springboot.Mountain_View_Hotel.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface IRoomService {
    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws SQLException;
}
