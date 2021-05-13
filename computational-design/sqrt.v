`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 12.05.2021 19:48:19
// Design Name: 
// Module Name: root
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


module sqrt(
    input [31:0] a,
    output [31:0] res
    );
    
    wire [31:0] num_1, res_1, check_1;
    assign num_1 = a;
    assign res_1 = 0;
    assign check_1 = {32{ num_1 >= (1 << 30 + res_1) }};

    wire [31:0] num_2, res_2, check_2;
    assign num_2 = num_1 - (check_1 & ((1 << 30) + res_1));
    assign res_2 = (res_1 >> 1) + (check_1 & (1 << 30));
    assign check_2 = {32{ num_2 >= ((1 << 28) + res_2) }};

    wire [31:0] num_3, res_3, check_3;
    assign num_3 = num_2 - (check_2 & ((1 << 28) + res_2));
    assign res_3 = (res_2 >> 1) + (check_2 & (1 << 28));
    assign check_3 = {32{ num_3 >= ((1 << 26) + res_3) }};

    wire [31:0] num_4, res_4, check_4;
    assign num_4 = num_3 - (check_3 & ((1 << 26) + res_3));
    assign res_4 = (res_3 >> 1) + (check_3 & (1 << 26));
    assign check_4 = {32{ num_4 >= ((1 << 24) + res_4) }};

    wire [31:0] num_5, res_5, check_5;
    assign num_5 = num_4 - (check_4 & ((1 << 24) + res_4));
    assign res_5 = (res_4 >> 1) + (check_4 & (1 << 24));
    assign check_5 = {32{ num_5 >= ((1 << 22) + res_5) }};

    wire [31:0] num_6, res_6, check_6;
    assign num_6 = num_5 - (check_5 & ((1 << 22) + res_5));
    assign res_6 = (res_5 >> 1) + (check_5 & (1 << 22));
    assign check_6 = {32{ num_6 >= ((1 << 20) + res_6) }};

    wire [31:0] num_7, res_7, check_7;
    assign num_7 = num_6 - (check_6 & ((1 << 20) + res_6));
    assign res_7 = (res_6 >> 1) + (check_6 & (1 << 20));
    assign check_7 = {32{ num_7 >= ((1 << 18) + res_7) }};

    wire [31:0] num_8, res_8, check_8;
    assign num_8 = num_7 - (check_7 & ((1 << 18) + res_7));
    assign res_8 = (res_7 >> 1) + (check_7 & (1 << 18));
    assign check_8 = {32{ num_8 >= ((1 << 16) + res_8) }};

    wire [31:0] num_9, res_9, check_9;
    assign num_9 = num_8 - (check_8 & ((1 << 16) + res_8));
    assign res_9 = (res_8 >> 1) + (check_8 & (1 << 16));
    assign check_9 = {32{ num_9 >= ((1 << 14) + res_9) }};

    wire [31:0] num_10, res_10, check_10;
    assign num_10 = num_9 - (check_9 & ((1 << 14) + res_9));
    assign res_10 = (res_9 >> 1) + (check_9 & (1 << 14));
    assign check_10 = {32{ num_10 >= ((1 << 12) + res_10) }};

    wire [31:0] num_11, res_11, check_11;
    assign num_11 = num_10 - (check_10 & ((1 << 12) + res_10));
    assign res_11 = (res_10 >> 1) + (check_10 & (1 << 12));
    assign check_11 = {32{ num_11 >= ((1 << 10) + res_11) }};

    wire [31:0] num_12, res_12, check_12;
    assign num_12 = num_11 - (check_11 & ((1 << 10) + res_11));
    assign res_12 = (res_11 >> 1) + (check_11 & (1 << 10));
    assign check_12 = {32{ num_12 >= ((1 << 8) + res_12) }};

    wire [31:0] num_13, res_13, check_13;
    assign num_13 = num_12 - (check_12 & ((1 << 8) + res_12));
    assign res_13 = (res_12 >> 1) + (check_12 & (1 << 8));
    assign check_13 = {32{ num_13 >= ((1 << 6) + res_13) }};

    wire [31:0] num_14, res_14, check_14;
    assign num_14 = num_13 - (check_13 & ((1 << 6) + res_13));
    assign res_14 = (res_13 >> 1) + (check_13 & (1 << 6));
    assign check_14 = {32{ num_14 >= ((1 << 4) + res_14) }};

    wire [31:0] num_15, res_15, check_15;
    assign num_15 = num_14 - (check_14 & ((1 << 4) + res_14));
    assign res_15 = (res_14 >> 1) + (check_14 & (1 << 4));
    assign check_15 = {32{ num_15 >= ((1 << 2) + res_15) }};

    wire [31:0] num_16, res_16, check_16;
    assign num_16 = num_15 - (check_15 & ((1 << 2) + res_15));
    assign res_16 = (res_15 >> 1) + (check_15 & (1 << 2));
    assign check_16 = {32{ num_16 >= res_16 }};

    wire [31:0] num_final, res_final;
    assign num_final = num_16 - (check_16 & (res_16 + 1));
    assign res_final = (res_16 >> 1) + (check_16 & 1);

    assign res = res_final;
endmodule
