`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 03/17/2021 08:36:49 PM
// Design Name: 
// Module Name: function
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

module main_function(
  input clock,
  input reset,
  input enable,
  
  input [7:0] a,
  input [7:0] b,
  
  output busy,
  output reg finish,
  output reg [23:0] result
);

  localparam READY = 2'd0;
  localparam SQUARE = 2'd1;
  localparam CUBE = 2'd2;
  localparam MUL = 2'd3;

  reg [1:0] state = READY;
  assign busy = state != READY;

  reg [15:0] a_mult = 16'b0;
  reg [7:0] b_mult = 8'b0;
  reg reset_mult = 0;
  reg enable_mult = 0;
  
  wire [23:0] result_mult;
  wire busy_mult;
  wire finish_mult;
  
  mult mult1 (
    .clock(clock),
    .reset(reset_mult),
    .enable(enable_mult),
    
    .a(a_mult),
    .b(b_mult),
    
    .busy(busy_mult),
    .finish(finish_mult),
    .result(result_mult)
  );

  always @(posedge clock)
    if (reset) begin
      state <= READY;
      finish <= 0;
      result <= 0;
    end else begin
      case (state)
        READY:
          if (enable) begin
            finish <= 0;
            result <= 0;
            state <= SQUARE;
            a_mult <= {8'b0, a};
            b_mult <= a;
            enable_mult <= 1;
          end
        
        SQUARE:
          if (finish_mult) begin
            state <= CUBE;
            a_mult <= result_mult;
            b_mult <= a;
            enable_mult <= 1;
          end
        
        CUBE:
          if (finish_mult) begin
            result <= result_mult;
            state <= MUL;
            a_mult <= {8'b0, a};
            b_mult <= b;
            enable_mult <= 1;
          end
        
        MUL:
          if (finish_mult) begin
            result <= result + result_mult;
            finish <= 1;
            state <= READY;
          end
      endcase
    end
  
  always @(posedge busy_mult) begin
    enable_mult <= 0;
  end
endmodule
