package com.bobisonfire.lab4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserRepository users;

    @Autowired
    public ApiController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/user/register")
    public long registerUser(
            @RequestParam String userName,
            @RequestParam(required = false) String password)
    {
        if (users.existsByUserName(userName))
            return -1;
        if (password == null)
            return 0;

        User user = new User(userName, password);
        users.save(user);
        return user.getId();
    }

    @GetMapping("/user/authorize")
    public long authorizeUser(
            @RequestParam String userName,
            @RequestParam String password)
    {
        Optional<User> userOpt = users.findByUserName(userName);
        if (!userOpt.isPresent())
            return -1;

        User user = userOpt.get();
        if (!password.equals(user.getPassword()))
            return 0;

        // todo add any way of proving that user authorized from that machine
        return user.getId();
    }

    @GetMapping("/history/get/{id}")
    public List<HistoryNode> getHistory(@PathVariable long id) {
        // todo user authenticity by id
        return users.findById(id).map(User::getUserHistory).orElse(null);
    }

    @GetMapping("/history/add/{id}")
    public HistoryNode addHistoryNode(@PathVariable long id,
                                      @RequestParam BigDecimal x,
                                      @RequestParam BigDecimal y,
                                      @RequestParam BigDecimal r)
    {
        int result = calculateHit(x, y, r) ? 1 : 0;
        HistoryNode historyNode = new HistoryNode(x, y, r, result);
        Optional<User> userOpt = users.findById(id);

        if (!userOpt.isPresent())
            return null;

        // todo user authenticity here
        userOpt.get().getUserHistory().add(historyNode);
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
}
