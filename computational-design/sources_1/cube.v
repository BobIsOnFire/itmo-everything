`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 03/15/2021 11:15:52 PM
// Design Name: 
// Module Name: cube
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module cube (
  input clk_i,
  input rst_i,
  input [7:0] a_bi,
  input start_i,
  output [1:0] busy_o,
  output reg [23:0] y_bo 
);
  localparam IDLE = 2'h0;
  localparam FIRST_WORK = 2'h1;
  localparam SECOND_WORK = 2'h2;
  reg [15:0] a;
  reg [7:0] b;
  reg start;
  wire mult_busy;
  wire [23:0] mult_result;
  mult mult1 (
    .clk_i(clk_i),
    .rst_i(rst_i),
    .a_bi(a),
    .b_bi(b),
    .start_i(start),
    .busy_o(mult_busy),
    .y_bo(mult_result)
  );
  reg [1:0] state;
  assign busy_o = state;
  always @(posedge clk_i)
    if (rst_i) begin
      y_bo <= 0;
      state <= IDLE;
    end else begin
      if (state == IDLE && start_i) begin
        if (a_bi == 0) begin
            y_bo <= 0;
        end else begin
            state <= FIRST_WORK;
            a <= {8'b0, a_bi};
            b <= a_bi;
            start <= 1;
        end
      end  
    end
  always @(mult_result) 
    begin
        start <= 0;
        case (state)
            FIRST_WORK:
                begin
                    state <= SECOND_WORK;
                    a <= mult_result[15:0];
                    start <= 1;
                end  
            SECOND_WORK:
                begin
                    state <= IDLE;
                    y_bo <= mult_result;
                end    
        endcase
    end    
endmodule
