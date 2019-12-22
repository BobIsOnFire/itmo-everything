package com.bobisonfire.lab4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@RestController
@RequestMapping(value = "/api", method = RequestMethod.GET)
public class ApiController {
    private final UserRepository users;

    @Autowired
    public ApiController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/user/register")
    public long registerUser(
            @RequestParam String userName,
            @RequestParam(required = false) String password,
            ServletRequest request) // returns -1 if user does not exist and 0 if no password is given
    {
        if (users.existsByUserName(userName))
            return -1;
        if (password == null || password.isEmpty())
            return 0;

        User user = new User( userName, password, request.getRemoteAddr() );
        users.save(user);
        return user.getId();
    }

    @GetMapping("/user/authorize")
    public long authorizeUser(
            @RequestParam String userName,
            @RequestParam String password,
            ServletRequest request)
    {
        Optional<User> userOpt = users.findByUserName(userName);
        if (!userOpt.isPresent())
            return -1;

        User user = userOpt.get();
        if (!password.equals(user.getPassword()))
            return 0;

        user.setLastAddress( request.getRemoteAddr() );
        users.save(user);
        return user.getId();
    }

    @GetMapping("/history/get/{id}")
    public List<HistoryNode> getHistory(
            @PathVariable long id,
            ServletRequest request)
    {
        Optional<User> userOpt = users.findById(id);
        if (!userOpt.isPresent())
            return null;

        User user = userOpt.get();
        if (!user.getLastAddress().equals( request.getRemoteAddr() ))
            return null;

        return user.getUserHistory();
    }

    @GetMapping("/history/add/{id}")
    public HistoryNode addHistoryNode(
            @PathVariable long id,
            @RequestParam String xQuery,
            @RequestParam String yQuery,
            @RequestParam String rQuery,
            ServletRequest request) // returns null when the user needs to authorize again, coordinate=null if it is not valid
    {
        BigDecimal x = convert(xQuery.trim(),
                num -> num.compareTo(BigDecimal.valueOf(-3)) >= 0 && num.compareTo(BigDecimal.valueOf(5)) <= 0);
        BigDecimal y = convert(yQuery.trim(),
                num -> num.compareTo(BigDecimal.valueOf(-3)) > 0 && num.compareTo(BigDecimal.valueOf(3)) < 0);
        BigDecimal r = convert(rQuery.trim(),
                num -> num.compareTo(BigDecimal.valueOf(-3)) >= 0 && num.compareTo(BigDecimal.valueOf(5)) <= 0);

        HistoryNode historyNode = new HistoryNode(x, y, r);
        if (x == null || y == null || r == null) return historyNode;
        historyNode.setResult( calculateHit(x, y, r) ? 1 : 0 );

        Optional<User> userOpt = users.findById(id);
        if (!userOpt.isPresent())
            return null;

        User user = userOpt.get();

        if (!user.getLastAddress().equals( request.getRemoteAddr() ))
            return null;

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

        return x.pow(2).add( y.pow(2) ).compareTo( r.pow(2) ) <= 0; // x^2 + y^2 <= r^2
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
