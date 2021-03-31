`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 03/15/2021 11:23:18 PM
// Design Name: 
// Module Name: cube_tb
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


module cube_tb();
    reg clk;
    reg rst;
    reg [7:0] a;
    reg start;
    wire [1:0] cube_busy_o;
    wire [23:0] result;
    cube cube1(
        .clk_i(clk),
        .rst_i(rst),
        .a_bi(a),
        .start_i(start),
        .busy_o(cube_busy_o),
        .y_bo(result)
    );
    initial begin
        clk=1; rst=1;
        #1
        clk=0; rst=0;
        #1
        a=8'd128; clk=1; start=1;
        #1 clk=0; #1 clk=1; start=0;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
        #1 clk=0; #1 clk=1;
    end
endmodule
