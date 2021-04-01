module demultiplexer_tb;

    reg x_in, s0_in, s1_in;
    wire y0_out, y1_out, y2_out, y3_out;

    demultiplexer demul_l(
        .x(x_in),
        .s0(s0_in),
        .s1(s1_in),
        .y0(y0_out),
        .y1(y1_out),
        .y2(y2_out),
        .y3(y3_out)
    );

    integer i;
    reg [2:0] test_val;
    reg [1:0] expected_gate;
    reg [1:0] sum;

    initial begin
        for (i = 0; i < 8; i = i+1) begin
            test_val = i;
            x_in = test_val[0];
            s0_in = test_val[1];
            s1_in = test_val[2];
            expected_gate = test_val[2] * 2 + test_val[1];

            #10

            sum = y0_out + y1_out + y2_out + y3_out;
            $display("State: x=%b, select=%b%b", x_in, s1_in, s0_in);
            if (x_in == 0) begin
                $display("No signals are expected");
            end
            if (y0_out == 1) begin
                if (x_in == 1 && expected_gate == 0) begin
                    $display("Success: Gate 0 set correctly");
                end else begin
                    $display("Failure: Gate 0 set incorrectly");
                end
            end
            if (y1_out == 1) begin
                if (x_in == 1 && expected_gate == 1) begin
                    $display("Success: Gate 1 set correctly");
                end else begin
                    $display("Failure: Gate 1 set incorrectly");
                end
            end
            if (y2_out == 1) begin
                if (x_in == 1 && expected_gate == 2) begin
                    $display("Success: Gate 2 set correctly");
                end else begin
                    $display("Failure: Gate 2 set incorrectly");
                end
            end
            if (y3_out == 1) begin
                if (x_in == 1 && expected_gate == 3) begin
                    $display("Success: Gate 3 set correctly");
                end else begin
                    $display("Failure: Gate 3 set incorrectly");
                end
            end
            if (x_in != sum) begin
                $display("Failure: Number of output signals does not match the input signal");
            end
        end
        #10 $stop;
    end
endmodule
