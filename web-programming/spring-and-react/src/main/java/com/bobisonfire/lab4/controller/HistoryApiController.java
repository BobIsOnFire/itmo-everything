package com.bobisonfire.lab4.controller;

import com.bobisonfire.lab4.data.HistoryEntity;
import com.bobisonfire.lab4.data.User;
import com.bobisonfire.lab4.data.UserRepository;
import com.bobisonfire.lab4.data.HistoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/history")
public class HistoryApiController {
    private final UserRepository users;

    @Autowired
    public HistoryApiController(UserRepository users) {
        this.users = users;
    }

    @GetMapping
    public List<HistoryDto> getHistory(Principal principal) {
        User user = users.findByUsername(principal.getName());

        return user.getUserHistory().stream()
                .map(HistoryApiController::dtoFromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public HistoryDto addHistoryNode(@Valid HistoryDto historyDto, Principal principal) {
        HistoryEntity historyEntity = entityFromDto(historyDto);
        User user = users.findByUsername(principal.getName());

        user.getUserHistory().add(historyEntity);
        users.save(user);

        historyDto.setResult( historyEntity.getResult() == 1 );
        return historyDto;
    }

    private static boolean calculateHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        if ( x.compareTo(BigDecimal.ZERO) < 0 ) { // x < 0
            if ( y.compareTo(BigDecimal.ZERO) > 0 ) // y > 0
                return false;

            return x.compareTo(r.negate()) >= 0 && y.compareTo(r.negate()) >= 0; // x >= -r and y >= -r
        }

        if ( y.compareTo(BigDecimal.ZERO) < 0 ) // y < 0
            return y.compareTo( x.multiply(BigDecimal.valueOf(2)).subtract(r) ) >= 0; // y >= 2 * x - r

        return x.pow(2).add( y.pow(2) ).compareTo( r.pow(2).divide( BigDecimal.valueOf(4), BigDecimal.ROUND_HALF_UP ) ) <= 0; // x^2 + y^2 <= r^2 / 4
    }

    private static HistoryEntity entityFromDto(HistoryDto dto) {
        HistoryEntity entity = new HistoryEntity();

        BigDecimal x = new BigDecimal(dto.getX());
        BigDecimal y = new BigDecimal(dto.getY());
        BigDecimal r =  new BigDecimal(dto.getR());

        entity.setX(x);
        entity.setY(y);
        entity.setR(r);
        entity.setResult( calculateHit(x, y, r) ? 1 : 0 );

        return entity;
    }

    private static HistoryDto dtoFromEntity(HistoryEntity entity) {
        HistoryDto dto = new HistoryDto();

        dto.setX( entity.getX().toPlainString() );
        dto.setY( entity.getY().toPlainString() );
        dto.setR( entity.getR().toPlainString() );
        dto.setResult( entity.getResult() == 1 );

        return dto;
    }
}
