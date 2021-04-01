`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 03/15/2021 11:14:31 PM
// Design Name: 
// Module Name: mult
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

module mult (
  input clock,
  input reset,
  input enable,
  
  input [15:0] a,
  input [7:0] b,
  
  output busy,
  output reg finish,
  output reg [23:0] result
);

  localparam READY = 1'b0;
  localparam WORK = 1'b1;
  
  reg state = READY;
  reg [7:0] b_temp = 8'b0;
  reg [3:0] counter = 0;
  
  wire [15:0] partial_sum;
  
  assign partial_sum = a & {16{b_temp[7]}};
  assign busy = state;
  
  always @(negedge clock)
    if (reset) begin
      state <= READY;
      result <= 0;
      finish <= 0;
      counter <= 0;
    end else begin
      case (state)
        READY:
          if (enable) begin
            state <= WORK;
            b_temp <= b;
            result <= 0;
            finish <= 0;
            counter <= 0;
          end
        WORK:
          if (counter == 8) begin
            state <= READY;
            finish <= 1;
          end else begin
            result <= (result << 1) + partial_sum;
            b_temp <= b_temp << 1;
            counter <= counter + 1;
          end
      endcase
    end
endmodule
