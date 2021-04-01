module demultiplexer(
    input  x,
    input  s0,
    input  s1,
    output y0,
    output y1,
    output y2,
    output y3
    );

    wire not_s0, not_s1, not_x;
    wire p0, p1, p2, p3;
    wire not_p0, not_p1, not_p2, not_p3;

    nor(not_s0, s0, s0);
    nor(not_s1, s1, s1);
    nor(not_x,   x,  x);

    // 2-to-4 decoder machinery
    nor(p0,     s0,     s1);
    nor(p1, not_s0,     s1);
    nor(p2,     s0, not_s1);
    nor(p3, not_s0, not_s1);

    nor(not_p0, p0, p0);
    nor(not_p1, p1, p1);
    nor(not_p2, p2, p2);
    nor(not_p3, p3, p3);

    nor(y0, not_x, not_p0);
    nor(y1, not_x, not_p1);
    nor(y2, not_x, not_p2);
    nor(y3, not_x, not_p3);

endmodule
