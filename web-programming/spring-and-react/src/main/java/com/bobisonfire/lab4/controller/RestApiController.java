package com.bobisonfire.lab4.controller;

import com.bobisonfire.lab4.data.HistoryNode;
import com.bobisonfire.lab4.data.User;
import com.bobisonfire.lab4.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.function.Predicate;

@RestController
@RequestMapping(value = "/api", method = RequestMethod.GET)
public class RestApiController {
    private final UserRepository users;

    @Autowired
    public RestApiController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/history/get")
    public List<HistoryNode> getHistory(
            Principal principal)
    {
        User user = users.findByUsername(principal.getName());
        if (user == null) return null;

        return user.getUserHistory();
    }

    @GetMapping("/history/add")
    public HistoryNode addHistoryNode(
            @RequestParam String xQuery,
            @RequestParam String yQuery,
            @RequestParam String rQuery,
            Principal principal) // returns null when the user needs to authorize again, coordinate=null if it is not valid
    {
        BigDecimal x = convert(xQuery.trim(),
                num -> num.compareTo(BigDecimal.valueOf(-3)) >= 0 && num.compareTo(BigDecimal.valueOf(5)) <= 0);
        BigDecimal y = convert(yQuery.trim(),
                num -> num.compareTo(BigDecimal.valueOf(-3)) > 0 && num.compareTo(BigDecimal.valueOf(3)) < 0);
        BigDecimal r = convert(rQuery.trim(),
                num -> num.compareTo(BigDecimal.valueOf(-3)) >= 0 && num.compareTo(BigDecimal.valueOf(5)) <= 0);

        HistoryNode historyNode = new HistoryNode(x, y, r);
        if (x == null || y == null || r == null) return historyNode;
        historyNode.setResult( calculateHit(x, y, r.abs()) ? 1 : 0 );

        User user = users.findByUsername(principal.getName());
        if (user == null) return null;

        user.getUserHistory().add(historyNode);
        users.save(user);
        return historyNode;
    }

    private boolean calculateHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        if ( x.compareTo(BigDecimal.ZERO) < 0 ) { // x < 0
            if ( y.compareTo(BigDecimal.ZERO) > 0 ) // y > 0
                return false;

            return x.compareTo(r.negate()) >= 0 && y.compareTo(r.negate()) >= 0; // x >= -r and y >= -r
        }

        if ( y.compareTo(BigDecimal.ZERO) < 0 ) // y < 0
            return y.compareTo( x.multiply(BigDecimal.valueOf(2)).subtract(r) ) >= 0; // y >= 2 * x - r

        return x.pow(2).add( y.pow(2) ).compareTo( r.pow(2).divide( BigDecimal.valueOf(4), BigDecimal.ROUND_HALF_UP ) ) <= 0; // x^2 + y^2 <= r^2 / 4
    }

    private BigDecimal convert(String query, Predicate<BigDecimal> rangePredicate) {
        if (query.isEmpty()) return null;
        try {
            BigDecimal number = new BigDecimal(query);
            if (!rangePredicate.test(number))
                return null;
            return number;
        } catch (NumberFormatException exc) {
            return null;
        }
    }
}
